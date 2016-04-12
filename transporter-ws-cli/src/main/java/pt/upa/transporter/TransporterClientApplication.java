package pt.upa.transporter;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClient;

import java.util.ArrayList;
import java.util.Collection;

public class TransporterClientApplication {

	private static final String _uddiURL= "http://localhost:9090";
	private static final String _serviceName = "UpaTransporter";
	private static TransporterClient _tc;
	private static ArrayList<String> _transporterNames = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
	}

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

	public static void clean(){
		_transporterNames.clear();
		_tc.clearTransports();
	}

	public static ArrayList<String> getTransporterNames(){
		return _transporterNames;
	}




}