package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.*;

import java.util.ArrayList;
import java.util.Collection;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.cli.TransporterClient;


public class BrokerPortTest {

	// static members
	
	// one-time initialization and clean-up

/* --- UNUSED

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }
*/

    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	
    	String[][] regions = {{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"},
    			{"Lisboa","Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"},
    			{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"}};   	
    }

 /*   @After
    public void tearDown() {
    	
    }


    // tests

    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongOrigin(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Caldas da Rainha";
    	final int PRICE = 30;    	
    	
    //	transporter = new TransporterPortType("Lisboa, Castelo Branco", "UpaTransporte1");
    	ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();
    	Collection<String> wsUrls;
	UpaTransporter1;
    	UpaTransporter2;
    	transport;
    	wsUrls.add();
	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	// Getting a NullPointerException
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
     
    @Test(expected = InvalidPriceFault_Exception.class)
    public void wrongPrice(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Leiria";
    	final int PRICE = -1;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
   
    @Test(expected = UnavailableTransportFault_Exception.class)
    public void nonAvailableTransportForTrip(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	final String ORIGIN = "Porto";
    	final String DEST = "Faro";
    	final int PRICE = 30;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
     
    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void nonAvailableTransportByPrice(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Beja";
    	final int PRICE = 30;
    	
    	//UpaTransporter makes an offer of 55, price is above budget
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
    
    @Test
    public void gettingTransport(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Beja";
    	final int PRICE = 30;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);    	
    }
    
    @Test
    public void twoTransportsSamePrice(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	
    	final String ORIGIN = "Porto";
    	final String DEST = "Lisboa";
    	final int PRICE = 30;
    	
    	//Need 2 different UpaTransporters to make an offer of 15, in the end must choose 1
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE); 
    }

    @Test
    public void pinging() {
    	// assertEquals("TaxisBetterThanUber" , ping("TaxisBetterThanUber"));
    }
    
    @Test
    public void listingTransportsStates(@Mocked final TransporterClient transClient, @Mocked final TransporterPortType transport) {
    	
    	ArrayList<TransportView> _tvs = new ArrayList<TransportView>();
    	// 4 different transporters, 1 for each 4 different states
    	// 1 BUDGETED
    	// 1 PROPOSED
    	// 1 FAILED
    	// 1 ONGOING
    	
    	// listTransports();
    	
    }
    
    @Test
    public void clearAllData() {
    
       	ArrayList<TransportView> _tvs = new ArrayList<TransportView>();
    	//Mock a couple transporters, then ask to clear data???
    	clearTransports();
    }
  */  
}




