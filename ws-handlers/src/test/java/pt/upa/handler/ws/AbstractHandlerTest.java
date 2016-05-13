package pt.upa.handler.ws;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;


/**
 *  Abstract handler test suite
 */
public abstract class AbstractHandlerTest {

    // static members

    //Not everything is needed, testing faults needed??? Depends... Test case when false is returned on the inbound...???
    /** Correct SOAP request message captured with LoggingHandler */
    protected static final String SOAP_REQUEST = "<S:Envelope " +
            "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<SOAP-ENV:Header>" +
            "</SOAP-ENV:Header>" +
            "<S:Body>" +
                "<ns2:requestJob xmlns:ns2=\"http://ws.transporter.upa.pt/\">" +
                    "<origin>Lisboa</origin>" +
                    "<destination>Porto</destination>" +
                    "<price>20</price>" +
                "</ns2:requestJob>" +
            "</S:Body></S:Envelope>";

    /** Correct SOAP response message captured with LoggingHandler */
    protected static final String SOAP_RESPONSE = "<S:Envelope " +
            "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<SOAP-ENV:Header>" +
                "<Upa:name xmlns:Upa=\"http://upa\">upatransporter2</Upa:name>" +
                "<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>" +
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>" +
            "</SOAP-ENV:Header>" +
            "<S:Body>" +
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
            "</S:Body></S:Envelope>";

    /** Wrong SOAP request message captured with LoggingHandler */
    protected static final String SOAP_REQUEST_INBOUND = "<S:Envelope " +
            "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<SOAP-ENV:Header>" +
                "<Upa:hmks xmlns:Upa=\"http://upa\"></Upa:hmks>" +
                "<Upa:TimeStampNonce xmlns:Upa=\"http://upa\"></Upa:TimeStampNonce>" +
            "</SOAP-ENV:Header>" +
            "<S:Body>" +
                "<ns2:requestJob xmlns:ns2=\"http://ws.transporter.upa.pt/\">" +
                    "<origin>Lisboa</origin>" +
                    "<destination>Porto</destination>" +
                    "<price>20</price>" +
                "</ns2:requestJob>" +
            "</S:Body></S:Envelope>";

    /** SOAP message factory */
    protected static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch(SOAPException e) {
            throw new RuntimeException(e);
        }
    }


    // helper functions

    protected static SOAPMessage byteArrayToSOAPMessage(byte[] msg) throws Exception {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg);
        StreamSource source = new StreamSource(byteInStream);
        SOAPMessage newMsg = MESSAGE_FACTORY.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(source);
        return newMsg;
    }


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

}
