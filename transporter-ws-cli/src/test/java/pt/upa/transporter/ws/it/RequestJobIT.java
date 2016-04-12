package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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


    // test for parity - pair

    @Test
    public void sucessPair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        testSucess("Porto","Lisboa",0,0);
    }

    @Test
    public void unsucessPairLocation() throws BadPriceFault_Exception, BadLocationFault_Exception{
        testUnsucess("Lisboa","Faro",0,0);
    }

    @Test
    public void above100Pair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        assertNull(_tc.getTransporterClient().requestJob("Lisboa","Porto",101));

    }

    @Test(expected = BadPriceFault_Exception.class)
    public void negativePricePair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        assertNull(_tc.getTransporterClient().requestJob("Lisboa","Porto",-1));
    }

    @Test
    public void under10Pair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        underPrices("Lisboa","Bragança",10,0);
    }

    @Test
    public void under100PairPriceP() throws BadPriceFault_Exception, BadLocationFault_Exception{
        underPrices("Viana do Castelo","Vila Real",100,0);
    }

    @Test
    public void under100PairPriceI() throws BadPriceFault_Exception, BadLocationFault_Exception{
        greaterPrices("Viana do Castelo","Vila Real",33,0);
    }

    // test for parity - unpair

    @Test
    public void sucessImpair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        testSucess("Lisboa","Faro",0,1);
    }

    @Test
    public void unsucessImpairLocation() throws BadPriceFault_Exception, BadLocationFault_Exception{
        testUnsucess("Porto","Lisboa",0,1);
    }

    @Test
    public void above100Unpair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        assertNull(_tc.getTransporterClient().requestJob("Lisboa","Beja",101));

    }

    @Test(expected = BadPriceFault_Exception.class)
    public void negativePriceUnpair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        assertNull(_tc.getTransporterClient().requestJob("Lisboa","Faro",-1));

    }

    @Test
    public void under10Unpair() throws BadPriceFault_Exception, BadLocationFault_Exception{
        underPrices("Lisboa","Faro",10,1);
    }

    @Test
    public void under100UnpairPriceP() throws BadPriceFault_Exception, BadLocationFault_Exception{
        greaterPrices("Viseu","Beja",100,1);
    }

    @Test
    public void under100UnpairPriceI() throws BadPriceFault_Exception, BadLocationFault_Exception{
        underPrices("Viseu","Beja",53,1);
    }


    // Common pair and unpair tests

    @Test
    public void sucess() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Lisboa","Coimbra",0);
        for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++)
            assertTrue(_tc.getTransporterClient().getJobs().get(String.valueOf(i)).getJobPrice() == 0);
    }

    @Test(expected = BadPriceFault_Exception.class)
    public void insucessPrice() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Lisboa","Coimbra",-1);
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void insucessLocations() throws BadPriceFault_Exception, BadLocationFault_Exception {
        _tc.getTransporterClient().requestJob("Brasil","Holanda",0);
    }

    public void under10() throws BadPriceFault_Exception, BadLocationFault_Exception{
        _tc.getTransporterClient().requestJob("Lisboa","Coimbra",10);
        priceLessThan(10);
    }

    public void above100() throws BadPriceFault_Exception, BadLocationFault_Exception{
        _tc.getTransporterClient().requestJob("Lisboa","Coimbra",100);
       assertEquals(0,_tc.getTransporterClient().getJobs().size());
    }

    public void pairPrice() throws BadPriceFault_Exception, BadLocationFault_Exception{
        underPrices("Lisboa","Coimbra",100,0);
        greaterPrices("Lisboa","Coimbra",100,1);
    }

    public void unpairPrice() throws BadPriceFault_Exception, BadLocationFault_Exception{
        greaterPrices("Lisboa","Coimbra",53,0);
        underPrices("Lisboa","Coimbra",53,1);
    }

    // Aux functions to grant parity tests

    private void priceLessThan(int price) {
        for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++)
            assertTrue(_tc.getTransporterClient().getJobs().get(String.valueOf(i)).getJobPrice() < price);
    }

    private void priceGreaterThan(int price) {
        for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++)
            assertTrue(_tc.getTransporterClient().getJobs().get(String.valueOf(i)).getJobPrice() > price);
    }

    private void underPrices(String origem, String destino, int price, int parity) throws BadPriceFault_Exception, BadLocationFault_Exception{
        if(getPortsAccordingToParity(parity).size() != 0) {
            _tc.getTransporterClient().requestJob(origem,destino,price);
            assertEquals(_tc.getTransporterClient().getJobs().size(), getPortsAccordingToParity(parity).size());
            priceLessThan(price);
        }
        else{ assertNull(null); }
    }

    private void greaterPrices(String origem, String destino, int price, int parity) throws BadPriceFault_Exception, BadLocationFault_Exception{
        if(getPortsAccordingToParity(parity).size() != 0) {
            _tc.getTransporterClient().requestJob(origem,destino,price);
            assertEquals(_tc.getTransporterClient().getJobs().size(), getPortsAccordingToParity(parity).size());
            priceGreaterThan(price);
        }
        else{ assertNull(null); }
    }

    private void testSucess(String origem, String destino, int price, int pair) throws BadPriceFault_Exception, BadLocationFault_Exception {
        ArrayList<TransporterPortType> _ports = getPortsAccordingToParity(pair);
        List<JobView> jv = new ArrayList<JobView>();
        boolean checkEquality = false;

        if (_ports.size() != 0){
            _tc.getTransporterClient().requestJob(origem, destino, price);
            for (TransporterPortType tp : _ports) {
                jv.addAll(tp.listJobs());
            }

            assertEquals(_ports.size(), jv.size());
            for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++) {
                checkEquality = false;
                for (JobView j : jv) {
                    checkEquality = _tc.getTransporterClient().equalsJobView(_tc.getTransporterClient().getJobs().get(String.valueOf(i)), j);
                    if(checkEquality) {
                        jv.remove(j);
                        break;
                    }
                }
                assertEquals(true, checkEquality);
            }
        }
        else{ assertNull(null); }

    }

    private void testUnsucess(String origem, String destino, int price, int pair) throws BadPriceFault_Exception, BadLocationFault_Exception{
        boolean fail = false;
        String companyName;

        _tc.getTransporterClient().requestJob(origem,destino,price);
        for(int i = 0 ; i < _tc.getTransporterClient().getJobs().size(); i++) {
            companyName = _tc.getTransporterClient().getJobs().get(String.valueOf(i)).getCompanyName();
            if(Integer.valueOf(companyName.substring(_tc.getServiceName().length(), companyName.length())) % 2 == pair){
                fail = true;
            }
        }
        assertEquals(fail,false);
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