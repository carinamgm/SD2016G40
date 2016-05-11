package pt.upa.broker.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.*;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClient {

    private BrokerPortType _upaBroker;

    public BrokerClient(BrokerPortType upaBroker){
        _upaBroker = upaBroker;
    }

	public String ping(String msg) {
        String result = "";

        try {
            result = _upaBroker.ping(msg);
        } catch(WebServiceException wse) {
            handleException(wse);
            result = _upaBroker.ping(msg);
        }

        return result;
    }

    public String schedule(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
            UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{

        String result = "";

        try {
            result = _upaBroker.requestTransport(origin, destination, price);
        } catch(WebServiceException wse) {
            handleException(wse);
            result = _upaBroker.requestTransport(origin, destination, price);
        }

        return result;
    }

    public TransportView checkTransportState(String id) throws UnknownTransportFault_Exception{
        TransportView result = null;

        try {
            result = _upaBroker.viewTransport(id);
        } catch(WebServiceException wse) {
            handleException(wse);
            result = _upaBroker.viewTransport(id);
        }

        return result;
    }

    public List<TransportView> listScheduleTransports() {
        List<TransportView> result = null;

        try {
            result = _upaBroker.listTransports();
        } catch (WebServiceException wse) {
            handleException(wse);
            result = _upaBroker.listTransports();
        }

        return result;
    }

    public void clearTransports() {
        try {
            _upaBroker.clearTransports();
        } catch (WebServiceException wse) {
            handleException(wse);
            _upaBroker.clearTransports();
        }
    }

    private void changeBinding(){
        UDDINaming uddiNaming = null;
        String endpointAddress;
        _upaBroker = null;
        try {
            uddiNaming = new UDDINaming(BrokerClientApplication._uddiURL);
            endpointAddress = uddiNaming.lookup(BrokerClientApplication._serviceName);

            BrokerService service = new BrokerService();
            _upaBroker = service.getBrokerPort();

            BindingProvider bindingProvider = (BindingProvider) _upaBroker;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }
    }

    private void handleException(WebServiceException wse) {
        System.out.println("Caught: " + wse);
        Throwable cause = wse.getCause();
        if (cause != null && cause instanceof SocketTimeoutException) {
            System.out.println("The cause was a timeout exception: " + cause);
        }
        changeBinding();
    }

}
