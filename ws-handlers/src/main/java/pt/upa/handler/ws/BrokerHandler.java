package pt.upa.handler.ws;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyPair;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class BrokerHandler extends AbstractHandler {

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

                // sign and digest
                KeyPair kp = getKeyPair("keys/UpaBroker.jks","keys/KeyStorePwd","upabroker");
                SOAPBody sb = se.getBody();
                String bodyString = convertDocString(sb.extractContentAsDocument());
                String encodedSignedBody = printBase64Binary(makeDigitalSignature(bodyString.getBytes(),kp));

                // add header element
                Name name = se.createName("HmKsUpaBroker", "Upa", "http://upa");
                SOAPHeaderElement element = sh.addHeaderElement(name);
                element.addTextNode(encodedSignedBody);

                // make the changes permanent
                msg.saveChanges();

                /*ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                String strMsg = new String(out.toByteArray());
                System.out.println(strMsg);

                MessageFactory factory = MessageFactory.newInstance();
                SOAPMessage newMessage = factory.createMessage(msg.getMimeHeaders(), new ByteArrayInputStream(out.toByteArray()));
                msg.getSOAPPart().setContent(newMessage.getSOAPPart().getContent());*/

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                msg.writeTo(out);
                String strMsg = new String(out.toByteArray());
                System.out.println(strMsg);


            } else {
                /*System.out.println("Reading header in inbound SOAP message...");

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
                Name name = se.createName("myHeader", "d", "http://demo");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();
                int value = Integer.parseInt(valueString);

                // print received header
                System.out.println("Header value is " + value);

                // put header in a property context
                smc.put(CONTEXT_PROPERTY, value);
                // set property scope to application client/server class can access it
                smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);*/

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }

}