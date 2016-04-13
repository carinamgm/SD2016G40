package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
//import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.cli.BrokerClient;
//import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.BindingProvider;
//import javax.xml.ws.Endpoint;
//import java.util.Collection;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.junit.Assert.*;

public class PingIT {

    // static members
	private static BrokerClient _bc;
	private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
    	UDDINaming uddiNaming = null;
    	String endpointAddress;

        System.out.println("----------------------");
        System.out.println("------- TESTING ------");
        System.out.println("-------- PING --------");

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
    }
    

    // tests

    @Test
    public void successfulpinging() {
        assertEquals("Stuff happens!", _bc.ping("Stuff happens!"));
    }
    
    @Test
    public void sendNullPing() {
    	assertEquals(null, _bc.ping(null));
    }
    
    @Test
    public void sendEmptyStringPing() {
    	assertEquals("", _bc.ping(""));
    }

    @Test
    public void sendWeirdSymbolsPing() {
        assertEquals("#$&//%", _bc.ping("#$&//%"));
    }
}
