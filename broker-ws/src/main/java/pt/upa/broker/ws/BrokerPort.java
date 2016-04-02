package pt.upa.broker.ws;


import pt.upa.transporter.ws.cli.TransporterClient;

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
        return "";
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
        return null;
    }


    /**
     *
     */
    @Override
    public void clearTransports(){
    }

}
