package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

//import java.util.List;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import static org.junit.Assert.*;


public class RequestTransportIT {

    // static members
    private static BrokerClient _bc;
    // private static List<TransportView> _tvs;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        UDDINaming uddiNaming = null;
        String endpointAddress;

        System.out.println("----------------------");
        System.out.println("------- TESTING ------");
        System.out.println("-- REQUESTTRANSPORT --");

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
            //_tvs = _bc.listScheduleTransports();

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }

    }

    @AfterClass
    public static void oneTimeTearDown() {
    	_bc = null;
        //_tvs = null;
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        _bc.clearTransports();
    }


    // tests

    @Test
    public void successfullyRequestTransport() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result;

        result = _bc.schedule("Porto", "Lisboa", 50);
        assertEquals("0", result);
    }

   @Test
    public void successfullyRequestMultipleDifferentTransporters() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

       String result1, result2;
       result1 = _bc.schedule("Faro", "Lisboa", 55);
       result2 = _bc.schedule("Lisboa", "Porto", 50);

       assertEquals("0", result1);
       assertEquals("0", result2);
    }

    @Test
    public void successfullyRequestMultipleEvenSameTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result1, result2;
        result1 = _bc.schedule("Porto", "Lisboa", 40);
        result2 = _bc.schedule("Lisboa", "Porto", 50);

        assertEquals("0", result1);
        assertEquals("1", result2);
    }

    @Test
    public void successfullyRequestMultipleOddSameTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result1, result2;
        result1 = _bc.schedule("Lisboa", "Faro", 35);
        result2 = _bc.schedule("Faro", "Leiria", 55);

        assertEquals("0", result1);
        assertEquals("1", result2);
    }

    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void oddPriceToEvenTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        _bc.schedule("Porto", "Lisboa", 45);
    }

    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void evenPriceToOddTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        _bc.schedule("Leiria", "Faro", 40);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendEmptyOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bc.schedule("", "Leiria", 30);
    }

    @Test(expected = UnavailableTransportFault_Exception.class)
    public void sendBigPrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_bc.schedule("Leiria", "Lisboa", 999999999);
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void sendNegativePrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bc.schedule("Porto", "Lisboa", -5);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendNullDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_bc.schedule("Porto", null, 50);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendWrongDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bc.schedule("Lisboa", "Caldas da Rainha", 50);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendWeirdSymbolsOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bc.schedule("!(%#)=", "Viseu", 50);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void originDestinationTooFarApart() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bc.schedule("Porto", "Faro", 50);
    }
    
}