package com.wangwenjun.concurrency.chapter13;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xwder
 * @date 2022/7/8 9:31
 **/
public class SimpleThreadPool2 extends Thread {

    /**
     * 线程创建分配的序号
     */
    private static volatile int THREAD_SEQ = 0;

    /**
     * 任务队列大小
     */
    private final int queueSize;

    /**
     * 任务队列最大等待执行的任务数量
     */
    private final static int DEFAULT_TASK_QUEUE_SIZE = 2000;

    /**
     * 任务队列
     */
    private final static LinkedList<Runnable> TASK_QUEUE = new LinkedList<>();

    /**
     * 自定义线程名称前缀
     */
    private final static String THREAD_PREFIX = "SIMPLE_THREAD_POOL-";

    /**
     * 自定义线程组
     */
    private final static ThreadGroup GROUP = new ThreadGroup("Pool_Group");

    /**
     * 线程池
     */
    private final static List<WorkerTask> THREAD_QUEUE = new ArrayList<>();

    /**
     * 任务队列提交任务拒绝策略
     */
    private final DiscardPolicy discardPolicy;

    /**
     * 线程池销毁状态
     */
    private volatile boolean destroy = false;

    /**
     * 线程池初始线程数量
     */
    private final int min;

    /**
     * 线程池最大线程数量
     */
    private final int max;

    /**
     * 线程池活跃数量值 介于min和max之间
     */
    private final int active;

    /**
     * 线程池中存在的线程数量
     */
    private int size;

    /**
     * 默认任务队列提交拒绝策略
     */
    public final static DiscardPolicy DEFAULT_DISCARD_POLICY = () -> {
        throw new DiscardException("Discard This Task.");
    };


    /**
     * 默认线程池构造方法
     */
    public SimpleThreadPool2() {
        this(4, 8, 12, DEFAULT_TASK_QUEUE_SIZE, DEFAULT_DISCARD_POLICY);
    }

    /**
     * 线程池构造方法
     *
     * @param min           线程池初始线程数量
     * @param active        线程池活跃数量值 介于min和max之间
     * @param max           线程池最大线程数量
     * @param queueSize     任务队列大小
     * @param discardPolicy 任务队列提交拒绝策略
     */
    public SimpleThreadPool2(int min, int active, int max, int queueSize, DiscardPolicy discardPolicy) {
        this.min = min;
        this.active = active;
        this.max = max;
        this.queueSize = queueSize;
        this.discardPolicy = discardPolicy;
        init();
    }

    /**
     * 初始化线程池
     */
    private void init() {
        for (int i = 0; i < this.min; i++) {
            createWorkTask();
        }
        this.size = min;
        this.start();
    }

    /**
     * 提交任务到任务队列
     *
     * @param runnable 任务
     */
    public void submit(Runnable runnable) {
        if (destroy) {
            throw new IllegalStateException("The thread pool already destroy and not allow submit task.");
        }

        synchronized (TASK_QUEUE) {
            if (TASK_QUEUE.size() > queueSize) {
                discardPolicy.discard();
            }
            TASK_QUEUE.addLast(runnable);
            TASK_QUEUE.notifyAll();
        }
    }


