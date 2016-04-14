package pt.upa.broker.ws.it;

import org.junit.*;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.*;

import static org.junit.Assert.assertEquals;

//import pt.upa.broker.ws.BrokerPort;
//import pt.upa.transporter.ws.cli.TransporterClient;
//import javax.xml.ws.Endpoint;
//import java.util.Collection;


public class ClearTransportsIT {

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
    public void setUp() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        _bcp = new BrokerClientApplication();
        _bcp.testSetup();

        _bcp.getBrokerClient().schedule("Porto", "Lisboa", 40);
        _bcp.getBrokerClient().schedule("Lisboa", "Faro", 51);
    }

    @After
    public void tearDown() {
        _bcp = null;
    }

    // tests

    @Test
    public void successfulClearTransports() {
        assertEquals(2, _bcp.getBrokerClient().listScheduleTransports().size());
        _bcp.getBrokerClient().clearTransports();
        assertEquals(0, _bcp.getBrokerClient().listScheduleTransports().size());
    }

}