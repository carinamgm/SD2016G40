package pt.upa.ca.ws;

import javax.jws.WebService;
import java.io.File;
import java.util.ArrayList;


@WebService(endpointInterface = "pt.upa.ca.ws.CaService")
public class CaImpl implements  CaService{

    ArrayList<File> _certificates = new ArrayList<>();

    public CaImpl(){
    }

    public CaImpl(ArrayList<File> certificates){
        _certificates = certificates;

    }

    @Override
    public byte[] requestCertificate() {
        return new byte[0];
    }
}