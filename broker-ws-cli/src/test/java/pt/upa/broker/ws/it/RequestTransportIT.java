package pt.upa.broker.ws.it;

import org.junit.*;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.*;

import java.util.List;

import static org.junit.Assert.assertTrue;

//import java.util.List;


public class RequestTransportIT {

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


    // tests

    @Test
    public void successfullyRequestTransport() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result;

        result = _bcp.getBrokerClient().schedule("Porto", "Lisboa", 0);
        List<TransportView> tvs = _bcp.getBrokerClient().listScheduleTransports();

        for(TransportView tv : tvs){
            if(tv.getId().equals(result))
                assertTrue(transporterViewsComparator(tv, "Porto", "Lisboa", 0, 0, TransportStateView.BOOKED));
        }
    }
    /*
    @Test
    public void successfullyRequestMultipleDifferentTransporters() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result1, result2;
        result1 = _bcp.getBrokerClient().schedule("Faro", "Lisboa", 55);
        result2 = _bcp.getBrokerClient().schedule("Lisboa", "Porto", 50);

        for (TransportView tv : _bcp.getBrokerClient().listScheduleTransports()) {
            if (tv.getId().equals(result1))
                assertTrue(transporterViewsComparator(tv, "Faro", "Lisboa", 55, 1, TransportStateView.BOOKED));

            if (tv.getId().equals(result2))
                assertTrue(transporterViewsComparator(tv, "Lisboa", "Porto", 50, 0, TransportStateView.BOOKED));
        }
    }

    @Test
    public void successfullyRequestMultipleEvenSameTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result1, result2;
        result1 = _bcp.getBrokerClient().schedule("Porto", "Lisboa", 40);
        result2 = _bcp.getBrokerClient().schedule("Lisboa", "Porto", 50);

        for (TransportView tv : _bcp.getBrokerClient().listScheduleTransports()) {
            if (tv.getId().equals(result1))
                assertTrue(transporterViewsComparator(tv, "Porto", "Lisboa", 40, 0, TransportStateView.BOOKED));

            if (tv.getId().equals(result2))
                assertTrue(transporterViewsComparator(tv, "Lisboa", "Porto", 50, 0, TransportStateView.BOOKED));
        }
    }

    @Test
    public void successfullyRequestMultipleOddSameTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        String result1, result2;
        result1 = _bcp.getBrokerClient().schedule("Lisboa", "Faro", 35);
        result2 = _bcp.getBrokerClient().schedule("Faro", "Leiria", 55);

        for (TransportView tv : _bcp.getBrokerClient().listScheduleTransports()) {
            if (tv.getId().equals(result1))
                assertTrue(transporterViewsComparator(tv, "Lisboa", "Faro", 35, 1, TransportStateView.BOOKED));

            if (tv.getId().equals(result2))
                assertTrue(transporterViewsComparator(tv, "Faro", "Leiria", 55, 1, TransportStateView.BOOKED));
        }
    }

    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void oddPriceToEvenTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        _bcp.getBrokerClient().schedule("Porto", "Lisboa", 45);
    }


    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void evenPriceToOddTransporter() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {

        _bcp.getBrokerClient().schedule("Leiria", "Faro", 40);
    }


    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendEmptyOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("", "Leiria", 30);
    }

    @Test(expected = UnavailableTransportFault_Exception.class)
    public void sendBigPrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("Leiria", "Lisboa", 999999999);
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void sendNegativePrice() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("Porto", "Lisboa", -5);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendNullDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
	UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	_bcp.getBrokerClient().schedule("Porto", null, 50);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendWrongDestination() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("Lisboa", "Caldas da Rainha", 50);
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void sendWeirdSymbolsOrigin() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("!(%#)=", "Viseu", 50);
    }

    @Test(expected = UnavailableTransportFault_Exception.class)
    public void originDestinationTooFarApart() throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {

        _bcp.getBrokerClient().schedule("Porto", "Faro", 50);
    }
*/
    // Aux functions

    /* This method is not to be apply for over 100 expected price - so the return false ist just to fill the method signature */
    private boolean transporterViewsComparator(TransportView tv, String origin, String destination, int expectedPrice, int companyPairity, TransportStateView state) {
        if (expectedPrice == 0)
            return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() == expectedPrice &&
                    Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == companyPairity && tv.getState() == state;
        else if (expectedPrice < 10)
            return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() < expectedPrice &&
                    Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == companyPairity && tv.getState() == state;
        else if (expectedPrice % 2 == 0 && expectedPrice <= 100) {
            if (Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == 0)
                return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() < expectedPrice && tv.getState() == state;
            else
                return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() > expectedPrice && tv.getState() == state;
        }
        else if (expectedPrice % 2 == 1 && expectedPrice <= 100) {
            if (Integer.valueOf(tv.getTransporterCompany().substring(14, tv.getTransporterCompany().length())) % 2 == 0)
                return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() > expectedPrice && tv.getState() == state;
            else
                return tv.getOrigin().equals(origin) && tv.getDestination().equals(destination) && tv.getPrice() < expectedPrice && tv.getState() == state;
        }
        return false;
    }
}