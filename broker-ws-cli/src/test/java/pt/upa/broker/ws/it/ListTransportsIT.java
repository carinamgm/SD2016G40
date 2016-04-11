package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.TransportView;

import java.util.ArrayList;

import static org.junit.Assert.*;



public class ListTransportsIT {

    // static members
	private static BrokerPortType _broker;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {

    	// _broker = new BrokerPort();
    }

    @AfterClass
    public static void oneTimeTearDown() {

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
    public void successfullTransportView() {
    	//List<TransportView> result = new List<TransportView>;
    	ArrayList<TransportView> result = new ArrayList<TransportView>();
    	
    	//result = _broker.listTransports();
    }
    
}