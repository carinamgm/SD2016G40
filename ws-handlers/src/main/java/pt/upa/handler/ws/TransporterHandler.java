package pt.upa.handler.ws;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public class TransporterHandler extends AbstractHandler {

    public boolean handleMessage(SOAPMessageContext smc) {
        System.out.println("AddHeaderHandler: Handling message.");

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                /*System.out.println("Writing header in outbound SOAP message...");

                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // add header element (name, namespace prefix, namespace)
                Name name = se.createName("myHeader", "d", "http://demo");
                SOAPHeaderElement element = sh.addHeaderElement(name);

                // add header element value
                int value = 22;
                String valueString = Integer.toString(value);
                element.addTextNode(valueString);*/

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

                // get first header element
                Name name = se.createName("HmKsUpaBroker", "Upa", "http://upa");
                Iterator it = sh.getChildElements(name);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                String strMsg = new String(out.toByteArray());
                System.out.println(strMsg);


                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();

                // request certificate to ca
                byte[] result = requestCertificate("upabroker");
                CertificateFactory cf   = CertificateFactory.getInstance("X.509");
                Certificate brokerCer = cf.generateCertificate(new ByteArrayInputStream(result));
                KeyPair kp = new KeyPair(brokerCer.getPublicKey(),null);

                // verifiy if brokerCer is signed by ca
                verifyDigitalSignature(parseBase64Binary(valueString),convertDocString(se.getBody().extractContentAsDocument()).getBytes(),kp);
            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }


}