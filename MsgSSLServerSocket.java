import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.*;

public class MsgSSLServerSocket {

    public static void main(String[] args) {
        SSLServerSocket serverSocket = null;
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) factory.createServerSocket(3343);

            System.err.println("Waiting for connection...");

            while (true) {
                SSLSocket socket = null;
                try {
                    socket = (SSLSocket) serverSocket.accept();

                    // Open BufferedReader for reading data from client
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // Open PrintWriter for writing data to client
                    PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                    String msg = input.readLine();

                    if ("Hola".equals(msg)) {
                        output.println("Welcome to the Server");
                    } else {
                        output.println("Incorrect message.");
                    }

                    output.close();
                    input.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

