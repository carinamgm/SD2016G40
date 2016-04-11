package pt.upa.broker.ws.it;

import org.junit.*;
import static org.junit.Assert.*;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;


public class RequestTransportIT {

    // static members
	private static BrokerPortType _broker;
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
    	//need to setup a caller for the BrokerPort Service
    	//	_broker = new BrokerPort();
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_broker = null;
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() {	
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void successfullyRequestTransport() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

    	String result;
    	
    	result = _broker.requestTransport("Porto", "Lisboa", 50);
    	
        //TODO: assertEquals("stuffhappened", result);
        // if the assert fails, the test fails
    }
    
    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendEmptyOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_broker.requestTransport("", "Leiria", 30);
    }
    
    @Test(expected = InvalidPriceFault_Exception.class)
    public void sendBigPrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_broker.requestTransport("Leiria", "Lisboa", 999999999);
    }
    
    @Test(expected = NullPointerException.class)
    public void sendNullDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_broker.requestTransport("Porto", null, 50);
    }

    
}