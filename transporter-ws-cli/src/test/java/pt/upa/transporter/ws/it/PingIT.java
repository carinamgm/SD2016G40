package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.TransporterClientApplication;

import static org.junit.Assert.assertEquals;

public class PingIT{
    // static members
    private static TransporterClientApplication _tc;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    // members

    // initialization and clean-up for each test

    @Before
    public void setUp() {
        _tc = new TransporterClientApplication();
        _tc.setup();
    }

    @After
    public void tearDown() {
        _tc.clean();
        _tc = null;
    }


    // tests

    @Test
    public void ping() {
        String pingMessage = "ping";
        String excpected = "";

        for(String s : _tc.getTransporterNames())
            excpected += s + ": " + pingMessage + "\n";

        assertEquals("Checking ping",excpected,_tc.getTransporterClient().ping(pingMessage));
    }

}