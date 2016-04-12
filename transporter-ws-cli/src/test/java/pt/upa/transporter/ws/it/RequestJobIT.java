package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestJobIT{
    // static members
    private static TransporterClientApplication _tc;

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
        _tc = new TransporterClientApplication();
        _tc.setup();
    }

    @After
    public void tearDown() {
        _tc.clean();
        _tc = null;
    }


    // tests

    @Test
    public void sucess() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Lisboa","Coimbra",0);

        int i = 0;
        for(TransporterPortType tp : _tc.getTransporterClient().getPorts()){
            JobView jv = tp.jobStatus("0");
            assertEquals("0",jv.getJobIdentifier());
            assertEquals(0,jv.getJobPrice());
            assertEquals("Lisboa",jv.getJobOrigin());
            assertEquals("Coimbra",jv.getJobDestination());
            assertEquals(_tc.getTransporterNames().get(i),jv.getCompanyName());
            assertEquals(JobStateView.PROPOSED,jv.getJobState());
            i++;
        }
    }

    @Test
    public void sucesss() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Porto","Lisboa",0);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void failLocation() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Paris","Ibiza",0);
    }

    @Test
    public void failPriceAbove100() throws BadPriceFault_Exception, BadLocationFault_Exception {
        assertNull(_tc.getTransporterClient().requestJob("Lisboa","Coimbra",140));
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void failPriceNegative() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Coimbra","Lisboa",-1);
    }



    private ArrayList<TransporterPortType> getPortsAccordingToParity(int rest){
        ArrayList<TransporterPortType> outputPorts = new ArrayList<TransporterPortType>();
        int index = 0;
        for(TransporterPortType tp : _tc.getTransporterClient().getPorts()){
            char number = _tc.getTransporterNames().get(index).charAt(_tc.getTransporterNames().get(index).length()-1);
            if(Character.getNumericValue(number) % 2 == rest)
                outputPorts.add(tp);
            index++;
        }
        return outputPorts;
    }


}