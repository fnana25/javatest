package junit4;

import org.junit.*;

import java.util.ArrayList;
import java.util.List;

/**
 * junit4 生命周期函数
 */
public class Junit4StandardTest {

    private static List<String> classLevelList;

    private List<String> testCaseLevelList;

    @BeforeClass
    public static void init(){
       classLevelList = new ArrayList<>();
    }

    @Before
    public void setUp(){
        this.testCaseLevelList = new ArrayList<>();
    }

    @Test
    public void addEleIntoClassLevelList(){
        Assert.assertTrue(classLevelList.add("java"));
        Assert.assertTrue(this.testCaseLevelList.add("java"));
    }

    @Test
    public void addEleIntoTestLevelList(){
        Assert.assertTrue(classLevelList.add("junit"));
        Assert.assertTrue(this.testCaseLevelList.add("junit"));
    }

    @After
    public void after(){
        Assert.assertEquals(1,this.testCaseLevelList.size());
        testCaseLevelList.clear();
    }

    @AfterClass
    public static void tearDown(){
        Assert.assertEquals(2,classLevelList.size());
        classLevelList.clear();
    }
}
