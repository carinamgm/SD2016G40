package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.*;

import javax.xml.ws.BindingProvider;
import java.util.*;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class TransporterClient {

    private ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();
    private ArrayList<JobView> _tracking = new ArrayList<JobView>();
    private int _identifier = 0;

	public TransporterClient(Collection<String> wsUrls){
        for(String wsEndpoint : wsUrls){
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

    public List<JobView> requestJob(String origin, String destination, int price) throws BadPriceFault_Exception, BadLocationFault_Exception{
        List<JobView> proposals = new ArrayList<JobView>();
        JobView jv = null;

        for (TransporterPortType tp : _ports) {
            jv = tp.requestJob(origin, destination, price);
            if (jv != null) {
                jv.setJobIdentifier(generateId());
                proposals.add(jv);
            }
        }

        return proposals.size() == 0 ? null : proposals;
    }

    public void decideJob(String id, boolean accept) throws BadJobFault_Exception{
        JobView jv = null;

        for(TransporterPortType tp : _ports){
            try{
                jv = tp.decideJob(id,accept);
            }
            catch(BadJobFault_Exception e){
            }
        }

        if(jv == null)
            throw new BadJobFault_Exception("Não existe tal trabalho", new BadJobFault());

        _tracking.add(jv);

        Timer t = new Timer();
        t.schedule(new ChangeState(jv),generateRandomLong());

    }

    public ArrayList<JobView> getTracking(){
        return _tracking;
    }

    public String ping(String message){
        String output = "";
        for(TransporterPortType tp : _ports)
            output += tp.ping(message) + "\n";
        return output;
    }

    private long generateRandomLong(){
        Random random = new Random();
        return  1 + (long)(random.nextDouble()*(5 - 1));
    }

    public JobView jobStatus(String id){
        JobView jv = null;
        for(TransporterPortType tp : _ports) {
            jv = tp.jobStatus(id);
            if (jv != null)
                return jv;
        }
        return jv;
    }

    public void clearTransports(){
        for(TransporterPortType tp :_ports)
            tp.clearJobs();
        _tracking.clear();
    }

    private String generateId(){
        int toGive = _identifier;
        _identifier++;
        return String.valueOf(toGive);
    }

}
