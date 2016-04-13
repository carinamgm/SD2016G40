package pt.upa.broker.ws.it;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerClientApplication;
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
    private static BrokerClientApplication _bcp;
    private static String _id1;
    private static String _id2;

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        _bcp = new BrokerClientApplication();
        _bcp.testSetup();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        _bcp = null;
    }

    // members
    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {
        _bcp = new BrokerClientApplication();
        _bcp.testSetup();

        _id1 = _bcp.getBrokerClient().schedule("Porto", "Lisboa", 40);
        _id2 = _bcp.getBrokerClient().schedule("Lisboa", "Faro", 51);

        try {
            _bcp.getBrokerClient().schedule("Leiria", "Braga", 35);
        } catch (UnavailableTransportPriceFault_Exception e) {}
        try {
            _bcp.getBrokerClient().schedule("Beja", "Viseu", 16);
        } catch (UnavailableTransportPriceFault_Exception e) {}

    }

    @After
    public void tearDown() {
        _bcp.getBrokerClient().clearTransports();
    }

    // tests

    @Test
    public void simpleStatesTransportView() {
       assertEquals(2,_bcp.getBrokerClient().listScheduleTransports().size());
       for(TransportView tv : _bcp.getBrokerClient().listScheduleTransports()){
           if(tv.getId() == _id1){
               assertTrue(tv.getOrigin().equals("Porto") && tv.getDestination().equals("Lisboa") && tv.getPrice() < 40 &&
                       Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == 0 && tv.getState() == TransportStateView.BOOKED);
           }
           if(tv.getId() == _id2){
               assertTrue(tv.getOrigin().equals("Porto") && tv.getDestination().equals("Lisboa") && tv.getPrice() < 51 &&
                       Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == 1 && tv.getState() == TransportStateView.BOOKED);
           }

       }
    }



}