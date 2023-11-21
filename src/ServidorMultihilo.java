import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMultihilo {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            // Creamos el socket del servidor
            serverSocket = new ServerSocket(5000);
            System.out.println("Servidor esperando conexiones...");

            while (true) {
                // Esperamos a que un cliente se conecte
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress() + ":" + clienteSocket.getPort());

                Thread clienteThread = new Thread(new ManejadorCliente(clienteSocket));
                clienteThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}