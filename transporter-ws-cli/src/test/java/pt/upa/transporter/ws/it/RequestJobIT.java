package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import static org.junit.Assert.assertEquals;

import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

public class RequestJobIT{
    // static members
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaTransporter";
    private static TransporterClient _tc;
    private static ArrayList<String> _transporterNames = new ArrayList<String>();

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
        UDDINaming uddi = null;
        try {
            uddi = new UDDINaming(_uddiURL);
            Collection<String> wsUrls = uddi.list(_serviceName + "%");
            for(String s : wsUrls){
                String aux = s.substring(s.lastIndexOf(":"));
                _transporterNames.add(_serviceName + aux.substring(4,aux.indexOf('/')));
            }
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
    }

    @After
    public void tearDown() {
        _tc.clearTransports();
    }


    // tests

    @Test
    public void sucess() throws BadPriceFault_Exception, BadLocationFault_Exception {
        /*
        ArrayList<JobView> proposals = _tc.requestJob("Lisboa","Coimbra",0);
        for(JobView jv :proposals)
            System.out.println(jv.getJobOrigin());


        /System.out.println("ssssssssssssssssssssssssssssss");


        JobView result = _tc.jobStatus("0");
        System.out.println(result.getJobOrigin());

        System.out.println("ssssssssssssssssssssssssssssss");*/



    }

}