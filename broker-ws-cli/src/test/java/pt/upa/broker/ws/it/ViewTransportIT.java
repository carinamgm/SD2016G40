package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import static org.junit.Assert.*;




public class ViewTransportIT {

    // static members
    private static BrokerClient _bc;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";
    private static String _transpId;


    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        _transpId = "";
        UDDINaming uddiNaming = null;
        String endpointAddress;

        System.out.println("----------------------");
        System.out.println("------- TESTING ------");
        System.out.println("--- VIEW TRANSPORT ---");

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
        _transpId = "";
    	_bc = null;
    }


    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    }

    @After
    public void tearDown() {
        _bc.clearTransports();
        _transpId = "";
    }


    // tests

    @Test
    public void successfulViewTransport() throws UnknownTransportFault_Exception, InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	TransportView transpState;

        _transpId = _bc.schedule("Porto", "Lisboa", 40);
    	transpState = _bc.checkTransportState(_transpId);

        assertEquals(TransportStateView.BOOKED, transpState.getState());
    }

/*
    @Test
    public void successfulViewCompletedTransport() throws UnknownTransportFault_Exception, InterruptedException {
        TransportView transpState;

        Thread.sleep(15000);
        transpState = _bc.checkTransportState(transpId);

        assertEquals(TransportStateView.COMPLETED, transpState.getState());
    }
*/
    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendNegativeIdViewTransport() throws UnknownTransportFault_Exception {

        _bc.checkTransportState("-5");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendNullViewTransport() throws UnknownTransportFault_Exception {
    	
    	_bc.checkTransportState(null);
    }
    
    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendEmptyStringViewTransport() throws UnknownTransportFault_Exception {
    	
    	_bc.checkTransportState("");
    }

    @Test(expected = UnknownTransportFault_Exception.class)
    public void sendWeirdCharactersViewTransport() throws UnknownTransportFault_Exception {
    	
    	_bc.checkTransportState("&$&/(%=");
    }

}