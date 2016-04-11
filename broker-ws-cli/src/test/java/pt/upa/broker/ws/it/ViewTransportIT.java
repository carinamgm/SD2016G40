package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

import static org.junit.Assert.*;




public class ViewTransportIT {

    // static members
	private static BrokerPortType _broker;
	private static String ticketId;


    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {

    	// _broker = new BrokerPort();
 
    }

    @AfterClass
    public static void oneTimeTearDown() {

    	_broker = null;
    }


    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	ticketId = _broker.requestTransport("Porto", "Lisboa", 40);
    }

    @After
    public void tearDown() {
    	ticketId = null;
    }


    // tests

    @Test
    public void successfullViewTransport() throws UnknownTransportFault_Exception {
    	
    	_broker.viewTransport(ticketId);
    	//TODO: assertEquals, to what?
    }
    
    @Test(expected = NullPointerException.class)
    public void sendNullViewTransport() throws UnknownTransportFault_Exception {
    	
    	_broker.viewTransport(null);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendEmptyStringViewTransport() throws UnknownTransportFault_Exception {
    	
    	_broker.viewTransport("");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendWeirdCharactersViewTransport() throws UnknownTransportFault_Exception {
    	
    	_broker.viewTransport("&$&/(%=");
    }
    
}