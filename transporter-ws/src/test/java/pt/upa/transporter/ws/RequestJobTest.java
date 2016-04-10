package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

public class RequestJobTest {


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

    @Test(expected = BadPriceFault_Exception.class)
    public void negativePrice() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	tp.requestJob("Faro", "Beja", -12);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void unknownOrigin() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	tp.requestJob("Londres", "Beja", 15);
    }
    
    @Test
    public void doNotWorkSouth() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	if(tp.getTransporterNumber() % 2 == 0)
    		assertEquals(null, tp.requestJob("Setúbal", "Portalegre", 40));
    }
    
    @Test
    public void doNotWorkNorth() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	if(tp.getTransporterNumber() % 2 != 0)
    		assertEquals(null, tp.requestJob("Braga", "Viana do Castelo", 32));
    }
    
    @Test
    public void successCenter() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	JobView jv = new JobView();
    	jv.setCompanyName("UpaTransporter2");
    	jv.setJobDestination("Lisboa");
    	jv.setJobOrigin("Santarém");
    	jv.setJobState(JobStateView.PROPOSED);
    	jv.setJobPrice(0);

    	assertEquals(jv,tp.requestJob("Santarém", "Lisboa", 0));
    }

    @Test
    public void priceAbove100() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	assertEquals(null, tp.requestJob("Lisboa","Leiria", 149));
    }
    
    @Test
    public void priceUnder10() throws BadPriceFault_Exception, BadLocationFault_Exception {    	
    	JobView jv = tp.requestJob("Viseu", "Vila Real", 7);
    	assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() > 7);
    }
}