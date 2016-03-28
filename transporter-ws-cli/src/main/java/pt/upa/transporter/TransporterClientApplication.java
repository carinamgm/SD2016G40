package pt.upa.transporter;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.BindingProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class TransporterClientApplication {

	private static ArrayList<TransporterPortType> _ports = new ArrayList<TransporterPortType>();

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");

		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterClientApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];

		UDDINaming uddiNaming = null;
		uddiNaming = new UDDINaming(uddiURL);

		System.out.println(args[1]);
		Collection<String> wsUrls = uddiNaming.list(name+"%");

		for(String wsEndpoint : wsUrls){
			TransporterService ts = new TransporterService();
			TransporterPortType port = ts.getTransporterPort();
			_ports.add(port);
		}

		Object[] ws = wsUrls.toArray();
		for(int i = 0; i < wsUrls.size(); i++){
			System.out.println("Setting endpoint address ..." + ws[i]);
			BindingProvider bindingProvider = (BindingProvider) _ports.get(i);
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, ws[i]);
		}

	}
}
