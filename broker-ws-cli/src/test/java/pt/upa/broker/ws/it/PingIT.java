package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
//import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.cli.BrokerClient;
//import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.BindingProvider;
//import javax.xml.ws.Endpoint;
//import java.util.Collection;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.junit.Assert.*;

public class PingIT {

    // static members
    private static BrokerClientApplication _bcp;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        _bcp = new BrokerClientApplication();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        _bcp = null;
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() {
        _bcp = new BrokerClientApplication();
        _bcp.testSetup();

    }

    @After
    public void tearDown() {
        _bcp = null;
    }

    // tests

    @Test
    public void successfulpinging() {
        assertEquals("Stuff happens!", _bcp.getBrokerClient().ping("Stuff happens!"));
    }
    
    @Test
    public void sendNullPing() {
    	assertEquals(null, _bcp.getBrokerClient().ping(null));
    }
    
    @Test
    public void sendEmptyStringPing() {
    	assertEquals("", _bcp.getBrokerClient().ping(""));
    }

    @Test
    public void sendWeirdSymbolsPing() {
        assertEquals("#$&//%", _bcp.getBrokerClient().ping("#$&//%"));
    }
}
