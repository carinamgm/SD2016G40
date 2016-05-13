package pt.upa.handler.ws;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import static java.sql.Timestamp.valueOf;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

//import java.security.Timestamp;

public class Handler extends AbstractHandler {

    public static String serviceName = "";
    public static ConcurrentHashMap<String,byte[]> _certificates = new ConcurrentHashMap<>();
    private static int _maxCallsToCa = 25;
    private static int _callsToCa = 25;
    private static ArrayList<Timestamp> listTimeStamps = new ArrayList<Timestamp>();

    public boolean handleMessage(SOAPMessageContext smc) {

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                System.out.println(serviceName + ": Writing header in outbound SOAP message...");

                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                //create a timestamped nonce, including it in the Header
                String nonce = (new Timestamp(System.currentTimeMillis())).toString();

                Name nonceName = se.createName("TimeStampNonce", "Upa", "http://upa");
                SOAPHeaderElement elementNonce = sh.addHeaderElement(nonceName);
                elementNonce.addTextNode(nonce);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(nonce.getBytes());
                String body = extractBody(msg);
                out.write(body.getBytes());

                // sign and digest
                KeyPair kp = getKeyPair("keys/" + serviceName + "/" + serviceName + ".jks", "keys/KeyStorePwd", serviceName.toLowerCase());
                String encodedSignedBody = printBase64Binary(makeDigitalSignature(out.toByteArray(), kp));

                // add name to header in order to inform broker who is the transporter
                Name name;
                SOAPHeaderElement element;
                if(!serviceName.equals("UpaBroker")){
                    name = se.createName("name", "Upa", "http://upa");
                    element = sh.addHeaderElement(name);
                    element.addTextNode(serviceName.toLowerCase());
                }

                // add encondedSignedBody = H(M+N)
                name = se.createName("hmks", "Upa", "http://upa");
                element = sh.addHeaderElement(name);
                element.addTextNode(encodedSignedBody);

                // make the changes permanent
                msg.saveChanges();

            } else {
                System.out.println(serviceName + ": Reading header in inbound SOAP message..." );

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // declare variables needed
                Name name;
                Iterator it;
                SOAPElement element;

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                String entity;
                // get the entity to which we should get the certificate from ca
                if(serviceName.equals("UpaBroker")) {
                    name = se.createName("name", "Upa", "http://upa");
                    it = sh.getChildElements(name);
                    if (!it.hasNext()) {
                        System.out.println("Header element name not found.");
                        return true;
                    }
                    element = (SOAPElement) it.next();
                    entity = element.getValue();
                }
                else {
                    entity = "upabroker";
                }

                // get signed nounce + body from header
                name = se.createName("hmks", "Upa", "http://upa");
                it = sh.getChildElements(name);

                if (!it.hasNext()) {
                    System.out.println("Header element HmKs not found!");
                    return true;
                }

                element = (SOAPElement) it.next();
                String hmks = element.getValue();


                // getting the nounce
                name = se.createName("TimeStampNonce", "Upa", "http://upa");
                it = sh.getChildElements(name);

                if(!it.hasNext()) {
                    System.out.println("Header element TimeStampNonce not found!");
                    return true;
                }

                element = (SOAPElement) it.next();

                String nounce = element.getValue();

                // verify the freshness of nounce
                if(!freshness(valueOf(nounce))){
                    System.out.println("TimeStampNonce has rotten TimeStamp!");
                    return false;
                }

                if(_callsToCa == 1){
                    _certificates.clear();
                    _callsToCa = _maxCallsToCa;
                }

                if(!_certificates.containsKey(entity))
                    _certificates.put(entity,requestCertificate(entity));
                else
                    _callsToCa--;

                // build a certificate to get the public key
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate entityCer = cf.generateCertificate(new ByteArrayInputStream(_certificates.get(entity)));
                KeyPair kp = new KeyPair(entityCer.getPublicKey(), null);

                if(!verifySignedCertificate(entityCer,extractCaCertificateKey("keys/"+ serviceName + "/" + serviceName + ".jks","keys/KeyStorePwd"))){
                    System.out.println("Certificate not signed by ca");
                    return false;
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                outputStream.write(nounce.getBytes());
                String body = extractBody(msg);
                outputStream.write(body.getBytes());

                // verifiy the digital signature
                if(!verifyDigitalSignature(parseBase64Binary(hmks), outputStream.toByteArray(), kp)){
                    System.out.println("Digest doesn't match H(M+N)");
                    return false;
                }
            }
        }
        catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }
        return true;
    }


    private boolean freshness(Timestamp nonce){
        String nonceStringAux = (new Timestamp(System.currentTimeMillis())).toString();
        Timestamp currentTime = valueOf(nonceStringAux);

        //Difference between nonce and currentTime to check if it's inside a 1min interval
        long diffTime = currentTime.getTime() - nonce.getTime();
        diffTime = diffTime / (60 * 1000);


        //Checks if TimeStamped nonce was already used before,
        //or if the msg was sent in the space of 1 minute
        if(!listTimeStamps.contains(nonce) && 0 <= diffTime && diffTime <= 1)
            listTimeStamps.add(nonce);
        else
            return false;

        return true;
    }

    // this functions was only needed to extract the body from the msg because
    // for some reason getting soapbody directly from msg returns null
    private String extractBody(SOAPMessage sm){
        String soapBodyBegin = "<S:Body>";
        String soapBodyEnd = "</S:Body>";
        OutputStream outputStream = new ByteArrayOutputStream( );

        try {
            sm.writeTo(outputStream);
        } catch (Exception e){
        }

        int begin = outputStream.toString().indexOf(soapBodyBegin);
        int end = outputStream.toString().indexOf(soapBodyEnd) + soapBodyEnd.length();

        return outputStream.toString().substring(begin,end);

    }



    /*
    // DEBUG --------------------------------------------- DEBUG -------------------------------------------

                ByteArrayOutputStream gg = new ByteArrayOutputStream();
                msg.writeTo(gg);
                try{

                    File file =new File(serviceName+".txt");

                    //if file doesnt exists, then create it
                    if(!file.exists()){
                        file.createNewFile();
                    }


                    //true = append file
                    FileWriter fileWritter = new FileWriter(file.getName(),true);
                    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                    bufferWritter.write(gg.toString());
                    bufferWritter.write("\n");
                    bufferWritter.close();

                    System.out.println("Done");

                }catch(IOException e){
                    e.printStackTrace();
                }


                // DEBUG --------------------------------------------- END DEBUG ---------------------------------------
     */

}
