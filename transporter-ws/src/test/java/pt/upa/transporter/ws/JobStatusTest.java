package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;


public class JobStatusTest {

    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members
    private TransporterPort tp;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	
    	String[][] regions = {{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"},
    			{"Lisboa","Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"},
    			{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"}};
    	
    	tp = new TransporterPort(regions, "UpaTransporter2");
    }

    @After
    public void tearDown() {
    	tp.clearJobs();
    	tp = null;
    }


    // tests

    @Test
    public void invalidID() {
    	assertEquals(null, tp.jobStatus("I'mSoInvalidICouldDie"));
    }

    @Test
    public void success() {
    	JobView jv = new JobView();
    	jv.setJobIdentifier("12345");
    	jv.setCompanyName("UpaTransporter2");
    	jv.setJobDestination("Bragança");
    	jv.setJobOrigin("Viseu");
    	jv.setJobState(JobStateView.ONGOING);
    	jv.setJobPrice(3);
    	
    	tp.listJobs().add(jv);
    	
    	assertEquals(jv, tp.jobStatus("12345"));
    	
    }
    
}