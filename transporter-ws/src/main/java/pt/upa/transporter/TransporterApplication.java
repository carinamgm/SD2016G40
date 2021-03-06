package pt.upa.transporter;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.handler.ws.Handler;
import pt.upa.transporter.ws.TransporterPort;

import javax.xml.ws.Endpoint;

public class TransporterApplication {

	public static final String[][] regions = {{"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"},
			{"Lisboa","Leiria", "Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"},
			{"Setúbal", "Évora", "Portalegre", "Beja", "Faro"}};

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterApplication.class.getSimpleName() + " starting...");

		// args[0] - uddi url
		// args[1] - webservice name
		// args[2] - webservice url
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterPort.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];

		System.out.println(name);

		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			TransporterPort port = new TransporterPort(regions, name);
			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			Handler.serviceName = name;

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
