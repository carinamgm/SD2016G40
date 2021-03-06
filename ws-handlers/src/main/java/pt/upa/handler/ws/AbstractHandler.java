package pt.upa.handler.ws;

import pt.upa.ca.CaClient;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Set;

public abstract class AbstractHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public abstract boolean handleMessage(SOAPMessageContext smc);

    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }

    protected KeyPair getKeyPair(String filepath,String pwdPath,String certificate) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("jks");
        String passwords = readPasswordFile(pwdPath);
        String storePass = passwords.substring(0,passwords.indexOf(" "));
        String keyPass = passwords.substring(storePass.length()+1,passwords.length()-1);

        ks.load(new FileInputStream(new File(filepath)),storePass.toCharArray());
        return new KeyPair(ks.getCertificate(certificate).getPublicKey(), (PrivateKey) ks.getKey(certificate,keyPass.toCharArray()));
    }

    protected PublicKey extractCaCertificateKey(String filepath, String pwdPath) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("jks");
        String passwords = readPasswordFile(pwdPath);
        String storePass = passwords.substring(0,passwords.indexOf(" "));

        ks.load(new FileInputStream(new File(filepath)),storePass.toCharArray());

        return ks.getCertificate("ca").getPublicKey();
    }

    protected byte[] makeDigitalSignature(byte[] bytes, KeyPair keyPair) throws Exception {

        // get a signature object using the SHA-1 and RSA combo
        // and sign the input with the private key
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(bytes);
        byte[] signature = sig.sign();

        return signature;
    }

    private String readPasswordFile(String filename) throws IOException {
        String content = null;
        File file = new File(filename);
        FileReader reader = new FileReader(file);

        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        content = new String(chars);
        reader.close();

        return content;
    }

    public static byte[] requestCertificate(String entity) {
        CaClient caCli = new CaClient();

        return caCli.requestCertificate(entity);
    }

    protected boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, KeyPair keyPair) throws Exception {

        // verify the signature with the public key
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(keyPair.getPublic());
        sig.update(bytes);
        try {
            return sig.verify(cipherDigest);
        } catch (SignatureException se) {
            System.err.println("Caught exception while verifying " + se);
            return false;
        }
    }


    public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
        try {
            certificate.verify(caPublicKey);
        } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
                | SignatureException e) {
            return false;
        }
        return true;
    }

}
