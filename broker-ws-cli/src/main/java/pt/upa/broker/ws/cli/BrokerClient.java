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

    public void schedule(String origin, String destination, int price){
        try{
            _upaBroker.requestTransport(origin, destination, price);
        }
        catch (InvalidPriceFault_Exception e){
        }
        catch (UnavailableTransportFault_Exception e){
        }
        catch (UnavailableTransportPriceFault_Exception e){
        }
        catch (UnknownLocationFault_Exception e){
        }
    }

    public void checkTransportState(String id){
        try{
            _upaBroker.viewTransport(id);
        }
        catch (UnknownTransportFault_Exception e){
        }
    }

    public void listScheduleTransports(){
        _upaBroker.listTransports();
    }

    public void clearTransports(){
        _upaBroker.clearTransports();
    }


}
