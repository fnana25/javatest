//package junit4;
//
//import org.junit.Assert;
//import org.junit.experimental.theories.DataPoints;
//import org.junit.experimental.theories.Theories;
//import org.junit.experimental.theories.Theory;
//import org.junit.runner.RunWith;
//
///**
// * 测试方法接受入参
// */
//@RunWith(Theories.class)
//public class TheoriesTest {
//
//    @DataPoints
//    public static int[] data() {
//
//        return new int[]{1, 10, 100};
//    }
//
//    @Theory
//    public void test(int a, int b) {
//        Assert.assertTrue(a + b > a);
//        System.out.printf("%d+%d>%d\n", a, b, a);
//    }
//}
