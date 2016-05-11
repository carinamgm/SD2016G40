package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.ws.BindingProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClientApplication {

    private static BrokerClient _bc;
    private static String _endpointAddress;
    public static final String _uddiURL= "http://localhost:9090";
    public static final String _serviceName = "UpaBroker";

	public static void main(String[] args) throws Exception {

        // args[0] - webservice url
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Missing: EndPointAddress %n", BrokerClient.class.getName());
			return;
		}

        _endpointAddress = args[0];
        setup();
	}

    public static void setup() throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        System.out.println("EndPointAddress: " + _endpointAddress);

        System.out.println("Creating stub ...");
        BrokerService service = new BrokerService();
        BrokerPortType port = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, _endpointAddress);

        int connectionTimeout = 5000;
        // The connection timeout property has different names in different versions of JAX-WS
        // Set them all to avoid compatibility issues
        final List<String> CONN_TIME_PROPS = new ArrayList<String>();
        CONN_TIME_PROPS.add("com.sun.xml.ws.connect.timeout");
        CONN_TIME_PROPS.add("com.sun.xml.internal.ws.connect.timeout");
        CONN_TIME_PROPS.add("javax.xml.ws.client.connectionTimeout");
        // Set timeout until a connection is established (unit is milliseconds; 0 means infinite)
        for (String propName : CONN_TIME_PROPS)
            requestContext.put(propName, connectionTimeout);
        System.out.printf("Set connection timeout to %d milliseconds%n", connectionTimeout);

        int receiveTimeout = 2000;
        // The receive timeout property has alternative names
        // Again, set them all to avoid compability issues
        final List<String> RECV_TIME_PROPS = new ArrayList<String>();
        RECV_TIME_PROPS.add("com.sun.xml.ws.request.timeout");
        RECV_TIME_PROPS.add("com.sun.xml.internal.ws.request.timeout");
        RECV_TIME_PROPS.add("javax.xml.ws.client.receiveTimeout");
        // Set timeout until the response is received (unit is milliseconds; 0 means infinite)
        for (String propName : RECV_TIME_PROPS)
            requestContext.put(propName, receiveTimeout);
        System.out.printf("Set receive timeout to %d milliseconds%n", receiveTimeout);

        _bc = new BrokerClient(port);

    }

    public static void testSetup(){
        UDDINaming uddiNaming = null;
        String endpointAddress;
        try {
            uddiNaming = new UDDINaming(_uddiURL);
            endpointAddress = uddiNaming.lookup(_serviceName);

            BrokerService service = new BrokerService();
            BrokerPortType port = service.getBrokerPort();

            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

            _bc = new BrokerClient(port);

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }
    }

    public static BrokerClient getBrokerClient(){
        return _bc;
    }

}
