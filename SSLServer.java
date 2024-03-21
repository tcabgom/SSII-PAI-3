

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class SSLServer {
    private static final int PORT = 7070;
    private static final String KEYSTORE_PATH = "C:\\SSLStore\\PAI 3 SSII\\keystore.jks";
    private static final String KEYSTORE_PASSWORD = "123456";

    public static void main(String[] args) {
        try {
            // Cargar el keystore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

            // Configurar el socket factory SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory socketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) socketFactory.createServerSocket(PORT);

            // Especificar la CipherSuite que queremos habilitar
            String[] enabledCipherSuites = {
                "TLS_AES_256_GCM_SHA384",   // AES 256 bits GCM para confidencialidad
                "TLS_AES_128_GCM_SHA256",   // AES 128 bits GCM para confidencialidad (opcional)
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",  // ECDHE con curva elíptica y ECDSA para autenticación
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",  // ECDHE con curva elíptica y ECDSA para autenticación (opcional)
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",    // ECDHE con curva elíptica y RSA para autenticación
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"     // ECDHE con curva elíptica y RSA para autenticación (opcional)
            };

            serverSocket.setEnabledCipherSuites(enabledCipherSuites);

            // Configurar para que el servidor envíe su certificado al cliente
            serverSocket.setNeedClientAuth(true);

            System.out.println("Servidor listo para aceptar conexiones...");

            while (true) {
                // Esperar conexiones entrantes
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Conexión aceptada desde: " + clientSocket.getInetAddress());

                // Enviar el certificado al cliente
                X509Certificate[] serverCertificates = (X509Certificate[]) clientSocket.getSession().getLocalCertificates();
                if (serverCertificates != null) {
                    for (X509Certificate certificate : serverCertificates) {
                        System.out.println("Enviando certificado al cliente: " + certificate.getSubjectX500Principal());
                    }
                }

                // Manejar la conexión en un hilo separado para permitir múltiples conexiones simultáneas
                new ConnectionHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionHandler extends Thread {
        private SSLSocket clientSocket;
    
        public ConnectionHandler(SSLSocket clientSocket) {
            this.clientSocket = clientSocket;
        }
    
        @Override
        public void run() {
            try {
                // Crear un BufferedReader para leer desde el socket del cliente
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Conexión aceptada");
                // Crear un PrintWriter para escribir en el socket del cliente
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    
                // Leer y responder mensajes del cliente en un bucle
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Mensaje recibido del cliente: " + message);
    
                    // Responder al cliente
                    String response = "¡Hola, cliente!";
                    out.println(response);
                    System.out.println("Respuesta enviada al cliente: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Cerrar el socket del cliente al finalizar el manejo de la conexión
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }    

}
