package pt.upa.broker.ws;

import mockit.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;


public class BrokerPortTest {

	// static members
    @Mocked TransporterClient _tca;

	// one-time initialization and clean-up


    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }


	/*String[][] regions = {{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"},
			{"Lisboa","Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"},
			{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"}};
	*/
	// members


    // initialization and clean-up for each test

    @Before
    public void setUp() {

	}

   @After
    public void tearDown() {
    	
    }


    // Mocking Transporter clis
    /*

	@Mocked TransporterClient transClient

	@Mocked

	private ArrayList<TransportView> _tvs = new ArrayList<TransportView>();
    private TransporterClient _tca;


	requestTransport

    proposals = _tca.requestJob(origin,destination,price);

    _tca.decideJob(chosenJobView.getJobIdentifier(), true);


    viewTransport!!!!!!!

    jv = _tca.jobStatus(id);


public List<TransportView> listTransports() {
        return _tvs;
    }


    public void clearTransports()



    */
    // tests

    @Test
    public void successfullyScheduled() throws BadPriceFault_Exception,
			BadLocationFault_Exception, InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{

        BrokerPort _bp = new BrokerPort();
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Braga";
    	final int PRICE = 30;
		String endresult;

        ArrayList<JobView> offers = new ArrayList<JobView>();
        JobView jv = new JobView();

        jv.setCompanyName("UpaTransporter2");
        jv.setJobDestination(DEST);
        jv.setJobIdentifier("0");
        jv.setJobOrigin(ORIGIN);
        jv.setJobPrice(20);
        jv.setJobState(JobStateView.PROPOSED);
        offers.add(jv);

		new NonStrictExpectations() {{
            _tca.requestJob(ORIGIN, DEST, PRICE); result = offers;
		}};

        endresult = _bp.requestTransport(ORIGIN, DEST, PRICE);

        assertEquals("0", endresult);

	//	ArrayList<TransporterPort> _ports = new ArrayList<TransporterPort>();
    //	Collection<String> wsUrls;

	//UpaTransporter1;
    //	UpaTransporter2;
    //	transport;
    //	wsUrls.add();

    }

    /*
    @Test(expected = InvalidPriceFault_Exception.class)
    public void wrongPrice(){}
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Leiria";
    	final int PRICE = -1;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
   
    @Test(expected = UnavailableTransportFault_Exception.class)
    public void nonAvailableTransportForTrip(@Mocked final TransporterClient transClient, @Mocked final TransporterPort transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	final String ORIGIN = "Porto";
    	final String DEST = "Faro";
    	final int PRICE = 30;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);
    }
     
    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void nonAvailableTransportByPrice(@Mocked final TransporterClient transClient, @Mocked final TransporterPort transport)
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
    public void gettingTransport(@Mocked final TransporterClient transClient, @Mocked final TransporterPort transport)
    		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
    		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
    	
    	
    	final String ORIGIN = "Lisboa";
    	final String DEST = "Beja";
    	final int PRICE = 30;
    	
    	BrokerPort broker = new BrokerPort(transClient);
    	
    	broker.requestTransport(ORIGIN, DEST, PRICE);    	
    }
    
    @Test
    public void twoTransportsSamePrice(@Mocked final TransporterClient transClient, @Mocked final TransporterPort transport)
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
    public void listingTransportsStates(@Mocked final TransporterClient transClient, @Mocked final TransporterPort transport) {
    	
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
        _bp.clearTransports();
    }
*/

}




