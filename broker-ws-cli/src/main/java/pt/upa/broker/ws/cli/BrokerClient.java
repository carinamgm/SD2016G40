package pt.upa.broker.ws.cli;

import pt.upa.broker.ws.*;

public class BrokerClient {

    private BrokerPortType _upaBroker;

    public BrokerClient(BrokerPortType upaBroker){
        _upaBroker = upaBroker;
    }

	public String ping(String msg){
        return _upaBroker.ping(msg);
    }

    public void schedule(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
        _upaBroker.requestTransport(origin, destination, price);
    }

    public void checkTransportState(String id) throws UnknownTransportFault_Exception{
        _upaBroker.viewTransport(id);
    }

    public void listScheduleTransports(){
        _upaBroker.listTransports();
    }

    public void clearTransports(){
        _upaBroker.clearTransports();
    }


}
