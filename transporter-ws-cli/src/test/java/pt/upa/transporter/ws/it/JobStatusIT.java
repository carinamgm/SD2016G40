package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JobStatusIT {
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

        for (TransporterPortType tp : _tc.getTransporterClient().getPorts()) {
            try {
                tp.requestJob("Lisboa", "Leiria", 0);
            } catch (Exception e) {
            }
        }
    }

    @After
    public void tearDown() {
        _tc.clean();
        _tc = null;
    }


    // tests

    @Test
    public void sucess() {
        int i = 0;
        for (TransporterPortType tp : _tc.getTransporterClient().getPorts()) {
            JobView jv = tp.jobStatus("0");
            assertEquals("0", jv.getJobIdentifier());
            assertEquals(0, jv.getJobPrice());
            assertEquals("Lisboa", jv.getJobOrigin());
            assertEquals("Leiria", jv.getJobDestination());
            assertEquals(_tc.getTransporterNames().get(i), jv.getCompanyName());
            assertEquals(JobStateView.PROPOSED, jv.getJobState());
            i++;
        }
    }

    @Test
    public void fail() {
        assertNull(_tc.getTransporterClient().jobStatus("-1"));
    }

}