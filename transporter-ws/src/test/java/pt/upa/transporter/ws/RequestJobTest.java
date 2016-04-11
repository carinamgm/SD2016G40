package pt.upa.transporter.ws;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestJobTest {


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    // members
    private TransporterPort tpp;
    private TransporterPort tpi;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	
    	String[][] regions = {{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"},
    			{"Lisboa","Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"},
    			{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"}};
    	
    	tpp = new TransporterPort(regions, "UpaTransporter2");
        tpi = new TransporterPort(regions, "UpaTransporter1");

    }

    @After
    public void tearDown() {
    	tpp.clearJobs();
    	tpp = null;
        tpi.clearJobs();
        tpi = null;
    }


    // tests with pair transporter

    @Test(expected = BadPriceFault_Exception.class)
    public void pNegativePrice() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	tpp.requestJob("Porto", "Braga", -12);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void pUnknownOrigin() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	tpp.requestJob("Londres", "Barcelona", 15);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void pDoNotWorkSouth() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpp.requestJob("Setúbal", "Portalegre", 40);
    }

    @Test
    public void pWorkNorth() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpp.requestJob("Braga", "Viana do Castelo", 32);
    }
    
    @Test
    public void pSuccessCenter() throws BadPriceFault_Exception, BadLocationFault_Exception {
        // Id's are set in Transporter Client, so to test this we need to set the identifier
        JobView toCompare = tpp.requestJob("Santarém", "Lisboa", 0);

        // We can't compair objects because they will be different
    	assertEquals("UpaTransporter2",toCompare.getCompanyName());
        assertEquals("Lisboa",toCompare.getJobDestination());
        assertEquals("Santarém",toCompare.getJobOrigin());
        assertEquals(JobStateView.PROPOSED,toCompare.getJobState());
        assertEquals(0,toCompare.getJobPrice());
    }

    @Test
    public void pPriceAbove100() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	assertEquals(null, tpp.requestJob("Lisboa","Leiria", 149));
    }

    @Test
    public void pPriceUnder100Above10Impair() throws BadPriceFault_Exception, BadLocationFault_Exception {
        JobView jv = tpp.requestJob("Lisboa","Leiria", 33);
        assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() > 33);
    }

    @Test
    public void pPriceUnder100Above10Pair() throws BadPriceFault_Exception, BadLocationFault_Exception {
        JobView jv = tpp.requestJob("Lisboa","Leiria", 12);
        assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() < 12);
    }
    
    @Test
    public void pPriceUnder10() throws BadPriceFault_Exception, BadLocationFault_Exception {
    	JobView jv = tpp.requestJob("Viseu", "Vila Real", 7);
    	assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() < 7);
    }

    // test with a impair transporter

    @Test(expected = BadPriceFault_Exception.class)
    public void iNegativePrice() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpi.requestJob("Beja", "Faro", -12);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void iUnknownOrigin() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpi.requestJob("Ibiza", "Roma", 15);
    }

    @Test
    public void iWorkSouth() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpi.requestJob("Setúbal", "Portalegre", 40);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void iDontWorkNorth() throws BadPriceFault_Exception, BadLocationFault_Exception {
        tpi.requestJob("Braga", "Viana do Castelo", 32);
    }

    @Test
    public void iSuccessCenter() throws BadPriceFault_Exception, BadLocationFault_Exception {
        // Id's are set in Transporter Client, so to test this we need to set the identifier
        JobView toCompare = tpi.requestJob("Coimbra", "Aveiro", 0);

        // We can't compair objects because they will be different
        assertEquals("UpaTransporter1",toCompare.getCompanyName());
        assertEquals("Aveiro",toCompare.getJobDestination());
        assertEquals("Coimbra",toCompare.getJobOrigin());
        assertEquals(JobStateView.PROPOSED,toCompare.getJobState());
        assertEquals(0,toCompare.getJobPrice());
    }

    @Test
    public void iPriceAbove100() throws BadPriceFault_Exception, BadLocationFault_Exception {
        assertEquals(null, tpi.requestJob("Lisboa","Leiria", 149));
    }

    @Test
    public void iPriceUnder100Above10Impair() throws BadPriceFault_Exception, BadLocationFault_Exception {
        JobView jv = tpi.requestJob("Lisboa","Leiria", 33);
        assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() < 33);
    }

    @Test
    public void iPriceUnder100Above10Pair() throws BadPriceFault_Exception, BadLocationFault_Exception {
        JobView jv = tpi.requestJob("Lisboa","Leiria", 12);
        assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() > 12);
    }

    @Test
    public void iPriceUnder10() throws BadPriceFault_Exception, BadLocationFault_Exception {
        JobView jv = tpi.requestJob("Faro", "Lisboa", 7);
        assertTrue("Preço mais baixo que o pedido", jv.getJobPrice() < 7);
    }



}