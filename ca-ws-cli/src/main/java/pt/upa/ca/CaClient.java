package pt.upa.ca;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CaImplService;
import pt.upa.ca.ws.CaService;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class CaClient {

	private final String uddiURL = "http://localhost:9090";
	private final String wsName = "ca-ws";
	private CaService _ca;

	public CaClient(){
		UDDINaming uddiNaming = null;
		String endpointAddress;

		try {
			uddiNaming = new UDDINaming(uddiURL);
			endpointAddress = uddiNaming.lookup(wsName);

			CaImplService service = new CaImplService();
			CaService port = service.getCaImplPort();

			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

			_ca = port;

		} catch (JAXRException e) {
			System.out.println(e.getMessage());
		}
	}

	public byte[] requestCertificate(String entity){
		return _ca.requestCertificate(entity);
	}

	
}
