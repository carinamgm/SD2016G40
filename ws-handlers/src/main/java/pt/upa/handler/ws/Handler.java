package pt.upa.handler.ws;

import com.sun.corba.se.pept.broker.Broker;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.KeyPair;
//import java.security.Timestamp;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.sql.Timestamp;

import static java.sql.Timestamp.valueOf;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class Handler extends AbstractHandler {

    public static String serviceName = "";
    private static int timesCalledCa = 10;
    private static byte[] result;
    private static ArrayList<Timestamp> listTimeStamps = new ArrayList<Timestamp>();

    public boolean handleMessage(SOAPMessageContext smc) {

        System.out.println("AddHeaderHandler: Handling message.");

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                System.out.println("Writing header in outbound SOAP message...");

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

                msg.saveChanges();

                if(serviceName.equals("UpaBroker")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    msg.writeTo(out);

                    // sign and digest
                    KeyPair kp = getKeyPair("/keys/UpaBroker.jks", "/keys/KeyStorePwd", "upabroker");
                    String encodedSignedBody = printBase64Binary(makeDigitalSignature(out.toByteArray(), kp));

                    // add header element
                    Name name = se.createName("HmKsUpaBroker", "Upa", "http://upa");
                    SOAPHeaderElement element = sh.addHeaderElement(name);
                    element.addTextNode(encodedSignedBody);

                    // make the changes permanent
                    msg.saveChanges();

                }
                else {

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    msg.writeTo(out);

                    // sign and digest
                    KeyPair kp = getKeyPair("/keys/" + serviceName + "/" + serviceName + ".jks", "/keys/KeyStorePwd", serviceName.toLowerCase());
                    String encodedSignedBody = printBase64Binary(makeDigitalSignature(out.toByteArray(), kp));

                    // add name to header in order to inform broker who is the transporter
                    Name name = se.createName("name", "Upa", "http://upa");
                    SOAPHeaderElement element = sh.addHeaderElement(name);
                    element.addTextNode(serviceName.toLowerCase());

                    // add header element
                    name = se.createName("HmKs" + serviceName.toLowerCase(), "Upa", "http://upa");
                    element = sh.addHeaderElement(name);
                    element.addTextNode(encodedSignedBody);

                    // make the changes permanent
                    msg.saveChanges();
                    throw new Exception();
                }

            } else {
                System.out.println("Reading header in inbound SOAP message...");

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                if(serviceName.equals("UpaBroker")) {
                    // get first header element
                    Name name = se.createName("name", "Upa", "http://upa");
                    Iterator it = sh.getChildElements(name);

                    if (!it.hasNext()) {
                        System.out.println("Header element name not found.");
                        return true;
                    }

                    SOAPElement element = (SOAPElement) it.next();
                    String entity = element.getValue();

                    name = se.createName("HmKs" + entity, "Upa", "http://upa");
                    it = sh.getChildElements(name);

                    if (!it.hasNext()) {
                        System.out.println("Header element HmKs not found!");
                        return true;
                    }

                    element = (SOAPElement) it.next();

                    // get header element value
                    String valueString = element.getValue();

                    // request certificate to ca
                    if (timesCalledCa == 10) {
                        result = requestCertificate(entity);
                        timesCalledCa = 0;
                    } else
                        timesCalledCa++;

                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Certificate brokerCer = cf.generateCertificate(new ByteArrayInputStream(result));
                    KeyPair kp = new KeyPair(brokerCer.getPublicKey(), null);


                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    msg.writeTo(out);

                    // verifiy if brokerCer is signed by ca
                    verifyDigitalSignature(parseBase64Binary(valueString), out.toByteArray(), kp);
                }
                else {
                    // get first header element
                    Name name = se.createName("HmKsUpaBroker", "Upa", "http://upa");
                    Iterator it = sh.getChildElements(name);

                    // check header element
                    if (!it.hasNext()) {
                        System.out.println("Header element not found.valueString");
                        return true;
                    }
                    SOAPElement element = (SOAPElement) it.next();

                    // get header element value
                    String valueString = element.getValue();

                    // request certificate to ca
                    if (timesCalledCa == 10) {
                        result = requestCertificate("upabroker");
                        timesCalledCa = 0;
                    } else
                        timesCalledCa++;

                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    Certificate brokerCer = cf.generateCertificate(new ByteArrayInputStream(result));
                    KeyPair kp = new KeyPair(brokerCer.getPublicKey(), null);


                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    msg.writeTo(out);
                    // verifiy if brokerCer is signed by ca
                    verifyDigitalSignature(parseBase64Binary(valueString), out.toByteArray(), kp);
                }

                Name name = se.createName("TimeStampNonce", "Upa", "http://upa");
                Iterator it = sh.getChildElements(name);

                if(!it.hasNext()) {
                    System.out.println("Header element TimeStampNonce not found!");
                    return true;
                }

                SOAPElement element = (SOAPElement) it.next();
                String valueString = element.getValue();

                Timestamp nonce = valueOf(valueString);
                String nonceStringAux = (new Timestamp(System.currentTimeMillis())).toString();
                Timestamp currentTime = valueOf(nonceStringAux);

                //Difference between nonce and currentTime to check if it's inside a 1min interval
                long diffTime = nonce.getTime() - currentTime.getTime();
                diffTime = diffTime / (60 * 1000);

                //Checks if TimeStamped nonce was already used before,
                //or if the msg was sent in the space of 1 minute
                if(!listTimeStamps.contains(nonce) && 0 <= diffTime && diffTime <= 1)
                    listTimeStamps.add(nonce);
                else
                    return false;
                //FIXME: return false or throw an exception?!?

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }


}
