package pt.upa.broker.ws.it;

import org.apache.commons.lang.ArrayUtils;
import org.junit.*;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.*;

import java.util.List;

import static org.junit.Assert.assertTrue;

//import java.util.List;


public class FailureRecuperationTestIT {

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
        _bcp.getBrokerClient().clearTransports();
        _bcp = null;
    }
    

    @Test
    public void success() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
    								UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, InterruptedException{
    	
    	List<TransportView> result = null;
    	String[] ids = new String[4];
    	
    	
    	ids[0] = _bcp.getBrokerClient().schedule("Lisboa", "Santarém", 0);
    	ids[1] = _bcp.getBrokerClient().schedule("Faro", "Portalegre", 0);
    	ids[2] = _bcp.getBrokerClient().schedule("Viseu", "Porto", 0);
    	ids[3] = _bcp.getBrokerClient().schedule("Braga", "Viana do Castelo", 0);
    	
    	Thread.sleep(12*1000);
    
    	result = _bcp.getBrokerClient().listScheduleTransports();
    	
    	assertTrue("Size not corresponding",result.size() == 4);
    	
    	for(TransportView tv : result){
    		ids = (String[]) ArrayUtils.removeElement(ids, tv.getId());	
    	}
    	
    	assertTrue("", ids.length == 0);
    	
    	_bcp.getBrokerClient().clearTransports();
    	result = _bcp.getBrokerClient().listScheduleTransports();
    	
    	assertTrue("", result.size() == 0);

    }
    
}
