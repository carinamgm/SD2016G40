package pt.upa.broker;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.cli.BrokerClient;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.ws.BindingProvider;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {

		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Missing: EndPointAddress %n", BrokerClient.class.getName());
			return;
		}

        String endpointAddress = args[0];

        System.out.println("EndPointAddress: " + endpointAddress);

        System.out.println("Creating stub ...");
        BrokerService service = new BrokerService();
        BrokerPortType port = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);


        BrokerClient bc = new BrokerClient(port);

        System.out.println(bc.ping("Sexy"));


	}

}
