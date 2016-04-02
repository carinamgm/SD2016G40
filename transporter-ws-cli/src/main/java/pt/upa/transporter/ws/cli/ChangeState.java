package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class ChangeState extends TimerTask {

    private JobView _jv;

    public ChangeState(JobView jv){
        _jv = jv;
    }

    @Override
    public void run() {
        Timer t = new Timer();
        if(_jv.getJobState() == JobStateView.ACCEPTED){
            _jv.setJobState(JobStateView.HEADING);
            t.schedule(new ChangeState(_jv),generateRandomLong());
        }
        else if(_jv.getJobState() == JobStateView.HEADING){
            _jv.setJobState(JobStateView.ONGOING);
            t.schedule(new ChangeState(_jv),generateRandomLong());
        }
        else if(_jv.getJobState() == JobStateView.ONGOING)
            _jv.setJobState(JobStateView.COMPLETED);
    }

    private long generateRandomLong(){
        Random random = new Random();
        long randomValue = 1 + (long)(random.nextDouble()*(5 - 1));
        return randomValue;
    }

}
