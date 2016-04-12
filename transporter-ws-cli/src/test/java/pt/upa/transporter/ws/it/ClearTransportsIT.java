package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ClearTransportsIT{
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
    public void sucess() {
        _tc.clean();
        int sizeCombined = 0;
        for(TransporterPortType tp : _tc.getTransporterClient().getPorts())
            sizeCombined += tp.listJobs().size();
        assertEquals("Clear transports",0,sizeCombined);
    }

}