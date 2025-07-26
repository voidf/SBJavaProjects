
public class _TestClassExample {

    private static StringBuilder log = new StringBuilder();

    @BeforeClass
    public static void setUpClass() {
        log.append("BeforeClass ");
    }

    @AfterClass
    public static void tearDownClass() {
        log.append("AfterClass ");
    }

    @Before
    public void setUp() {
        log.append("Before ");
        // try {
        //     throw new Exception();
        // } catch (Exception e) {
        //     e.printStackTrace(System.err);
        // }
    }

    @After
    public void tearDown() {
        log.append("After ");
    }

    @Test
    public void testMethod1() {
        log.append("Test1 ");
    }

    @Test
    public void testMethod2() {
        log.append("Test2 ");
    }

    @Test
    public void testFailure() {
        throw new RuntimeException("Intentional failure");
    }

    public static String getLog() {
        return log.toString();
    }

    public static void resetLog() {
        log.setLength(0);
    }
}
