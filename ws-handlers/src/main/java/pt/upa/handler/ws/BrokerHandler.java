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
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class BrokerHandler extends AbstractHandler {

    public static final String CONTEXT_PROPERTY = "my.property";

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

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);

                // sign and digest
                KeyPair kp = getKeyPair("keys/UpaBroker.jks","keys/KeyStorePwd","upabroker");
                String encodedSignedBody = printBase64Binary(makeDigitalSignature(out.toByteArray(),kp));

                // add header element
                Name name = se.createName("HmKsUpaBroker", "Upa", "http://upa");
                SOAPHeaderElement element = sh.addHeaderElement(name);
                element.addTextNode(encodedSignedBody);

                // make the changes permanent
                msg.saveChanges();

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
                Name name = se.createName("name", "Upa", "http://upa");
                Iterator it = sh.getChildElements(name);

                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }

                SOAPElement element = (SOAPElement) it.next();
                String entity = element.getValue();

                name = se.createName("HmKs"+entity, "Upa", "http://upa");
                it = sh.getChildElements(name);

                if (!it.hasNext()) {
                    System.out.println("Header element not found1.");
                    return true;
                }

                element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();

                // request certificate to ca
                byte[] result = requestCertificate(entity);
                CertificateFactory cf   = CertificateFactory.getInstance("X.509");
                Certificate brokerCer = cf.generateCertificate(new ByteArrayInputStream(result));
                KeyPair kp = new KeyPair(brokerCer.getPublicKey(),null);


                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                // verifiy if brokerCer is signed by ca
                verifyDigitalSignature(parseBase64Binary(valueString),out.toByteArray(),kp);

                // put header in a property context
                smc.put(CONTEXT_PROPERTY, out.toByteArray());
                // set property scope to application client/server class can access it
                smc.setScope(CONTEXT_PROPERTY, MessageContext.Scope.APPLICATION);

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }


}