package pt.upa.handler.ws;

import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.After;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;

import static org.junit.Assert.*;


/**
 *  Handler test suite
 */
public class HeaderHandlerTest extends AbstractHandlerTest {

    private static Handler _handler = new Handler();
    // tests

    @After
    public void tearDown() {
        _handler.serviceName = "";
    }

    @Test
    public void testHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST;
        // System.out.println(soapText);

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

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        // assert header element
        Name name = soapEnvelope.createName("TimeStampNonce", "Upa", "http://upa");
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());

        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        //soapMessage.writeTo(System.out);
        assertNotEquals(new Timestamp(System.currentTimeMillis()).toString(), valueString);
    }

    @Test
    public void testHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
       /* final String soapText = SOAP_REQUEST.replace("<SOAP-ENV:Header/>",
            "<SOAP-ENV:Header>" +
            "<d:myHeader xmlns:d=\"http://demo\">22</d:myHeader>" +
            "</SOAP-ENV:Header>"); */
        //System.out.println(soapText);


        final String soapText = SOAP_RESPONSE;
        char[] cenas = new char[9999];
        Arrays.fill(cenas, 'f');
        String nonce = (new Timestamp(System.currentTimeMillis())).toString();
        soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText.replace("<Upa:HmKs xmlns:Upa=\"http://upa\"></Upa:HmKs>",
                "<Upa:HmKs xmlns:Upa=\"http://upa\">" + cenas.toString() + "</Upa:HmKs>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

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

        // Additional verification code, if any, either here or before the verification block.
        // assert that message would proceed normally
        assertTrue(handleResult);

        //soapMessage.writeTo(System.out);
    }

    @Test
    public void testHeaderHandlerWrongTimer(
            @Mocked final SOAPMessageContext soapMessageContext)
            throws Exception {

        final String soapText = SOAP_REQUEST_INBOUND;
        char[] cenas = new char[9999];
        Arrays.fill(cenas, 'f');
        String nonce = (new Timestamp(1241514214)).toString();
        soapText.replace("<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>",
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\">" + nonce + "</Upa:TimeStampNonce>");
        soapText.replace("<Upa:HmKs xmlns:Upa=\"http://upa\"></Upa:HmKs>",
                "<Upa:HmKs xmlns:Upa=\"http://upa\">" + cenas.toString() + "</Upa:HmKs>");

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        _handler.serviceName = "UpaTransporter2";


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
        assertTrue(handleResult);

        //soapMessage.writeTo(System.out);
    }

    @Test
    public void testHeaderHandlerWrongMessage(
            @Mocked final SOAPMessageContext soapMessageContext)
            throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = SOAP_REQUEST;
        final String soapTextAux = WRONG_SOAP_RESPONSE;

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;
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

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue(handleResult);
    }

}
