package junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tag配置了之后，需要在pom文件配置参数：
 * <configuration>
 *     <groups>dev</groups>
 * </configuration>
 * 1、方法的tag会和类上的tag合并，取并集
 * 2、可以通过TestInfo的getTags方法查询到底有哪些tag
 */
@Tags({
        @Tag("dev"),
        @Tag("sit"),
        @Tag("uat")
})
//@Tag("dev")
//@Tag("sit")
//@Tag("uat")
//等价于@tags
@DisplayName("<The unit test for java immutable>")
class JupiterTaggingTest {

    private static List<String> LIST = null;

    @BeforeAll
    public static void init() {
        LIST = Arrays.asList("HELLO", "JAVA", "JUNIT", "JUPITER");
    }

    @Tag("dev")
    @Test
    @DisplayName("<The unit test for size>")
    public void listSizeShouldEquals4() {
        assertEquals(4, LIST.size());
    }

    @Tags({
            @Tag("sit")
    })
    @Test
    @DisplayName("<The unit test for contains>")
    public void listContainsJavaElement() {
        assertTrue(() -> LIST.contains("JAVA"));
    }

    @Tag("uat")
    @Tag("prod")
    @Test
    @DisplayName("<The unit test for update and throw exception>")
    public void ImmutableListCouldNotUpdate(TestInfo testInfo) {

        //查询当前测试用例tags信息
        testInfo.getTags().forEach(System.out::println);
        Executable executable = () -> {
            String firstStr = LIST.remove(0);
            assertEquals("HELLO", firstStr);
        };
        assertThrows(UnsupportedOperationException.class, executable);
    }

    @Test
    @DisplayName("immutable list only support read but update")
    public void immutableListCanReadButUpdate() {

        //第一个抛出异常之后，后面的断言不会再执行，导致不知道后面的断言是否正确。
//        assertEquals("HELLO",LIST.remove(0))/;
        //第一个断言失败，后面的断言不会再执行，导致不知道后面的断言是否正确。
//        assertEquals("HELL2eO", LIST.get(0));
//        assertEquals("JAVA", LIST.get(1));
//        assertEquals("JUNIT", LIST.get(2));

        //改用assertAll方法，即使其中有异常抛出也会继续执行
        assertAll("assert read and update mixed operation", Stream.of(
                () -> assertEquals("HELLO", LIST.remove(0)),
                () -> assertEquals("JAVA", LIST.get(1)),
                () -> assertEquals("HELL2eO", LIST.get(0)),
                () -> assertEquals("JUNIT", LIST.get(2))
        ));
    }
}
