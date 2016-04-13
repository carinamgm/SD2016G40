package pt.upa.transporter;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

public class TransporterClientApplication {

	private static TransporterClient _tc;
	private static String _uddiURL= "http://localhost:9090";
	private static String _serviceName = "UpaTransporter";
	private static ArrayList<String> _transporterNames = new ArrayList<String>();

	// The main class is useless since is the broker who creates the instance of TransporterClient directly
	public static void main(String[] args) throws Exception {
		// The args are not used for nothing but:
		// arg[0] - uddiURL
		// arg[1] - wsName
	}

	// Functions for the IT tests

	public static void setup(){
		UDDINaming uddi = null;
		try {
			uddi = new UDDINaming(_uddiURL);
			Collection<String> wsUrls = uddi.list(_serviceName + "%");
			_tc = new TransporterClient(wsUrls);
			for(String s : wsUrls){
				String aux = s.substring(s.lastIndexOf(":"));
				_transporterNames.add(_serviceName + aux.substring(4,aux.indexOf('/')));
			}
		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();
		}
	}

	public static TransporterClient getTransporterClient(){
		return _tc;
	}

	public static ArrayList<String> getTransporterNames(){
		return _transporterNames;
	}

	public static String getServiceName(){
		return _serviceName;
	}

	public static void clean(){
		_transporterNames.clear();
		_tc.clearTransports();
	}


}