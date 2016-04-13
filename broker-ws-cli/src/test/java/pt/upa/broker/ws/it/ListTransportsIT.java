package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class ListTransportsIT {

    // static members
    private static BrokerClient _bc;
    private static List<TransportView> _result;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        UDDINaming uddiNaming = null;
        _result = null;
        String endpointAddress;

        System.out.println("----------------------");
        System.out.println("------- TESTING ------");
        System.out.println("--- LISTTRANSPORTS ---");

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

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {
        _bc.schedule("Porto", "Lisboa", 40);
        _bc.schedule("Lisboa", "Faro", 51);

        try {
            _bc.schedule("Leiria", "Braga", 35);
        } catch (UnavailableTransportPriceFault_Exception e) {}
        try {
            _bc.schedule("Beja", "Viseu", 16);
        } catch (UnavailableTransportPriceFault_Exception e) {}

    }

    @After
    public void tearDown() {
        _bc.clearTransports();
    }


    // tests

    @Test
    public void simpleStatesTransportView() {

    	_result = _bc.listScheduleTransports();
    }

    @Test
    public void varyingAndSimpleStatesTransportView() {

        _result = _bc.listScheduleTransports();
    }
}