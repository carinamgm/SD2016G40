package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.transporter.ws.cli.TransporterClient;

import javax.xml.ws.Endpoint;
import java.util.Collection;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");


		// args[0] - uddi url
		// args[1] - webservice name
		// args[2] - webservice url
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerPort.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			uddiNaming = new UDDINaming(uddiURL);
			Collection<String> wsUrls = uddiNaming.list("UpaTransporter"+"%");
			String secBrokerUrl = "";
			BrokerPort port = null;
			if(name.equals("UpaBroker")){
				Collection<String> seqUrl = uddiNaming.list("UpaBrokerSec");
				String[] aux = seqUrl.toArray(new String[1]);
				secBrokerUrl = aux[0];
				port = new BrokerPort(new TransporterClient(wsUrls), secBrokerUrl);
			}
			else {
				port = new BrokerPort(new TransporterClient(wsUrls), args);
			}

			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming.rebind(name, url);

			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					System.out.printf("Deleted '%s' from UDDI%n", name);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}

	}

}
