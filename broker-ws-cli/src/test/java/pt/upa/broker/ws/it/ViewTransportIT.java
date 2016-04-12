package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.cli.BrokerClient;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
//import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerService;
//import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.BindingProvider;
//import javax.xml.ws.Endpoint;
//import java.util.Collection;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import static org.junit.Assert.*;




public class ViewTransportIT {

    // static members
    private static BrokerClient _bc;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";
    private static String transpId;


    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        transpId = "";
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
        transpId = "";
    	_bc = null;
    }

/*
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
*/ 
}