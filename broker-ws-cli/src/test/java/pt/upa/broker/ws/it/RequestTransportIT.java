package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
//import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;
//import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.BindingProvider;
//import javax.xml.ws.Endpoint;
//import java.util.Collection;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import static org.junit.Assert.*;


public class RequestTransportIT {

    // static members
    private static BrokerClient _bc;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        UDDINaming uddiNaming = null;
        String endpointAddress;

        try {
            uddiNaming = new UDDINaming(_uddiURL);
            endpointAddress = uddiNaming.lookup(_serviceName);

            System.out.println("EndPointAddress: " + endpointAddress);

            System.out.println("Creating stub ...");
            BrokerService service = new BrokerService();
            BrokerPortType port = service.getBrokerPort();

            System.out.println("Setting endpoint address ...");
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

            _bc = new BrokerClient(port);

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }

    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_bc = null;
        System.out.println("----------------------");
        System.out.println("-------- TEST --------");
        System.out.println("----------------------");
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() {
        System.out.println("----------- SETTING UP -------------");
    }

    @After
    public void tearDown() {
        System.out.println("--------------TEARING DOWN -------------");
        _bc.clearTransports();
    }


    // tests

    @Test
    public void successfullyRequestTransport() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception, InterruptedException {

    	String result;
        TransportView transp;
    	
    	result = _bc.schedule("Porto", "Lisboa", 50);
        Thread.sleep(10000);
        transp = _bc.checkTransportState(result);
        System.out.printf("TRANSPORTER STATE: %s %s %s %d\n", transp.getState(), transp.getOrigin(), transp.getDestination(), transp.getPrice());
        Thread.sleep(10000);
        transp = _bc.checkTransportState(result);
        System.out.printf("TRANSPORTER STATE: %s %s %s %d\n", transp.getState(), transp.getOrigin(), transp.getDestination(), transp.getPrice());

        // TIMERS NOT WORKING!!!!!!!!
        // if the assert fails, the test fails
         assertEquals("0", result);

    }


    /*@Test(expected = UnknownLocationFault_Exception.class)
    public void sendEmptyOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

    	_bc.schedule("", "Leiria", 30);
    }
*/
    /*
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
*/
    
}