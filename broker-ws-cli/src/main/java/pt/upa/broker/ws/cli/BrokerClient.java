package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.*;

import java.util.List;

public class BrokerClient {

    private BrokerPortType _upaBroker;

    public BrokerClient(BrokerPortType upaBroker){
        _upaBroker = upaBroker;
    }

	public String ping(String msg){
        return _upaBroker.ping(msg);
    }

    public String schedule(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{

        return _upaBroker.requestTransport(origin, destination, price);
    }

    public TransportView checkTransportState(String id) throws UnknownTransportFault_Exception{
        return _upaBroker.viewTransport(id);
    }

    public List<TransportView> listScheduleTransports(){ return _upaBroker.listTransports(); }

    public void clearTransports(){
        _upaBroker.clearTransports();
    }


}
