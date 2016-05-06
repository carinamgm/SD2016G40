package pt.upa.ca.ws;

import javax.jws.WebService;

@WebService
public interface CaService {
    public byte[] requestCertificate();
}
