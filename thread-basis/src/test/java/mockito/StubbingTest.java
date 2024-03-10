package mockito;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.MatcherAssert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class StubbingTest {

    private List<String> list;

    @BeforeEach
    public void init(){
        this.list = mock(ArrayList.class);
    }

    @Test
    public void howToUseStubbing(){
        when(list.get(anyInt())).thenThrow(new RuntimeException());
        try {
            list.get(0);
            Assertions.fail();
        }catch (Exception e){
            MatcherAssert.assertThat(e, instanceOf(RuntimeException.class));
        }
    }

    @Test
    public void stubbingWithAnswer(){

        when(list.get(anyInt())).thenAnswer(e->{
            Integer index = e.getArgument(0,Integer.class);
            return String.valueOf(index * 10);
        });

        assertThat(list.get(10),equalTo("100"));
        assertThat(list.get(99),equalTo("990"));
    }

    @Test
    public void testMatcher(){
        double price = 23.46;
        assertThat(price,either(equalTo(23.45)).or(equalTo(23.54)));
        assertThat(price,both(equalTo(23.45)).and(equalTo(23.54)));
        assertThat(price,anyOf(is(23.45),is(23.54),not(23.56)));
        assertThat(price,allOf(is(23.45),not(is(23.54)),not(23.56)));

        assertThat(Stream.of(1,2,3).anyMatch(i->i>2),equalTo(true));
        assertThat(Stream.of(1,2,3).allMatch(i->i>0),equalTo(true));
    }

    @AfterAll
    public void destroy(){
        reset(this.list);
    }
}
