package pt.upa.broker;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClientApplication {

    private static BrokerClient _bc;
    private static String _endpointAddress;

	public static void main(String[] args) throws Exception {

		// Check arguments
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

}
