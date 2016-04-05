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
	
	//private final ArrayList<String> _regions = new ArrayList<String>();

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
        return _tca.ping(message);
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
    public String requestTransport(String origin, String destination, int price) throws UnknownLocationFault_Exception, InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception{
        String output = "";
        ArrayList<JobView> proposals = null;
        TransportView tv = new TransportView();
        JobView jv = null;
        
        //INITIALIZE TRANSPORTVIEW
        tv.setState(TransportStateView.REQUESTED);
        tv.setDestination(destination);
        tv.setOrigin(origin);
        
        try{
            proposals = _tca.requestJob(origin,destination,price);
        }
        catch(BadPriceFault_Exception e){
        	//tv.setState(TransportStateView.FAILED);
            InvalidPriceFault ipf = new InvalidPriceFault();
            ipf.setPrice(e.getFaultInfo().getPrice());
            throw new InvalidPriceFault_Exception(e.getMessage(),ipf);
        }
        catch(BadLocationFault_Exception e){
        	//tv.setState(TransportStateView.FAILED);
            throw new UnknownLocationFault_Exception("Origin and destination can't be resolved",new UnknownLocationFault());
        }
        
        
        if(proposals == null){
        	//tv.setState(TransportStateView.FAILED);
        	throw new UnavailableTransportFault_Exception("There are no transports available", new UnavailableTransportFault());
        }
        
        tv.setState(TransportStateView.BUDGETED);
        jv = searchBestOffer(proposals);
        
        if(jv.getJobPrice() <= price){
        	tv.setPrice(jv.getJobPrice());
        	tv.setDestination(jv.getCompanyName());
        	tv.setId(jv.getJobIdentifier());
        	_tvs.add(tv);
        	
        	try {
				jv = _tca.decideJob(jv.getJobIdentifier(), true);
			} catch (BadJobFault_Exception e) {
				output = "Unsuccessful";
			} finally { 
        		
        	}
        }
        
        else{
        	tv.setState(TransportStateView.FAILED);
        	throw new UnavailableTransportPriceFault_Exception("There are no transports available for that price", new UnavailableTransportPriceFault());
        }
        
        output = "Success"; //FIXME don't know what to write lolz

        return output;
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
        return null;
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
        //	lista.add(_tvs.get(i).getState());
        }
        
        return lista;
    }


    /**
     *
     */
    @Override
    public void clearTransports(){
    	_tvs.clear();
    	//TODO ask transporter to clean all my data
    }

}
