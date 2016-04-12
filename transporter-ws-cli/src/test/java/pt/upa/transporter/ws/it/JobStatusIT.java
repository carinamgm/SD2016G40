package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

public class JobStatusIT{
    // static members
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaTransporter";
    private static TransporterClient _tc;


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
        UDDINaming uddi = null;
        try {
            uddi = new UDDINaming(_uddiURL);
            Collection<String> wsUrls = uddi.list(_serviceName + "%");
            _tc = new TransporterClient(wsUrls);

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        _tc = null;
    }

    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
        for(TransporterPortType tp : _tc.getPorts()){
            try{
                tp.requestJob("Lisboa", "Leiria", 0);
            }
            catch(Exception e){}
        }

    }

    @After
    public void tearDown() {
        _tc.clearTransports();
    }


    // tests

 /*   @Test
    public void sucess() {
        JobView jvBraga = _tc.jobStatus("0");
        JobView toBeCompared = null;
        for(TransporterPortType tp : _tc.getPorts())
            toBeCompared = tp.jobStatus("0");
        assertEquals(jvBraga,toBeCompared);
    }

    @Test
    public void fail(){
        assertNull(_tc.jobStatus("-1"));
    }
*/
}