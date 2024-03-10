package junit5;

import org.junit.jupiter.api.extension.*;

public interface JupiterLifeCycle {

    class MyInstancePostProcessor implements TestInstancePostProcessor{
        @Override
        public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyInstancePostProcessor--postProcessTestInstance---"+o);
            System.out.println(extensionContext.getTestInstanceLifecycle());
            System.out.println("--MyInstancePostProcessor--postProcessTestInstance---"+o);
        }
    }

    class MyBeforeAllCallback implements BeforeAllCallback {
        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyBeforeAllCallback--beforeAll---");
        }
    }

    class MyBeforeEachCallback implements BeforeEachCallback {
        @Override
        public void beforeEach(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyBeforeEachCallback--beforeEach--");
        }
    }

    class MyBeforeTestExecutionCallback implements BeforeTestExecutionCallback{
        @Override
        public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyBeforeTestExecutionCallback--beforeTestExecution--");
        }
    }

    class MyTestExecutionExceptionHandler implements TestExecutionExceptionHandler{
        @Override
        public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
            System.out.println("--MyTestExecutionExceptionHandler--handleTestExecutionException--");
        }
    }

    class MyAfterTestExecutionCallback implements AfterTestExecutionCallback{
        @Override
        public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyAfterTestExecutionCallback--afterTestExecution--");
        }
    }

    class MyAfterEachCallback implements AfterEachCallback{
        @Override
        public void afterEach(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyAfterEachCallback--afterEach--");
        }
    }

    class MyAfterAllCallback implements AfterAllCallback{
        @Override
        public void afterAll(ExtensionContext extensionContext) throws Exception {
            System.out.println("--MyAfterAllCallback--afterAll--");
        }
    }
}
