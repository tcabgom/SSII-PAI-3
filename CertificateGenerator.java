

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.security.auth.x500.X500Principal;


public class CertificateGenerator {

    private static KeyPair generateKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private static X509Certificate generateCertificate(KeyPair keyPair, String dn) throws Exception {
        Date startDate = new Date(); // Validity start date
        Date expiryDate = new Date(startDate.getTime() + TimeUnit.DAYS.toMillis(365)); // Validity end date
        
    }

    private static void saveToKeyStore(KeyPair keyPair, X509Certificate cert, String alias, String keystorePassword, String privateKeyPassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null); // Inicializar el keystore

        keyStore.setKeyEntry(alias, keyPair.getPrivate(), privateKeyPassword.toCharArray(), new java.security.cert.Certificate[]{cert});

        try (FileOutputStream fos = new FileOutputStream("keystore.jks")) {
            keyStore.store(fos, keystorePassword.toCharArray());
        }
    }

    public static void main(String[] args) throws Exception {
        

        KeyPair keyServer = generateKey();
        X509Certificate certificateServer = generateCertificate(keyServer, "server.com");
        saveToKeyStore(keyServer, certificateServer, "server_alias", "keystore_password", "private_key_password");

        System.out.println("Key and certificate saved to keystore successfully.");
    }
}
