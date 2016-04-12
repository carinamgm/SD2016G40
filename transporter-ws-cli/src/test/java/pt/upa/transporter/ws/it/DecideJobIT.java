package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

public class DecideJobIT{
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
    }

}