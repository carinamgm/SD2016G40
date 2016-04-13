package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClientApplication {

    private static BrokerClient _bc;
    private static String _endpointAddress;
    private static final String _uddiURL= "http://localhost:9090";
    private static final String _serviceName = "UpaBroker";

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

    public static void setup(){
        System.out.println("EndPointAddress: " + _endpointAddress);

        System.out.println("Creating stub ...");
        BrokerService service = new BrokerService();
        BrokerPortType port = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, _endpointAddress);

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
