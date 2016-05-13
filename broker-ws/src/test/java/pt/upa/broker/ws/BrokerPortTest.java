package pt.upa.broker.ws;


import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.*;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BrokerPortTest {

	// static members
    @Mocked TransporterClient _tca;
    private String _upa1 = "UpaTransporter1";
    private String _upa2 = "UpaTransporter2";
    private String _upa3 = "UpaTransporter3";
    private String _upa4 = "UpaTransporter4";

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

	}

    @After
    public void tearDown() {
    }

    
    @Test
    public void simpleEvenAccept() throws Exception {
        // Preparation code not specific to JMockit, if any.
        String origin = "Lisboa";
        String destination = "Porto";
        String id = "0";
        int budgetPrice = 20;
        int jobPrice = 10;
        JobView jv = createJobView(JobStateView.PROPOSED, jobPrice, origin, destination, id, _upa2);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = jv;
            _tca.decideJob(id, true);
            jv.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id);
            result = jv;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        String idOfRequestedJob = bp.requestTransport(origin, destination, budgetPrice);
        TransportView outputTv = bp.viewTransport(id);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
            _tca.decideJob(id, true); maxTimes = 1;
            _tca.jobStatus(id); maxTimes = 1;
        }};

        assertEquals("The Correct ID was not returned.", id, idOfRequestedJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id)){
                assertTrue(comparateTransportsViews(outputTv,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }

    }

    @Test
    public void simpleOddAccept() throws Exception {
        String origin = "Faro";
        String destination = "Lisboa";
        String id = "0";
        int budgetPrice = 25;
        int jobPrice = 12;
        JobView jv = createJobView(JobStateView.PROPOSED, jobPrice, origin, destination, id, _upa1);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = jv;
            _tca.decideJob(id, true);
            jv.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id);
            result = jv;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        String idOfRequestedJob = bp.requestTransport(origin, destination, budgetPrice);
        TransportView outputTv = bp.viewTransport(id);

        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
            _tca.decideJob(id, true); maxTimes = 1;
            _tca.jobStatus(id); maxTimes = 1;
        }};

        assertEquals("The Correct ID was not returned.", id, idOfRequestedJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id)){
                assertTrue(comparateTransportsViews(outputTv,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }
    }


    @Test(expected = InvalidPriceFault_Exception.class)
    public void negativePrice() throws Exception {
        String origin = "Faro";
        String destination = "Lisboa";
        int budgetPrice = -5;

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            {
                BadPriceFault bf = new BadPriceFault();
                bf.setPrice(budgetPrice);
                result = new BadPriceFault_Exception("Prices can't be negative", bf);
            }
        }};

        BrokerPort bp = new BrokerPort(_tca);

        bp.requestTransport(origin, destination, budgetPrice);

        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
        }};

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            assertEquals(TransportStateView.FAILED, tv.getState());
        }
    }

    @Test(expected = UnavailableTransportFault_Exception.class)
    public void nonAvailableTransportForTrip() throws Exception {
        String origin = "Faro";
        String destination = "Lisboa";
        int budgetPrice = 101;

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = null;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        bp.requestTransport(origin, destination, budgetPrice);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
        }};

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1, tvs.size());

        for (TransportView tv : tvs) {
            assertEquals(TransportStateView.FAILED, tv.getState());
        }
    }


    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void nonAvailableTransportByPrice() throws Exception {
        String origin = "Setúbal";
        String destination = "Lisboa";
        String id = "0";
        int budgetPrice = 12;
        int jobPrice = 20;
        JobView jv = createJobView(JobStateView.PROPOSED, jobPrice, origin, destination, id, _upa1);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = jv;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        bp.requestTransport(origin, destination, budgetPrice);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
        }};

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1, tvs.size());

        for (TransportView tv : tvs) {
            assertEquals(TransportStateView.FAILED, tv.getState());
        }
    }

    @Test
    public void differentTransportersSameRegion() throws Exception {

    	final String origin = "Leiria";
    	final String destination = "Santarém";
        String id = "0";
    	int budgetPrice = 30;
        int jobPriceupa1 = 40;
        int jobPriceupa2 = 10;
        JobView jv1 = createJobView(JobStateView.PROPOSED, jobPriceupa1, origin, destination, id, _upa1);
        JobView jv2 = createJobView(JobStateView.PROPOSED, jobPriceupa2, origin, destination, id, _upa2);
        ArrayList<JobView> listjv = new ArrayList<JobView>();
        listjv.add(jv1);
        listjv.add(jv2);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = listjv;
            _tca.decideJob(id, true);
            jv2.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id);
            result = jv2;
        }};

        BrokerPort bp = new BrokerPort(_tca);


        String idOfRequestedJob = bp.requestTransport(origin, destination, budgetPrice);
        TransportView outputTv = bp.viewTransport(id);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
            _tca.decideJob(id, true); maxTimes = 1;
            _tca.jobStatus(id); maxTimes = 1;
        }};

        assertEquals("The Correct ID was not returned.", id, idOfRequestedJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id)){
                assertTrue(comparateTransportsViews(outputTv,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }
    }

    @Test
    public void OddTransportersSameRegion() throws Exception {

        final String origin = "Faro";
        final String destination = "Santarém";
        String id = "0";
        int budgetPrice = 25;
        int jobPrice = 10;
        JobView jv1 = createJobView(JobStateView.PROPOSED, jobPrice, origin, destination, id, _upa1);
        JobView jv3 = createJobView(JobStateView.PROPOSED, jobPrice, origin, destination, id, _upa3);
        ArrayList<JobView> listjv = new ArrayList<JobView>();
        listjv.add(jv1);
        listjv.add(jv3);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            result = listjv;
            _tca.decideJob(id, true);
            jv3.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id);
            result = jv3;
        }};

        BrokerPort bp = new BrokerPort(_tca);


        String idOfRequestedJob = bp.requestTransport(origin, destination, budgetPrice);
        TransportView outputTv = bp.viewTransport(id);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
            _tca.decideJob(id, true); maxTimes = 1;
            _tca.jobStatus(id); maxTimes = 1;
        }};

        assertEquals("The Correct ID was not returned.", id, idOfRequestedJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(1,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id)){
                assertTrue(comparateTransportsViews(outputTv,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void wrongDestinationCity() throws Exception {
        String origin = "Lisboa";
        String destination = "Caldas da Rainha";
        int budgetPrice = 20;

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetPrice);
            {
                BadLocationFault blf = new BadLocationFault();
                blf.setLocation(origin + " " + destination);
                result = new BadLocationFault_Exception("Invalid Routes " + origin + " - " + destination, blf);
            }
        }};

        BrokerPort bp = new BrokerPort(_tca);

        bp.requestTransport(origin, destination, budgetPrice);

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetPrice); maxTimes = 1;
        }};
    }

    @Test
    public void multipleTransportersMultipleTransports() throws Exception {

        final String origin = "Lisboa";
        final String destination = "Santarém";
        String ideven = "0";
        String idodd = "1";
        int budgetjvodd = 25;
        int budgetjveven = 20;
        int jobPricejvodd = 15;
        int jobPricejveven = 10;
        JobView jv2 = createJobView(JobStateView.PROPOSED, jobPricejveven, origin, destination, ideven, _upa2);
        JobView jv1 = createJobView(JobStateView.PROPOSED, jobPricejvodd, origin, destination, idodd, _upa1);
        JobView jv3 = createJobView(JobStateView.PROPOSED, jobPricejvodd, origin, destination, idodd, _upa3);
        ArrayList<JobView> listjvodd = new ArrayList<JobView>();
        listjvodd.add(jv1);
        listjvodd.add(jv3);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetjveven);
            result = jv2;
            _tca.decideJob(ideven, true);
            jv2.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(ideven);
            result = jv2;

            _tca.requestJob(origin, destination, budgetjvodd);
            result = listjvodd;
            _tca.decideJob(idodd, true);
            jv1.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(idodd);
            result = jv1;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        String idOfEvenJob = bp.requestTransport(origin, destination, budgetjveven);
        String idOfOddJob = bp.requestTransport(origin, destination, budgetjvodd);

        TransportView outputTvEvenJob = bp.viewTransport(ideven);
        TransportView outputTvOddJob = bp.viewTransport(idodd);;

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetjveven); maxTimes = 1;
            _tca.decideJob(ideven, true); maxTimes = 1;
            _tca.jobStatus(ideven); maxTimes = 1;
            _tca.requestJob(origin, destination, budgetjvodd); maxTimes = 1;
            _tca.decideJob(idodd, true); maxTimes = 1;
            _tca.jobStatus(idodd); maxTimes = 1;
        }};

        assertEquals("The Correct ID for Even Transporter was not returned.", ideven, idOfEvenJob);
        assertEquals("The Correct ID for Odd Transporter was not returned.", idodd, idOfOddJob);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(2,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(ideven)){
                assertTrue(comparateTransportsViews(outputTvEvenJob,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
            if(tv.getId().equals(idodd)){
                assertTrue(comparateTransportsViews(outputTvOddJob,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
        }
    }

    @Test
    public void multipleTransportersMultipleStates() throws Exception {

        final String originJob1 = "Castelo Branco";
        final String originJob2 = "Lisboa";
        final String originJob3 = "Viana do Castelo";
        final String destinationForAllJobs = "Guarda";
        final String id1 = "0";
        final String id2 = "1";
        final String id3 = "2";
        final int budget1 = 25;
        final int budget2 = 20;
        final int budget3 = 10;
        final int jobPrice1 = 10;
        final int jobPrice2 = 10;
        final int jobPrice3 = 8;

        JobView jv1 = createJobView(JobStateView.PROPOSED, jobPrice1, originJob1, destinationForAllJobs, id1, _upa1);
        JobView jv2 = createJobView(JobStateView.PROPOSED, jobPrice2, originJob2, destinationForAllJobs, id2, _upa2);
        JobView jv3 = createJobView(JobStateView.PROPOSED, jobPrice3, originJob3, destinationForAllJobs, id3, _upa3);

        ArrayList<JobView> listjvall = new ArrayList<JobView>();
        listjvall.add(jv1);
        listjvall.add(jv2);
        listjvall.add(jv3);

        new Expectations() {{
            //Expectations for the 1st Job
            _tca.requestJob(originJob1, destinationForAllJobs, budget1);
            result = jv1;
            _tca.decideJob(id1, true);
            jv1.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id1);
            result = jv1;

            //Expectations for the 2nd Job
            _tca.requestJob(originJob2, destinationForAllJobs, budget2);
            result = jv2;
            _tca.decideJob(id2, true);
            jv2.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id2);
            {
                jv2.setJobState(JobStateView.HEADING);
                result = jv2;
            }

            //Expectations for the 3rd Job
            _tca.requestJob(originJob3, destinationForAllJobs, budget3);
            result = listjvall;
            _tca.decideJob(id3, true);
            jv3.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(id3);
            {
                jv3.setJobState(JobStateView.COMPLETED);
                result = jv3;
            }
        }};

        BrokerPort bp = new BrokerPort(_tca);

        String idOfJob1 = bp.requestTransport(originJob1, destinationForAllJobs, budget1);
        String idOfJob2 = bp.requestTransport(originJob2, destinationForAllJobs, budget2);
        String idOfJob3 = bp.requestTransport(originJob3, destinationForAllJobs, budget3);

        TransportView outputTvJob1 = bp.viewTransport(id1);
        TransportView outputTvJob2 = bp.viewTransport(id2);
        TransportView outputTvJob3 = bp.viewTransport(id3);

        new Verifications() {{
            _tca.requestJob(originJob1, destinationForAllJobs, budget1); maxTimes = 1;
            _tca.decideJob(id1, true); maxTimes = 1;
            _tca.jobStatus(id1); maxTimes = 1;
            _tca.requestJob(originJob2, destinationForAllJobs, budget2); maxTimes = 1;
            _tca.decideJob(id2, true); maxTimes = 1;
            _tca.jobStatus(id2); maxTimes = 1;
            _tca.requestJob(originJob3, destinationForAllJobs, budget3); maxTimes = 1;
            _tca.decideJob(id3, true); maxTimes = 1;
            _tca.jobStatus(id3); maxTimes = 1;
        }};

        assertEquals("The Correct ID for UpaTransporter1 was not returned.", id1, idOfJob1);
        assertEquals("The Correct ID for UpaTransporter2 was not returned.", id2, idOfJob2);
        assertEquals("The Correct ID for UpaTransporter3 was not returned.", id3, idOfJob3);

        List<TransportView> tvs = bp.listTransports();
        assertEquals(3,tvs.size());

        for(TransportView tv : tvs){
            if(tv.getId().equals(id1)){
                assertTrue(comparateTransportsViews(outputTvJob1,tv));
                assertEquals(TransportStateView.BOOKED, tv.getState());
            }
            if(tv.getId().equals(id2)){
                assertTrue(comparateTransportsViews(outputTvJob2,tv));
                assertEquals(TransportStateView.HEADING, tv.getState());
            }
            if(tv.getId().equals(id3)){
                assertTrue(comparateTransportsViews(outputTvJob3,tv));
                assertEquals(TransportStateView.COMPLETED, tv.getState());
            }
        }
    }

    @Test
    public void clearTransportsSuccessfully() throws Exception{

        final String origin = "Lisboa";
        final String destination = "Santarém";
        String ideven = "0";
        String idodd = "1";
        int budgetjvodd = 25;
        int budgetjveven = 20;
        int jobPricejvodd = 15;
        int jobPricejveven = 10;
        JobView jv2 = createJobView(JobStateView.PROPOSED, jobPricejveven, origin, destination, ideven, _upa2);
        JobView jv1 = createJobView(JobStateView.PROPOSED, jobPricejvodd, origin, destination, idodd, _upa1);
        JobView jv3 = createJobView(JobStateView.PROPOSED, jobPricejvodd, origin, destination, idodd, _upa3);
        ArrayList<JobView> listjvodd = new ArrayList<JobView>();
        listjvodd.add(jv1);
        listjvodd.add(jv3);

        new Expectations() {{
            _tca.requestJob(origin, destination, budgetjveven);
            result = jv2;
            _tca.decideJob(ideven, true);
            jv2.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(ideven);
            result = jv2;

            _tca.requestJob(origin, destination, budgetjvodd);
            result = listjvodd;
            _tca.decideJob(idodd, true);
            jv1.setJobState(JobStateView.ACCEPTED);
            _tca.jobStatus(idodd);
            result = jv1;

            _tca.clearTransports();
            result = null;
        }};

        BrokerPort bp = new BrokerPort(_tca);

        String idOfEvenJob = bp.requestTransport(origin, destination, budgetjveven);
        String idOfOddJob = bp.requestTransport(origin, destination, budgetjvodd);

        TransportView outputTvEvenJob = bp.viewTransport(ideven);
        TransportView outputTvOddJob = bp.viewTransport(idodd);

        assertEquals("Transporter State is incorrect.", TransportStateView.BOOKED , outputTvEvenJob.getState());
        assertEquals("Transporter State is incorrect.", TransportStateView.BOOKED , outputTvOddJob.getState());

        assertEquals("The Correct ID for Even Transporter was not returned.", ideven, idOfEvenJob);
        assertEquals("The Correct ID for Odd Transporter was not returned.", idodd, idOfOddJob);

        bp.clearTransports();

        new Verifications() {{
            _tca.requestJob(origin, destination, budgetjveven); maxTimes = 1;
            _tca.decideJob(ideven, true); maxTimes = 1;
            _tca.jobStatus(ideven); maxTimes = 1;
            _tca.requestJob(origin, destination, budgetjvodd); maxTimes = 1;
            _tca.decideJob(idodd, true); maxTimes = 1;
            _tca.jobStatus(idodd); maxTimes = 1;
            _tca.clearTransports(); maxTimes = 1;
        }};

        List<TransportView> tvs = bp.listTransports();
        assertEquals(0,tvs.size());
    }


    /* Aux functions */

    private JobView createJobView(JobStateView state, int price, String destination, String origin, String id, String companyName){
        JobView jb = new JobView();
        jb.setJobState(state);
        jb.setJobPrice(price);
        jb.setJobDestination(destination);
        jb.setJobOrigin(origin);
        jb.setJobIdentifier(id);
        jb.setCompanyName(companyName);
        return jb;
    }


    private boolean comparateTransportsViews(TransportView tv1, TransportView tv2){
        return tv1.getState() == tv2.getState() && tv1.getPrice() == tv2.getPrice() && tv1.getDestination().equals(tv2.getDestination()) &&
                tv1.getOrigin().equals(tv2.getOrigin()) && tv1.getTransporterCompany().equals(tv2.getTransporterCompany());
    }

}