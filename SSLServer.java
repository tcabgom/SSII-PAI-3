import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SSLServer {
    private static final int PORT = 7070;
    private static final String KEYSTORE_PATH = "C:\\SSLStore\\PAI 3 SSII\\keystore.jks";
    private static final String KEYSTORE_PASSWORD = "Y mis 5€??"; // Cambia esto por la contraseña de tu keystore

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

            System.out.println("Servidor listo para aceptar conexiones...");

            while (true) {
                // Esperar conexiones entrantes
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Conexión aceptada desde: " + clientSocket.getInetAddress());

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
                // Manejar la conexión...
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Leer el mensaje del cliente
                String message = in.readLine();
                System.out.println("Mensaje recibido del cliente: " + message);

                // Responder al cliente
                String response = "¡Hola, cliente!";
                out.println(response);
                System.out.println("Respuesta enviada al cliente: " + response);

                // Cerrar el socket del cliente
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

