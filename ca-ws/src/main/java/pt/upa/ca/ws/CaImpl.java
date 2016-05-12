package pt.upa.ca.ws;

import javax.jws.WebService;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@WebService(endpointInterface = "pt.upa.ca.ws.CaService")
public class CaImpl implements  CaService{

    ConcurrentHashMap<String,Certificate> _certificates = new ConcurrentHashMap<>();

    public CaImpl(){
    }

    public CaImpl(ArrayList<File> certificateFiles){
        init(certificateFiles);
    }

    private void init(ArrayList<File> certificateFiles){
        Pattern pattern = Pattern.compile(".\\.cer");
        for(File parent : certificateFiles){
            for(File child : parent.listFiles()) {
                if(pattern.matcher(child.getName()).find()) {
                    try{
                        _certificates.put(child.getName().toLowerCase().substring(0,child.getName().toLowerCase().indexOf(".")),readCertificateFile(child.getPath()));
                    }
                    catch (Exception e) {}
                }
            }
        }
    }

    @Override
    public byte[] requestCertificate(String entityName) {
        System.out.println("Serving request to: " + entityName + ".cer");
        byte[] output = null;
        try{
           output =  _certificates.get(entityName).getEncoded();
        } catch (Exception e){}
        finally {
            return output;
        }
    }

    private Certificate readCertificateFile(String certificateFilePath) throws Exception {
        FileInputStream fis;

        try {
            fis = new FileInputStream(certificateFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("Certificate file <" + certificateFilePath + "> not fount.");
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        if (bis.available() > 0) {
            Certificate cert = cf.generateCertificate(bis);
            return cert;
        }
        bis.close();
        fis.close();
        return null;
    }


}