package pt.upa.broker.ws;


import com.sun.corba.se.pept.broker.Broker;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.*;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BrokerPortTest {

	// static members
    @Mocked TransporterClient _tca;
    private String _upa1 = "UpaTransporter1";
    private String _upa2 = "UpaTransporter2";

	// one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

	// members

    // initialization and clean-up for each test

    @Before
    public void setUp() {

	}

   @After
    public void tearDown() {
    }

    
    @Test
    public void simpleAccept() throws Exception {
        // Preparation code not specific to JMockit, if any.
        String origin = "Lisboa";
        String destination = "Porto";
        String id = "0";
        int price = 0;
        JobView jv = createJobView(JobStateView.PROPOSED, price, origin, destination, id, _upa2);


        // METES O QUE ESPERAS QUE O TRANSPORTER CLIENT RETORNE AO BROKER OU SEJA QUANDO O BROKER FOR CHAMAR O TRANSPORTER CLIENT O QUE VAI RECEBER PARA OPERAR SOBRE ESSES DADOS
        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            _tca.requestJob("Lisboa", "Porto", 0);
            result = jv;
            _tca.decideJob(id, true);
            jv.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id);
            result = jv;
        }};

        // EXECUTAS OS METODOS DO BROKER NORMALMENTE
        // Unit under test is exercised.
        BrokerPort bp = new BrokerPort(_tca);


        String idOfRequestedJob = bp.requestTransport(origin, destination, 0);
        TransportView outputTv = bp.viewTransport(id);

        // O NUMERO DE VEZES QUE PODE SER CHAMADA CADA FUNCAO DO TRANSPORTER CLIENT A PARTIR DO BROKER COM X ARGUMENTOS
        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            _tca.requestJob(origin, destination, price); maxTimes = 1;
            _tca.decideJob(id, true); maxTimes = 1;
            _tca.jobStatus(id); maxTimes = 1;
        }};

        // FAZES OS TESTES AQUI DOS ASSERTS
        // Additional verification code, if any, either here or before the verification block.
        assertEquals("The Correct ID was not returned.", id, idOfRequestedJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id)){
                assertTrue(comparateTransportsViews(outputTv,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }

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
/*
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

    }*/

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

    /* Aux functions */

    private JobView createJobView(JobStateView state, int price, String destination, String origin, String id, String companyName){
        JobView jb = new JobView();
        jb.setJobState(state);
        jb.setJobPrice(price);
        jb.setJobDestination(destination);
        jb.setJobOrigin(origin);
        jb.setJobIdentifier(id);
        jb.setCompanyName(companyName);
        return jb;
    }


    private boolean comparateTransportsViews(TransportView tv1, TransportView tv2){
        return tv1.getState() == tv2.getState() && tv1.getPrice() == tv2.getPrice() && tv1.getDestination().equals(tv2.getDestination()) &&
                tv1.getOrigin().equals(tv2.getOrigin()) && tv1.getTransporterCompany().equals(tv2.getTransporterCompany());
    }



}