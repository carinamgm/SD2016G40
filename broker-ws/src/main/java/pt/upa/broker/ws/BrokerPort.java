package pt.upa.broker.ws;



import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPortType",
        wsdlLocation="broker.1_0.wsdl",
        name="UpaBroker",
        portName="BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType {
	
	private ArrayList<TransportView> _tvs = new ArrayList<TransportView>();
    private TransporterClient _tca;

    public BrokerPort(){}

    public BrokerPort(TransporterClient tca){
        _tca = tca;
    }


    /**
     *
     * @param message
     * @return
     *     returns java.lang.String
     */
    @Override
	public String ping(String message){
        return message;
    }


    /**
     *
     * @param price
     * @param origin
     * @param destination
     * @return
     *     returns java.lang.String
     * @throws UnknownLocationFault_Exception
     * @throws InvalidPriceFault_Exception
     * @throws UnavailableTransportPriceFault_Exception
     * @throws UnavailableTransportFault_Exception
     */
    @Override
    public String requestTransport(String origin, String destination, int price) throws UnknownLocationFault_Exception, InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {
        ArrayList<JobView> proposals = null;
        TransportView tv = new TransportView();
        JobView chosenJobView = null;
          
        tv.setState(TransportStateView.REQUESTED);
		tv.setOrigin(origin);
		tv.setDestination(destination);
        
        try{
            proposals = _tca.requestJob(origin,destination,price);
        }
        catch(BadPriceFault_Exception e){
            InvalidPriceFault ipf = new InvalidPriceFault();
            ipf.setPrice(e.getFaultInfo().getPrice());
            throw new InvalidPriceFault_Exception(e.getMessage(),ipf);
        }
        catch(BadLocationFault_Exception e){
            throw new UnknownLocationFault_Exception("Origin and destination can't be resolved",new UnknownLocationFault());
        }
        
        
        if(proposals == null){
        	throw new UnavailableTransportFault_Exception("There are no transports available", new UnavailableTransportFault());
        }
        
        tv.setState(TransportStateView.BUDGETED);
        chosenJobView = searchBestOffer(proposals);
        
        tv.setPrice(chosenJobView.getJobPrice());
    	tv.setTransporterCompany(chosenJobView.getCompanyName());
    	tv.setId(chosenJobView.getJobIdentifier());
    	_tvs.add(tv);
    	
        if(chosenJobView.getJobPrice() <= price){
        	try {
				chosenJobView = _tca.decideJob(chosenJobView.getJobIdentifier(), true);
			} catch (BadJobFault_Exception e) {
				tv.setState(TransportStateView.FAILED);
			}
        	tv.setState(TransportStateView.BOOKED);
        }
        
        else{
        	tv.setState(TransportStateView.FAILED);
        	throw new UnavailableTransportPriceFault_Exception("There are no transports available for that price", new UnavailableTransportPriceFault());
        }
       
        return tv.getId();
    }
    

    private JobView searchBestOffer(ArrayList<JobView> proposals){
    	JobView jvbo = null;
    	int highestPrice = 100;
    	int jobPrice;
    	
    	for(JobView _jv: proposals){
    		jobPrice = _jv.getJobPrice();
    		if(jobPrice < highestPrice){
    			highestPrice = jobPrice;
    			jvbo = _jv;
    		}
    	}
        return jvbo;
    }


    /**
     *
     * @param id
     * @return
     *     returns pt.upa.broker.ws.TransportView
     * @throws UnknownTransportFault_Exception
     */
    @Override
    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
        JobView jv = new JobView();
    	for(TransportView tv : _tvs){
        	if(tv.getId() == id)
        		jv = _tca.viewState(id);
        		switch (jv.getJobState()) {
	        		
        			case PROPOSED:
	        			tv.setState(TransportStateView.BUDGETED);
	        			break;
	        		case ACCEPTED:
	        			tv.setState(TransportStateView.BOOKED);
	        			break;
	        		case REJECTED:
	        			tv.setState(TransportStateView.FAILED);
	        			break;
	        		case HEADING:
	        			tv.setState(TransportStateView.HEADING);
	        			break;
	        		case ONGOING:
	        			tv.setState(TransportStateView.ONGOING);
	        			break;
	        		case COMPLETED:
	        			tv.setState(TransportStateView.COMPLETED);
	        			break;
        		}
        		
        		return tv;
        }
        throw new UnknownTransportFault_Exception(id, new UnknownTransportFault());
    }
    
    
    /**
     *
     * @return
     *     returns java.util.List<pt.upa.broker.ws.TransportView>
     */
    @Override
    public List<TransportView> listTransports() {
        List<TransportView> lista = new ArrayList<TransportView>();
        for (int i = 0;i < _tvs.size(); i++){
        	lista.add(_tvs.get(i));
        }   
        return lista;
    }


    /**
     *
     */
    @Override
    public void clearTransports(){
    	_tvs.clear();
    	_tca.clearTransports();
    	//FIXEME
    }

}
