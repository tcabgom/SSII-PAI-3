import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class SSLClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7070;
    private static final String KEYSTORE_PATH = "C:\\SSLStore\\PAI 3 SSII\\keystore.jks";
    private static final String KEYSTORE_PASSWORD = "123456";

    public static void main(String[] args) {
        try {
            // Configurar el contexto SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");

            // Cargar el keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

            // Configurar el gestor de claves
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

            // Configurar el contexto SSL con el gestor de claves y un TrustManager personalizado
            sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{new CustomTrustManager()}, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(SERVER_HOST, SERVER_PORT);

            // Obtener el certificado del servidor
            X509Certificate[] serverCertificates = (X509Certificate[]) socket.getSession().getPeerCertificates();
            if (serverCertificates != null) {
                for (X509Certificate certificate : serverCertificates) {
                    System.out.println("Certificado del servidor: " + certificate.getSubjectX500Principal());
                    certificate.checkValidity();
                    System.out.println("Certificado válido: ");
                }
            }

            // Configurar la CipherSuite
            String[] enabledCipherSuites = {
                "TLS_AES_256_GCM_SHA384",   // AES 256 bits GCM para confidencialidad
                "TLS_AES_128_GCM_SHA256",   // AES 128 bits GCM para confidencialidad (opcional)
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",  // ECDHE con curva elíptica y ECDSA para autenticación
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",  // ECDHE con curva elíptica y ECDSA para autenticación (opcional)
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",    // ECDHE con curva elíptica y RSA para autenticación
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"     // ECDHE con curva elíptica y RSA para autenticación (opcional)
            };
            socket.setEnabledCipherSuites(enabledCipherSuites);

            // Realizar operaciones con el servidor...
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar un mensaje al servidor
            String message = "Hola, servidor!";
            out.println(message);
            System.out.println("Mensaje enviado al servidor: " + message);

            // Recibir respuesta del servidor
            String response = in.readLine();
            System.out.println("Respuesta del servidor: " + response);

            // Cerrar el socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
