package pt.upa.handler.ws;

import mockit.Expectations;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.Iterator;

import static java.sql.Timestamp.valueOf;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static org.junit.Assert.*;

/**
 *  Handler test suite
 */
public class HeaderHandlerTest extends AbstractHandlerTest {

    private static Handler _handler;
    // tests

    @BeforeClass
    public static void oneTimeSetUp() {
        _handler = new Handler();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        _handler = null;
    }

    @After
    public void tearDown() {
        _handler.serviceName = "";
    }

    @Test
    public void testHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        String soapText = SOAP_REQUEST;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;
        _handler.serviceName = "UpaBroker";

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};

        // Unit under test is exercised.
        boolean handleResult = _handler.handleMessage(soapMessageContext);

        // assert that message would proceed normally
        assertTrue(handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        // assert header elements and their values
        Name name = soapEnvelope.createName("TimeStampNonce", "Upa", "http://upa");
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();

        //assert veracity of nonce TimeStamp
        String nonceStringAux = (new Timestamp(System.currentTimeMillis())).toString();
        Timestamp currentTime = valueOf(nonceStringAux);
        long diffTime = currentTime.getTime() - valueOf(valueString).getTime();
        diffTime = diffTime / (60 * 1000);
        assertTrue((0 <= diffTime) && (diffTime <= 1));

        //test veracity of ca, verifyDigSig
    }

    @Test
    public void testHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.

        _handler.serviceName = "UpaTransporter2";
        String soapText = SOAP_RESPONSE;
        String nonce = (new Timestamp(System.currentTimeMillis())).toString();
        final Boolean soapOutbound = false;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(nonce.getBytes());
        String body = "<S:Body>" +
                "<ns2:requestJobResponse xmlns:ns2=\"http://ws.transporter.upa.pt/\">" +
                "<return>" +
                "<JobState>PROPOSED</JobState>" +
                "<CompanyName>UpaTransporter2</CompanyName>" +
                "<JobDestination>Porto</JobDestination>" +
                "<JobOrigin>Lisboa</JobOrigin>" +
                "<JobPrice>16</JobPrice>" +
                "<JobIdentifier>1</JobIdentifier>" +
                "</return>" +
                "</ns2:requestJobResponse>" +
                "</S:Body>";
        out.write(body.getBytes());

        // sign and digest
        KeyPair kp = _handler.getKeyPair("keys/" + _handler.serviceName + "/" + _handler.serviceName + ".jks", "keys/KeyStorePwd", _handler.serviceName.toLowerCase());
        String encodedSignedBody = printBase64Binary(_handler.makeDigitalSignature(out.toByteArray(), kp));

        soapText = soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText = soapText.replace("<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>",
                "<Upa:hmks xmlns:Upa=\"http://upa\">" + encodedSignedBody + "</Upa:hmks>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());

        _handler.serviceName = "UpaBroker";
        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};

        // Unit under test is exercised.
        boolean handleResult = _handler.handleMessage(soapMessageContext);

        // assert that message would proceed normally
        assertTrue(handleResult);
    }

    @Test
    public void testHeaderHandlerWrongTimer(
            @Mocked final SOAPMessageContext soapMessageContext)
            throws Exception {

        _handler.serviceName = "UpaTransporter2";
        String soapText = SOAP_RESPONSE;
        String nonce = (new Timestamp(1241514214)).toString();
        final Boolean soapOutbound = false;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(nonce.getBytes());
        String body = "<S:Body>" +
                "<ns2:requestJobResponse xmlns:ns2=\"http://ws.transporter.upa.pt/\">" +
                "<return>" +
                "<JobState>PROPOSED</JobState>" +
                "<CompanyName>UpaTransporter2</CompanyName>" +
                "<JobDestination>Porto</JobDestination>" +
                "<JobOrigin>Lisboa</JobOrigin>" +
                "<JobPrice>16</JobPrice>" +
                "<JobIdentifier>1</JobIdentifier>" +
                "</return>" +
                "</ns2:requestJobResponse>" +
                "</S:Body>";
        out.write(body.getBytes());

        // sign and digest
        KeyPair kp = _handler.getKeyPair("keys/" + _handler.serviceName + "/" + _handler.serviceName + ".jks", "keys/KeyStorePwd", _handler.serviceName.toLowerCase());
        String encodedSignedBody = printBase64Binary(_handler.makeDigitalSignature(out.toByteArray(), kp));

        soapText = soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText = soapText.replace("<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>",
                "<Upa:hmks xmlns:Upa=\"http://upa\">" + encodedSignedBody + "</Upa:hmks>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());

        _handler.serviceName = "UpaBroker";
        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};
        // Unit under test is exercised.
        //stuff to Test
        boolean handleResult = _handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.
        // assert that message would proceed normally
        assertFalse(handleResult);
    }

    @Test
    public void testHeaderHandlerWrongMessage(
            @Mocked final SOAPMessageContext soapMessageContext)
            throws Exception {

        // Preparation code not specific to JMockit, if any.
        _handler.serviceName = "UpaTransporter2";
        final Boolean soapOutbound = false;
        String soapText = SOAP_RESPONSE;

        String nonce = (new Timestamp(System.currentTimeMillis())).toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(nonce.getBytes());

        out.write(soapText.getBytes());

        // sign and digest
        KeyPair kp = _handler.getKeyPair("keys/" + _handler.serviceName + "/" + _handler.serviceName + ".jks", "keys/KeyStorePwd", _handler.serviceName.toLowerCase());
        String encodedSignedBody = printBase64Binary(_handler.makeDigitalSignature(out.toByteArray(), kp));

        soapText = soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText = soapText.replace("<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>",
                "<Upa:hmks xmlns:Upa=\"http://upa\">" + encodedSignedBody + "</Upa:hmks>");
        soapText = soapText.replace("<JobPrice>16</JobPrice>", "<JobPrice>32</JobPrice>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());

        _handler.serviceName = "UpaBroker";
        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            returns(soapOutbound);

            soapMessageContext.getMessage();
            returns(soapMessage);
        }};

        // Unit under test is exercised.
        Boolean handleResult = _handler.handleMessage(soapMessageContext);
        assertFalse(handleResult);
    }

    @Test
    public void testHeaderHandlerWrongCa(
            @Mocked final SOAPMessageContext soapMessageContext)
            throws Exception {

        // Preparation code not specific to JMockit, if any.
        _handler.serviceName = "UpaBroker";
        final Boolean soapOutbound = false;

        // byte[] byteAux = {121};
        // String entity = "upabroker";

        String soapText = SOAP_REQUEST_INBOUND;

        String nonce = (new Timestamp(System.currentTimeMillis())).toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(nonce.getBytes());
        String body = "<S:Body>" +
                "<ns2:requestJob xmlns:ns2=\"http://ws.transporter.upa.pt/\">" +
                "<origin>Lisboa</origin>" +
                "<destination>Porto</destination>" +
                "<price>20</price>" +
                "</ns2:requestJob>" +
                "</S:Body>";
        out.write(body.getBytes());

        // sign and digest
        KeyPair kp = _handler.getKeyPair("keys/" + _handler.serviceName + "/" + _handler.serviceName + ".jks", "keys/KeyStorePwd", _handler.serviceName.toLowerCase());
        String encodedSignedBody = printBase64Binary(_handler.makeDigitalSignature(out.toByteArray(), kp));

        soapText = soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText = soapText.replace("<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>",
                "<Upa:hmks xmlns:Upa=\"http://upa\">" + encodedSignedBody + "</Upa:hmks>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());

        _handler.serviceName = "UpaTransporter2";
        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            returns(soapOutbound);

            soapMessageContext.getMessage();
            returns(soapMessage);
        }};

        _handler._certificates.put("upabroker",_handler.requestCertificate("rougue"));

        // Missing generating certificate
        // Unit under test is exercised.
        boolean handleResult = _handler.handleMessage(soapMessageContext);
        assertTrue(!handleResult);
    }

}
