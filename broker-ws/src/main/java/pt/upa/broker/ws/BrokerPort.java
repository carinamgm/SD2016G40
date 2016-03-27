package pt.upa.broker.ws;

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

    @Override
	public String ping(String name){
        return name;
    }

    @Override
    public String requestTransport(String origin, String destination, int price){
        return "hummm";
    }

    @Override
    public TransportView viewTransport(String id){
        return new TransportView();
    }

    @Override
    public List<TransportView> listTransports() {
        return null;
    }

    @Override
    public void clearTransports(){

    }

}
