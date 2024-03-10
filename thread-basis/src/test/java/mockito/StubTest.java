package mockito;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.listeners.MockitoListener;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import java.util.List;

/**
 *
 * 插桩的两种方式：
 * when(obj.someMethod).thenXxx ; 其中obj可以是mock对象
 * doXxx().when(obj).someMethod() ; 其中obj可以是mock/spy对象
 * mock对象在没有插桩时，默认返回方法的默认值，如 int返回0，对象返回null等。
 * spy在没有插桩时默认调用真实的方法，写在when中会导致先执行一次原方法，达不到mock的目的，所以需要使用：
 * doXxx().when(obj).someMethod()
 */
@ExtendWith(MockitoExtension.class)
public class StubTest {

    @Spy
    private List<String> list;

    @Test
    public void testSpyStub(){

//        Mockito.when(list.get(0)).thenReturn("Hello");

        Mockito.doReturn("Hello").when(list).get(0);
        assertThat(list.get(0), Matchers.equalTo("Hello"));

    }

}
