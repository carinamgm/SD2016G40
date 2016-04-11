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

    @Override
	public String ping(String message){
        return message;
    }

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
            UnknownLocationFault ulf = new UnknownLocationFault();
            ulf.setLocation(e.getFaultInfo().getLocation());
            throw new UnknownLocationFault_Exception(e.getMessage(),ulf);
        }
        
        if(proposals == null){
            UnavailableTransportFault utf = new UnavailableTransportFault();
            utf.setDestination(destination);
            utf.setOrigin(origin);
        	throw new UnavailableTransportFault_Exception("There are no transports available", utf);
        }
        
        tv.setState(TransportStateView.BUDGETED);
        chosenJobView = searchBestOffer(proposals);
        
        tv.setPrice(chosenJobView.getJobPrice());
    	tv.setTransporterCompany(chosenJobView.getCompanyName());
    	tv.setId(chosenJobView.getJobIdentifier());

    	_tvs.add(tv);
    	
        if(chosenJobView.getJobPrice() <= price){
        	try {
				_tca.decideJob(chosenJobView.getJobIdentifier(), true);
			} catch (BadJobFault_Exception e) {
				tv.setState(TransportStateView.FAILED);
			}
        	tv.setState(TransportStateView.BOOKED);
        }
        else{
        	tv.setState(TransportStateView.FAILED);
            UnavailableTransportPriceFault utp = new UnavailableTransportPriceFault();
            utp.setBestPriceFound(price);
        	throw new UnavailableTransportPriceFault_Exception("There are no transports available for that price", utp);
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

    @Override
    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
        UnknownTransportFault utf = new UnknownTransportFault();
        utf.setId(id);

        JobView jv = null;
    	for(TransportView tv : _tvs){
        	if(tv.getId() == id){
        		jv = _tca.viewState(id);
                if(jv != null) {
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
                throw new UnknownTransportFault_Exception(id, utf);
            }
        }
        throw new UnknownTransportFault_Exception(id, utf);
    }

    //FIXME? List or ArrayList???
    @Override
    public List<TransportView> listTransports() {
        return _tvs;
    }

    @Override
    public void clearTransports(){
    	_tvs.clear();
    	_tca.clearTransports();
    }

}
