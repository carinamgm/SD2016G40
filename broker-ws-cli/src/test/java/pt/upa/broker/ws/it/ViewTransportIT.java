package pt.upa.broker.ws.it;

import org.junit.*;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.*;

import static org.junit.Assert.assertEquals;




public class ViewTransportIT {

    // static members
    private static BrokerClientApplication _bcp;
    private static String _transpId;


    // static members


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
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
        _bcp = new BrokerClientApplication();
        _bcp.testSetup();

        _transpId = _bcp.getBrokerClient().schedule("Porto", "Lisboa", 40);


    }

    @After
    public void tearDown() {
        _bcp.getBrokerClient().clearTransports();
        _bcp = null;
        _transpId = "";
    }


    // tests

   @Test
    public void successfulViewTransport() throws UnknownTransportFault_Exception, InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

    	TransportView transpState;

       transpState = _bcp.getBrokerClient().checkTransportState(_transpId);
        assertEquals(TransportStateView.BOOKED, transpState.getState());
    }


    @Test
    public void successfulViewCompletedTransport() throws UnknownTransportFault_Exception, InterruptedException {
        TransportView transpState;

        Thread.sleep(15000);
        transpState = _bcp.getBrokerClient().checkTransportState(_transpId);

        assertEquals(TransportStateView.COMPLETED, transpState.getState());
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendNegativeIdViewTransport() throws UnknownTransportFault_Exception {

        _bcp.getBrokerClient().checkTransportState("-5");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendNullViewTransport() throws UnknownTransportFault_Exception {

        _bcp.getBrokerClient().checkTransportState(null);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendEmptyStringViewTransport() throws UnknownTransportFault_Exception {

        _bcp.getBrokerClient().checkTransportState("");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendWeirdCharactersViewTransport() throws UnknownTransportFault_Exception {

        _bcp.getBrokerClient().checkTransportState("&$&/(%=");
    }

}