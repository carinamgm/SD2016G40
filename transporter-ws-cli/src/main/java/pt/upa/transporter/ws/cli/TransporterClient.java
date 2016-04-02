package pt.upa.transporter.ws.cli;

import pt.upa.transporter.ws.*;

import javax.xml.ws.BindingProvider;
import java.util.*;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class TransporterClient {

    private ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();
    private ArrayList<JobView> _tracking = new ArrayList<JobView>();

	public TransporterClient(Collection<String> wsUrls){
        for(String wsEndpoint : wsUrls){
            TransporterService ts = new TransporterService();
            TransporterPortType port = ts.getTransporterPort();
            _ports.add(port);
        }

        Object[] ws = wsUrls.toArray();
        for(int i = 0; i < wsUrls.size(); i++){
            //System.out.println("Setting endpoint address ..." + ws[i]);
            BindingProvider bindingProvider = (BindingProvider) _ports.get(i);
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, ws[i]);
        }
    }

    public ArrayList<JobView> requestJob(String origin, String destination, int price) throws BadPriceFault_Exception {
        ArrayList<JobView> proposals = new ArrayList<JobView>();
        JobView jv = null;

        for (TransporterPortType tp : _ports) {
            try {
                jv = tp.requestJob(origin, destination, price);
            } catch (BadLocationFault_Exception e) {
            } finally {
                if (jv != null)
                    proposals.add(jv);
            }
        }
        return proposals.size() == 0 ? null : proposals;
    }

    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
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

        return jv;
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

}
