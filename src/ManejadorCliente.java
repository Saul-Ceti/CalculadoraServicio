import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private static Map<Socket, String> operaciones = new HashMap<>();

    public ManejadorCliente(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());
        ) {
            String mensaje = (String) in.readObject();
            operaciones.put(clienteSocket, mensaje);

            if(operaciones.size() == 2){
                //Obtener los 2 números y los operadores de las operaciones
                double primerNumero = Double.parseDouble(operaciones.get(clienteSocket).substring(0, operaciones.get(clienteSocket).indexOf(" ")));
                double segundoNumero = Double.parseDouble(operaciones.get(getOtroClienteSocket(clienteSocket)).substring(0, operaciones.get(getOtroClienteSocket(clienteSocket)).indexOf(" ")));
                char operador1 = operaciones.get(clienteSocket).charAt(operaciones.get(clienteSocket).length() - 2);
                char operador2 = operaciones.get(getOtroClienteSocket(clienteSocket)).charAt(operaciones.get(getOtroClienteSocket(clienteSocket)).length() - 2);

                String resultadoCliente1 = realizarOperacion(primerNumero, segundoNumero, operador1);
                String resultadoCliente2 = realizarOperacion(segundoNumero, primerNumero, operador2);

                //out.writeObject(resultadoCliente1);
                //out.flush();

                //ObjectOutputStream otroClienteOut = new ObjectOutputStream(getOtroClienteSocket(clienteSocket).getOutputStream());
                //otroClienteOut.writeObject(resultadoCliente2);
                //otroClienteOut.flush();

                operaciones.clear();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error en la conexión con el cliente: " + e.getMessage());
        }
    }

    private String realizarOperacion(Double num1, Double num2, char operador) {
        try {
            // Realizar la operación
            double resultado = 0;
            switch (operador) {
                case '+':
                    resultado = num1 + num2;
                    break;
                case '-':
                    resultado = num1 - num2;
                    break;
                case '*':
                    resultado = num1 * num2;
                    break;
                case '/':
                    resultado = num1 / num2;
                    break;
            }

            return String.valueOf(resultado);
        } catch (Exception e) {
            return "Error en la operación";
        }
    }

    private Socket getOtroClienteSocket(Socket clienteSocket) {
        for (Socket socket : operaciones.keySet()) {
            if (socket != clienteSocket)
                return socket;
        }

        return null;
    }
}
