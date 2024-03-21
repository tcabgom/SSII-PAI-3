import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class SSLClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7070;

    public static void main(String[] args) {
        try {
            // Configurar el socket factory SSL
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, null, null);
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(SERVER_HOST, SERVER_PORT);

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

