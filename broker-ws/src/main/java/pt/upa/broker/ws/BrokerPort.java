package pt.upa.broker.ws;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.util.*;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPortType",
        wsdlLocation="broker.2_0.wsdl",
        name="UpaBroker",
        portName="BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType {

    private List<String> _north = Arrays.asList("Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança");
    private List<String> _south = Arrays.asList("Setúbal", "Évora", "Portalegre", "Beja", "Faro");
	private List<TransportView> _tvs = Collections.synchronizedList(new ArrayList<TransportView>());
	private List<TransportView> _auxUpdate = new ArrayList<TransportView>();
    private TransporterClient _tca;
    private BrokerPortType _sb = null;

    private String[] _args; // args[0] uddiUrl args[1] name args[2] url

    Timer _notify = new Timer();
    Timer _timeout = new Timer();
    private int TIMENOTIFY = 4000;
    private int TIMETIMEOUT = 6000;
    private int TIMETOINIT = 15000; //15secs for the first iteration, since secondary broker is launched first.
    
    public BrokerPort(){}

    public BrokerPort(TransporterClient tca){
        _tca = tca;
    }

    public BrokerPort(TransporterClient tca, String[] args){
    	_tca = tca;
        _timeout.schedule(new TimerTask() {
            @Override
            public void run() {
                takeControl();
            }
        }, TIMETOINIT);
        _args = args;
    }

    public BrokerPort(TransporterClient tca, String url){
    	_tca = tca;
        if(!url.equals("")){
    	  BrokerService service = new BrokerService();
          _sb = service.getBrokerPort();

          BindingProvider bindingProvider = (BindingProvider) _sb;
          Map<String, Object> requestContext = bindingProvider.getRequestContext();
          requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);
        }
        _notify.schedule(new ProofTimerTask(this), TIMENOTIFY);
    }

    @Override
    public void imAlive(){
    	if(isPrimaryBroker()){
    		System.out.println("Sending alive proof");
    		_sb.imAlive();
            _notify = new Timer();
            _notify.schedule(new ProofTimerTask(this), TIMENOTIFY);
    	}
    	else{
    		System.out.println("Primary Broker is alive;");
    		_timeout.cancel();
            _timeout = new Timer();
    		_timeout.schedule(new TimerTask() {
                @Override
                public void run() {
                    takeControl();
                }
            }, TIMETIMEOUT);
    	}
    }
  
    private void takeControl() {
        if(!isPrimaryBroker()) {
            System.out.println("Taking UpaBroker Position");
            try {
                UDDINaming uddiNaming = new UDDINaming(_args[0]);
                uddiNaming.rebind("UpaBroker", _args[2]);
            } catch (JAXRException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean isPrimaryBroker(){
    	return _sb != null;
    }
    
    @Override
	public String ping(String message){
        return message;
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
            throws UnknownLocationFault_Exception, InvalidPriceFault_Exception,
            UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception {

        if((_north.contains(origin) && _south.contains(destination)) || (_north.contains(destination) && _south.contains(origin))){
            UnavailableTransportFault utf = new UnavailableTransportFault();
            utf.setOrigin(origin);
            utf.setDestination(destination);
            throw new UnavailableTransportFault_Exception("North and south is not possible",utf);
        }


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

        if(chosenJobView.getJobPrice() <= price){
        	try {
				_tca.decideJob(chosenJobView.getJobIdentifier(), true);
                tv.setState(TransportStateView.BOOKED);
			} catch (BadJobFault_Exception e) {
				tv.setState(TransportStateView.FAILED);
			}

        }
        else{
        	tv.setState(TransportStateView.FAILED);
            UnavailableTransportPriceFault utp = new UnavailableTransportPriceFault();
            utp.setBestPriceFound(price);
        	throw new UnavailableTransportPriceFault_Exception("There are no transports available for that price", utp);
        }

        _tvs.add(tv);

        updateDataToShip("request",tv);

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
        	if(tv.getId().equals(id)){
        		jv = _tca.jobStatus(id);
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
                    updateDataToShip("view",tv);
                    return tv;
                }
                throw new UnknownTransportFault_Exception(id, utf);
            }
        }
        throw new UnknownTransportFault_Exception(id, utf);
    }

    @Override
    public List<TransportView> listTransports() {
        return _tvs;
    }

    @Override
    public void clearTransports(){
    	_tvs.clear();
    	_tca.clearTransports();
    	if(isPrimaryBroker())
    		updateSecondaryBroker("clear",null);
    }

	@Override
	public void updateSecondaryBroker(String operation, List<TransportView> update) {
        if(isPrimaryBroker()){
            _sb.updateSecondaryBroker(operation,_auxUpdate);
            _auxUpdate.clear();
        }
        else{
            if(operation.equals("clear"))
                clearTransports();
            else{
                stateAlreadyInTVS(update);
                _tvs.addAll(update);
            }
        }
	}

    private void updateDataToShip(String operation, TransportView tv){
        if(isPrimaryBroker()){
            if(tv != null)
                _auxUpdate.add(tv);
            updateSecondaryBroker(operation,_auxUpdate);
        }
    }

    private void stateAlreadyInTVS(List<TransportView> update) {
        if (!isPrimaryBroker()) {
            for (TransportView tvi : update) {
                for (TransportView tvj : _tvs) {
                    if (tvi.getId() == tvj.getId())
                        _tvs.remove(tvj);
                }
            }
        }
    }

	
}