    @Override
    public void run() {
        while (!destroy) {
            System.out.printf("Pool#Min:%d,Active:%d,Max:%d,Current:%d,QueueSize:%d\n",
                    this.min, this.active, this.max, this.size, TASK_QUEUE.size());
            try {
                Thread.sleep(5_000L);
                // 任务队列数量大于active并且当前线程池中的线程数量小于active 扩展线程池线程数量到active
                if (TASK_QUEUE.size() > active && size < active) {
                    for (int i = size; i < active; i++) {
                        createWorkTask();
                    }
                    System.out.println("The pool incremented to active.");
                    size = active;
                } else if (TASK_QUEUE.size() > max && size < max) {
                    // 任务队列数量大于线程池最大线程数量 且当前线程池中的线程数量小于最大线程数量
                    // 扩展线程池线程数量到最大线程数量
                    for (int i = size; i < max; i++) {
                        createWorkTask();
                    }
                    System.out.println("The pool incremented to max.");
                    size = max;
                }

                // 当任务队列为空的时候 将线程池中存在的线程数量消减至active数量
                synchronized (THREAD_QUEUE) {
                    if (TASK_QUEUE.isEmpty() && size > active) {
                        System.out.println("=========Reduce========");
                        int releaseSize = size - active;
                        for (Iterator<WorkerTask> it = THREAD_QUEUE.iterator(); it.hasNext(); ) {
                            if (releaseSize <= 0) {
                                break;
                            }

                            WorkerTask task = it.next();
                            task.close();
                            task.interrupt();
                            it.remove();
                            releaseSize--;
                        }
                        size = active;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建任务执行线程
     */
    private void createWorkTask() {
        WorkerTask task = new WorkerTask(GROUP, THREAD_PREFIX + (THREAD_SEQ++));
        task.start();
        THREAD_QUEUE.add(task);
    }

    /**
     * 停止线程池
     *
     * @throws InterruptedException 中断异常
     */
    public void shutdown() throws InterruptedException {
        // 等待任务队列执行完毕
        while (!TASK_QUEUE.isEmpty()) {
            Thread.sleep(50);
        }
        synchronized (THREAD_QUEUE) {
            int initVal = THREAD_QUEUE.size();
            while (initVal > 0) {
                for (WorkerTask task : THREAD_QUEUE) {
                    if (task.getTaskState() == TaskStateEnum.BLOCKED) {
                        task.interrupt();
                        task.close();
                        initVal--;
                    } else {
                        Thread.sleep(10);
                    }
                }
            }
        }
        System.out.println("thread pool group active count size: "+GROUP.activeCount());
        this.destroy = true;
        System.out.println("The thread pool disposed.");
    }

    private enum TaskStateEnum {
        /**
         * 空闲
         */
        FREE,
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 锁住
         */
        BLOCKED,
        /**
         * 死亡
         */
        DEAD
    }


    /**
     * 拒绝策略接口
     **/
    public interface DiscardPolicy {

        /**
         * 拒绝策略实现
         *
         * @throws DiscardException
         */
        void discard() throws DiscardException;
    }

    /**
     * 拒绝策略异常
     **/
    public static class DiscardException extends RuntimeException {

        public DiscardException(String message) {
            super(message);
        }
    }

    /**
     * 自定义线程池中的线程
     */
    private static class WorkerTask extends Thread {

        /**
         * 线默认程状态
         */
        private volatile TaskStateEnum taskState = TaskStateEnum.FREE;

        public TaskStateEnum getTaskState() {
            return this.taskState;
        }

        public WorkerTask(ThreadGroup group, String name) {
            super(group, name);
        }

        @Override
        public void run() {
            OUTER:
            while (this.taskState != TaskStateEnum.DEAD) {
                Runnable runnable;
                synchronized (TASK_QUEUE) {
                    while (TASK_QUEUE.isEmpty()) {
                        try {
                            taskState = TaskStateEnum.BLOCKED;
                            TASK_QUEUE.wait();
                        } catch (InterruptedException e) {
                            System.out.println("thread:" + Thread.currentThread().getName() + " interrupted and close");
                            break OUTER;
                        }
                    }
                    runnable = TASK_QUEUE.removeFirst();
                }

                // 运行任务
                if (runnable != null) {
                    taskState = TaskStateEnum.RUNNING;
                    runnable.run();
                    taskState = TaskStateEnum.FREE;
                }
            }
        }

        /**
         * 关闭线程
         */
        public void close() {
            this.taskState = TaskStateEnum.DEAD;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool2 threadPool = new SimpleThreadPool2();
        for (int i = 0; i < 40; i++) {
            threadPool.submit(() -> {
                System.out.println("The runnable be serviced by " + Thread.currentThread() + " start.");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("=========="+Thread.currentThread().getName());
                    e.printStackTrace();
                }
                System.out.println("The runnable be serviced by " + Thread.currentThread() + " finished.");
            });
        }

        Thread.sleep(20000);
        threadPool.shutdown();

       /* Thread.sleep(10000);
        threadPool.shutdown();
        threadPool.submit(() -> System.out.println("======="));*/
    }
}
