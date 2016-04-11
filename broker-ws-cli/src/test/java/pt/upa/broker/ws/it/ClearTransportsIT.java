package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;

import static org.junit.Assert.*;


public class ClearTransportsIT {

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

    //	_broker = null;
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() {
    	
    	// Maybe not even needed
    	// _broker.requestTransport("Porto", "Lisboa", 30);    	
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void successfulClearTransports() {

    //	_broker.clearTransports();
    	
    	// Expected null, or someway to say "emptylist"?
    //	assertEquals(null ,_broker.listTransports());
    }

}