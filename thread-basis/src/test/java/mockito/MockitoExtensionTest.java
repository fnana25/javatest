package mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MockitoExtensionTest {

    @Mock
    private List<String> list;
    @Spy
    private List<String> spyList = new ArrayList<>();

    @InjectMocks
    private SimpleService simpleService;

    @Mock
    private SimpleRepository simpleRepository;

    @Test
    public void testMockList(){
        Mockito.when(list.get(0)).thenReturn("Hello").thenReturn("World").thenReturn("Junit").thenReturn("Junit");
        assertAll(
                ()->assertEquals("Hello",list.get(0)),
                ()->assertEquals("World",list.get(0)),
                ()->assertEquals("Junit",list.get(0)),
                ()->assertEquals("Junit",list.get(0))
        );
    }

    /**
     * 等价于第一个test,
     * @param list
     */
    @Test
    public void testMockListParameter(@Mock List<String> list){
        Mockito.when(list.get(0)).thenReturn("Hello").thenReturn("World").thenReturn("Junit").thenReturn("Junit");
        assertAll(
                ()->assertEquals("Hello",list.get(0)),
                ()->assertEquals("World",list.get(0)),
                ()->assertEquals("Junit",list.get(0)),
                ()->assertEquals("Junit",list.get(0))
        );
    }

    @Test
    public void testSpyList(){

        spyList.add("1");

        Mockito.doReturn("Hello").doReturn("World").doReturn("Junit").doReturn("Junit").when(list).get(0);
        spyList.add("2");
        assertEquals(2,spyList.size());
        assertAll(
                ()->assertEquals("Hello",list.get(0)),
                ()->assertEquals("World",list.get(0)),
                ()->assertEquals("Junit",list.get(0)),
                ()->assertEquals("Junit",list.get(0))
        );
    }

    @Test
    public void shouldBeTrueWhenServiceCreate(){
        Mockito.when(simpleRepository.insert()).thenReturn(true);
        assertTrue(simpleService.create());
    }

    static class SimpleService{
        private final SimpleRepository simpleRepository;
        SimpleService(SimpleRepository simpleRepository){
            this.simpleRepository = simpleRepository;
        }

        public boolean create(){
            return this.simpleRepository.insert();
        }
    }

    static class SimpleRepository{
        public boolean insert(){
            throw new UnsupportedOperationException();
        }
    }
}
