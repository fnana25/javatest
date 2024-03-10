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
 * https://junit.org/junit5/docs/5.5.2/user-guide/#overview
 * Assert
 * Assert Exception
 * Assert All
 * DisplayName
 * Disabled
 * Assert Timeout
 * Assumptions
 * Repeat
 * Order
 * Nested Test Class
 * Test case lifeCycle
 * Tagging & Filtering
 * Custom Tagging
 * Integration With Hamcrest
 * TestInstancePostProcessor
 * Callback API
 * Handler API
 * Conditional Execution
 */
@DisplayName("<The unit test for java immutable>")
class ImmutableListTest {

    private final List<String> LIST = Arrays.asList("HELLO", "JAVA", "JUNIT", "JUPITER");

    private static String ENV;

    @BeforeAll
    public static void init() {
        ENV = System.getenv().getOrDefault("env", "N/A");
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

    @DisplayName("Basic test")
    @Nested
    class BasicTest {
        @Test
        @DisplayName("<The unit test for size>")
        public void listSizeShouldEquals4() {
            assertEquals(4, LIST.size());
        }

        @Test
        @DisplayName("<The unit test for contains>")
        public void listContainsJavaElement() {
            assertTrue(() -> LIST.contains("JAVA"));
        }

        @Test
        @DisplayName("<The unit test for update and throw exception>")
        public void ImmutableListCouldNotUpdate() {

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

    @Nested
    @DisplayName("Repeat test")
    class RepeatTest {
        @RepeatedTest(3)
        public void repeatTest() {
            assertTrue(() -> LIST.contains("JUNIT"));
        }

        @DisplayName("Repeat Assert immutable list element ==>")
        @RepeatedTest(value = 4, name = "{displayName} {currentRepetition}/{totalRepetitions}")
        public void repeatWithIndex(RepetitionInfo info) {

            String element;
            switch (info.getCurrentRepetition()) {
                case 1:
                    element = "HELLO";
                    break;
                case 2:
                    element = "JAVA";
                    break;
                case 3:
                    element = "JUNIT";
                    break;
                case 4:
                    element = "JUPITER";
                    break;
                default:
                    element = "N/A";
            }
            assertEquals(element, LIST.get(info.getCurrentRepetition() - 1));
        }
    }

    @Nested
    @DisplayName("TimeOut test")
    class TimeOutTest {
        @Test
        //不加这个注解，这个方法会一直阻塞，不会结束
        @Timeout(value = 3, unit = TimeUnit.SECONDS)
        @DisplayName("Simple test for assertTimeout")
        public void timeoutAssertion() {
            final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
            assertTimeout(Duration.ofSeconds(2), () -> {
                queue.take();
            });
        }
    }

    @Nested
    @DisplayName("Assumption test")
    class AssumptionTest {
        @Test
        public void onlyExecutionAtSitEnv() {
            Assumptions.assumeTrue(Objects.equals(ENV, "sit"));
            final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
            assertTimeout(Duration.ofSeconds(2), () -> {
                queue.take();
            });
        }
    }
}
