package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.TransporterClientApplication;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DecideJobIT{
    private static final int _maxRunTime = 15000; // 15 sec in milisecs
    private static final int _second = 1000; // 1 sec = 1000 milisecs
    private static TransporterClientApplication _tc;
    static int _phase1 = 0;
    static int _phase2 = 0;
    static int _phase3 = 0;
    static int _elapsed = 0;

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

        try {
            _tc.getTransporterClient().requestJob("Lisboa","Coimbra",0);

        } catch (Exception e) {
        }
    }

    @After
    public void tearDown() {
        _tc.clean();
        _tc = null;
        _phase1 = _phase2 = _phase3 = 0;
    }


    // tests

    @Test
    public void sucess() throws BadJobFault_Exception {
        for (int i = 0; i < _tc.getTransporterClient().getJobs().size(); i++) {
            if(i % 2 == 0) {
                _tc.getTransporterClient().decideJob(String.valueOf(i), true);
                assertEquals(_tc.getTransporterClient().jobStatus(String.valueOf(i)).getJobState(),JobStateView.ACCEPTED);
            }
            else {
                _tc.getTransporterClient().decideJob(String.valueOf(i), false);
                assertEquals(_tc.getTransporterClient().jobStatus(String.valueOf(i)).getJobState(),JobStateView.REJECTED);
            }
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                checkProgression(t);
            }
        },0,_second);

        while(_phase3 == 0){
            sleep(1);
        }

        assertEquals(3, _phase1 + _phase2 + _phase3);
        assertTrue(_elapsed < _maxRunTime);
    }

    private void sleep(int time){
        try {
            TimeUnit.SECONDS.sleep(time);
        }
        catch(Exception e){}
    }


    private void checkProgression(Timer t){
        _elapsed += _second;

        JobView jv = _tc.getTransporterClient().jobStatus("0");

        if(jv.getJobState() == JobStateView.HEADING){;
            _phase1 = 1;
        }

        if(jv.getJobState() == JobStateView.ONGOING){;
            _phase2 = 1;
        }

        if(jv.getJobState() == JobStateView.COMPLETED){
            _phase3 = 1;
        }

        if(_phase1 == 1 && _phase2 == 1 && _phase3 == 1){
            t.cancel();
        }
    }


    @Test(expected = BadJobFault_Exception.class)
    public void fail() throws BadJobFault_Exception{
        _tc.getTransporterClient().decideJob("-1",true);
    }

}