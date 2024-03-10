//package junit4;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//
//import java.util.*;
//
///**
// * 参数化
// */
//@RunWith(Parameterized.class)
//public class ParameterTest {
//
//    @Parameterized.Parameter(0)
//    public String literal;
//
//    @Parameterized.Parameter(1)
//    public int length;
//
//    @Parameterized.Parameters(name = "{index}=>literal={0},length={1}")
//    public static Collection<Object[]> data() {
//
//        return Arrays.asList(new Object[][]{
//                {"junit",5},
//                {"java",4},
//                {"day",3},
//        });
//    }
//
//    @Test
//    public void test(){
//        Assert.assertEquals(length,literal.length());
//    }
//}
