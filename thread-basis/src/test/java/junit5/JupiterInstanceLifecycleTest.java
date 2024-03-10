package junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({
        JupiterLifeCycle.MyInstancePostProcessor.class,
        JupiterLifeCycle.MyBeforeAllCallback.class,
        JupiterLifeCycle.MyBeforeEachCallback.class,
        JupiterLifeCycle.MyBeforeTestExecutionCallback.class,
        JupiterLifeCycle.MyTestExecutionExceptionHandler.class,
        JupiterLifeCycle.MyAfterTestExecutionCallback.class,
        JupiterLifeCycle.MyAfterEachCallback.class,
        JupiterLifeCycle.MyAfterAllCallback.class,
})
@DisplayName("<Lifecycle test>")
public class JupiterInstanceLifecycleTest {

    private final List<String> LIST = Arrays.asList("HELLO", "JAVA", "JUNIT", "JUPITER");

    @BeforeAll
    public static void init() {
        System.out.println("-----------------------init");
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        System.out.println("<setUp " + testInfo.getTestMethod().get() + ">");
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        System.out.println("<tearDown " + testInfo.getTestMethod().get() + ">");
    }

    @AfterAll
    public static void destroy() {
        System.out.println("-----------------destroy");
    }

    @Test
    @DisplayName("<The unit test for size>")
    public void listSizeShouldEquals4() {
//        assertEquals(4, LIST.size());
        assertThat(LIST, hasSize(4));
    }

    @Test
    @DisplayName("<The unit test for contains>")
    public void listContainsJavaElement() {
//        assertTrue(() -> LIST.contains("JAVA"));
        assertThat(LIST,hasItem("JAVA"));
    }

    @Test
    @DisplayName("<The unit test for update and throw exception>")
    public void ImmutableListCouldNotUpdate() {

        Executable executable = () -> {
            String firstStr = LIST.remove(0);
//            assertEquals("HELLO", firstStr);
            assertThat(firstStr,equalTo("HELLO"));
        };
        assertThrows(UnsupportedOperationException.class, executable);
    }

    @Test
    @DisplayName("immutable list only support read but update")
    public void immutableListCanReadButUpdate() {
        //改用assertAll方法，即使其中有异常抛出也会继续执行
        assertAll("assert read and update mixed operation", Stream.of(
                () -> assertEquals("HELLO", LIST.remove(0)),
                () -> assertEquals("JAVA", LIST.get(1)),
                () -> assertEquals("HELL2eO", LIST.get(0)),
                () -> assertEquals("JUNIT", LIST.get(2))
        ));
    }
}
