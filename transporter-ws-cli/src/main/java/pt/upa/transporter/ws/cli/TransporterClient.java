package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.*;

import javax.xml.ws.BindingProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;


public class TransporterClient {

    private ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();
    private ConcurrentHashMap<String,Identifier> _jobsMap = new ConcurrentHashMap<String,Identifier>();
    private AtomicInteger _identifier = new AtomicInteger(0);

    public TransporterClient(Collection<String> wsUrls){
        for(int i = 0; i < wsUrls.size(); i++){
            TransporterService ts = new TransporterService();
            TransporterPortType port = ts.getTransporterPort();
            _ports.add(port);
        }

        Object[] ws = wsUrls.toArray();
        for(int i = 0; i < wsUrls.size(); i++){
            BindingProvider bindingProvider = (BindingProvider) _ports.get(i);
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, ws[i]);
        }
    }

    public ArrayList<TransporterPortType> getPorts(){
        return _ports;
    }

    public ArrayList<JobView> requestJob(String origin, String destination, int price) throws BadPriceFault_Exception, BadLocationFault_Exception{
        ArrayList<JobView> proposals = new ArrayList<JobView>();
        JobView jv = null;
        int nBadLocations = 0;
        String id;

        for (TransporterPortType tp : _ports) {
            try {
                jv = tp.requestJob(origin, destination, price);
                if (jv != null) {
                    id = generateId();
                    _jobsMap.put(id, new Identifier(jv,tp));

                    JobView jv2 = new JobView();
                    jv2.setJobOrigin(jv.getJobOrigin());
                    jv2.setJobDestination(jv.getJobDestination());
                    jv2.setJobState(jv.getJobState());
                    jv2.setCompanyName(jv.getCompanyName());
                    jv2.setJobPrice(jv.getJobPrice());
                    jv2.setJobIdentifier(id);

                    proposals.add(jv2);

                }
            }
            catch(BadLocationFault_Exception e){
                nBadLocations++;
            }
        }

        if(nBadLocations == _ports.size()){
            BadLocationFault blf = new BadLocationFault();
            blf.setLocation(origin + " " + destination);
            throw new BadLocationFault_Exception("Invalid Routes " + origin + " " + destination, blf);
        }

        return proposals.size() == 0 ? null : proposals;
    }

    public void decideJob(String id, boolean accept) throws BadJobFault_Exception{
        if(_jobsMap.get(id) == null) {
            BadJobFault bjf = new BadJobFault();
            bjf.setId(id);
            throw new BadJobFault_Exception("There is no such job ", bjf);
        }

        _jobsMap.get(id).getCompany().decideJob(_jobsMap.get(id).getJobView().getJobIdentifier(),accept);
    }

    public String ping(String message){
        String output = "";
        for(TransporterPortType tp : _ports)
            output += tp.ping(message) + "\n";
        return output;
    }

    public JobView jobStatus(String id){
        if(_jobsMap.get(id) == null)
            return null;

        return _jobsMap.get(id).getCompany().jobStatus(_jobsMap.get(id).getJobView().getJobIdentifier());
    }

    public boolean equalsJobView(JobView jv1, JobView jv2){
        return jv1.getJobIdentifier().equals(jv2.getJobIdentifier()) && jv1.getJobState().equals(jv2.getJobState()) &&
                jv1.getCompanyName().equals(jv2.getCompanyName()) && jv1.getJobDestination().equals(jv2.getJobDestination()) &&
                jv1.getJobOrigin().equals(jv2.getJobOrigin()) && jv1.getJobPrice() == jv2.getJobPrice();
    }

    public void clearTransports(){
        for(TransporterPortType tp :_ports)
            tp.clearJobs();
        _jobsMap.clear();
        _identifier.set(0);
    }

    private String generateId(){
        return Integer.toString(_identifier.getAndIncrement());
    }

    public ConcurrentHashMap<String,Identifier> getJobs(){
        return _jobsMap;
    }


}