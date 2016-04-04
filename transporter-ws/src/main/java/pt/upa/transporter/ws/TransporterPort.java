package pt.upa.transporter.ws;

import jdk.nashorn.internal.scripts.JO;

import javax.jws.WebService;
import java.util.*;


@WebService(
        endpointInterface="pt.upa.transporter.ws.TransporterPortType",
        wsdlLocation="transporter.1_0.wsdl",
        name="TransporterWebService",
        portName="TransporterPort",
        targetNamespace="http://ws.transporter.upa.pt/",
        serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType {

    private final ArrayList<String> _regions = new ArrayList<String>();
    private int _transporterNumber;
    private String _companyName;
    private ArrayList<JobView> _jobsList = new ArrayList<JobView>();
    private int _identifier = 0;

    public TransporterPort(){
    }

    public TransporterPort(String[][] regions, String name){
        _companyName = name;
        init(regions);
    }

    private void init(String[][] regions){
        String defaultName = "UpaTransporter";
        _transporterNumber = Integer.parseInt(_companyName.substring(defaultName.length()));

        if(_transporterNumber % 2 == 0)
            fillArrayList(regions, 0,regions.length-1);
        else
            fillArrayList(regions, 1,regions.length);
    }

    private void fillArrayList(String[][] regions, int begin, int limit){
        for(int i = begin; i < limit; i++)
            for(int j = 0; j < regions[i].length; j++)
                _regions.add(regions[i][j]);
    }

    @Override
    public String ping(String message){
        return _companyName + " " + "ping result: " + message;
    }

    @Override
    public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
        if(_regions.contains(origin) && _regions.contains(destination)){
            if(price >= 0) {
                int priceOffer = makeOffer(price);
                JobView jv = priceOffer >= 0 ? new JobView() : null;
                if(jv != null) {
                    jv.setJobState(JobStateView.PROPOSED);
                    jv.setCompanyName(_companyName);
                    jv.setJobDestination(destination);
                    jv.setJobOrigin(origin);
                    jv.setJobPrice(priceOffer);
                    jv.setJobIdentifier(generateId());
                    _jobsList.add(jv);
                }
                return jv;
            }
            BadPriceFault bf = new BadPriceFault();
            bf.setPrice(price);
            throw new BadPriceFault_Exception("Prices can't be negative", bf);
        }
        BadLocationFault blf = new BadLocationFault();
        blf.setLocation(origin + " " + destination);
        throw new BadLocationFault_Exception("Invalidas rotas" + origin + " " + destination, blf);
    }

    private int makeOffer(int price){
        Random r = new Random();
        if(price == 0)
            return 0;
        else if(price <= 10)
            return price - generateRandom(r,price,1);
        else if(price > 10 && price <= 100){
            if(price % 2 == 1)
                return _transporterNumber % 2 == 1 ? price - generateRandom(r,price,1) : price + generateRandom(r,price,1);
            if(price % 2 == 0)
                return _transporterNumber % 2 == 0 ? price - generateRandom(r,price,1) : price + generateRandom(r,price,1);
        }
        return -1;
    }

    private int generateRandom(Random r, int max, int min){
        return r.nextInt(max - min) + min;
    }

    @Override
    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
        for(JobView jv : _jobsList){
            if(id == jv.getJobIdentifier() && jv.getJobState() == JobStateView.PROPOSED) {
                if(accept)
                    jv.setJobState(JobStateView.ACCEPTED);
                else
                    jv.setJobState(JobStateView.REJECTED);
                return jv;
            }
        }
        throw new BadJobFault_Exception("Não existe tal trabalho", new BadJobFault());
    }

    @Override
    public JobView jobStatus(String id){
        for(JobView jv : _jobsList){
            if(id == jv.getJobIdentifier())
                return jv;
        }
        return null;
    }

    @Override
    public List<JobView> listJobs(){
        return _jobsList;
    }

    @Override
    public void clearJobs(){
        _jobsList.clear();
    }

    private String generateId(){
        int toGive = _identifier;
        _identifier++;
        return String.valueOf(toGive);
    }

}
