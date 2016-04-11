package pt.upa.transporter.ws;

import org.junit.*;

import static org.junit.Assert.assertEquals;


public class DecideJobTest {

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
    	
    	tp = new TransporterPort(regions, "UpaTransporter3");
    }

    @After
    public void tearDown() {
    	tp.clearJobs();
    	tp = null;
    }

    // tests

    @Test
    public void sucess() throws BadJobFault_Exception {
    	JobView jv = new JobView();
    	jv.setJobState(JobStateView.PROPOSED);
    	jv.setJobIdentifier("129db");
    	
    	tp.listJobs().add(jv);
    	
    	assertEquals(jv,tp.decideJob("129db", true));
    }
    
    @Test
    public void fail() throws BadJobFault_Exception {
    	JobView jv = new JobView();
    	jv.setJobIdentifier("h31l0");
    	jv.setJobState(JobStateView.PROPOSED);
    	
    	tp.listJobs().add(jv);
    	
    	assertEquals(jv,tp.decideJob("h31l0", false));
  
    }
    
    @Test(expected = BadJobFault_Exception.class)
    public void exception() throws BadJobFault_Exception {
    	tp.decideJob("3x(3pt10n", true);
    }
}
    