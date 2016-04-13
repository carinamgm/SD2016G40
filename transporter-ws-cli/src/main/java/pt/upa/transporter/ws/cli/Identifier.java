package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;

public class Identifier {

    private JobView _jv;
    private TransporterPortType _company;

    public Identifier(JobView jv, TransporterPortType company){
        _jv = jv;
        _company = company;
    }

    public JobView getJobView(){
        return _jv;
    }

    public TransporterPortType getCompany(){
        return _company;
    }

}
