package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.TransporterClientApplication;
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

                tp.requestJob("Porto", "Lisboa", 10);
                tp.requestJob("Porto", "Lisboa", 33);
                tp.requestJob("Porto", "Lisboa", 100);
                tp.requestJob("Porto", "Lisboa", 101);

                tp.requestJob("Beja", "Lisboa", 10);
                tp.requestJob("Beja", "Lisboa", 33);
                tp.requestJob("Beja", "Lisboa", 100);
                tp.requestJob("Beja", "Lisboa", 101);
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
        boolean valid = false;
        for (TransporterPortType tp : _tc.getTransporterClient().getPorts()) {
            valid = false;
            for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++) {
                valid = _tc.getTransporterClient().equalsJobView(_tc.getTransporterClient().getJobs().get(String.valueOf(i)),
                        tp.jobStatus(_tc.getTransporterClient().getJobs().get(String.valueOf(i)).getJobIdentifier()));
                if(valid){
                    _tc.getTransporterClient().getJobs().remove(i);
                    break;
                }
            }
        }
        assertEquals(0,_tc.getTransporterClient().getJobs().size());


    }

    @Test
    public void fail() {
        assertNull(_tc.getTransporterClient().jobStatus("-1"));
    }

}