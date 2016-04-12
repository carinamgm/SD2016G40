package pt.upa.transporter.ws;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChangeState extends TimerTask {

    private JobView _jv;

    public ChangeState(JobView jv){
      _jv = jv;
    }

    @Override
    public void run() {
        Timer t = new Timer();
        if(JobStateView.ACCEPTED.equals(_jv.getJobState())){
            _jv.setJobState(JobStateView.HEADING);
            t.schedule(new ChangeState(_jv),generateRandomLong());
        }
        else if(JobStateView.HEADING.equals(_jv.getJobState())){
            _jv.setJobState(JobStateView.ONGOING);
            t.schedule(new ChangeState(_jv),generateRandomLong());
        }
        else if(JobStateView.ONGOING.equals(_jv.getJobState()))
            _jv.setJobState(JobStateView.COMPLETED);
    }

    private long generateRandomLong(){
        Random random = new Random();
        long randomValue = 1 + (long)(random.nextDouble()*(5 - 1));
        return randomValue;
    }


}
