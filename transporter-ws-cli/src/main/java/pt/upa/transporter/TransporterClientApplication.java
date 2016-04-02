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

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		/*
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
		*/

	}
}
