package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;

import static org.junit.Assert.*;



public class PingIT {

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
    

    // tests

    @Test
    public void successfulpinging() {
    	
    //	assertEquals("Stuff happens!", _broker.ping("Stuff happens!"));
    }
    
    @Test
    public void sendNullPing() {
    	
    //	assertEquals(null, _broker.ping(null));
    }
    
    @Test
    public void sendEmptyStringPing() {
    	
    //	assertEquals("", _broker.ping(""));
    }

}