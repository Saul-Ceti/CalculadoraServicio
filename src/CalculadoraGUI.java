import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CalculadoraGUI extends JFrame {
    private JTextField textField;
    private Socket serverSocket;

    public CalculadoraGUI() {
        setTitle("Calculadora");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textField = new JTextField();
        textField.setEditable(false);

        JButton[] digitos = new JButton[10];
        for (int i = 0; i < 10; i++) {
            digitos[i] = new JButton(Integer.toString(i));
            digitos[i].addActionListener(new DigitosActionListener());
        }

        JButton[] operadores = new JButton[] {
            new JButton("+"),
            new JButton("-"),
            new JButton("*"),
            new JButton("/"),
        };

        for (JButton operador : operadores) {
            operador.addActionListener(new OperadoresActionListener());
        }

        JButton botonIgual = new JButton("=");
        botonIgual.addActionListener(new IgualActionListener());

        setLayout(new BorderLayout());
        add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 4));
        for (JButton button : digitos) {
            buttonPanel.add(button);
        }
        for (JButton button : operadores) {
            buttonPanel.add(button);
        }
        buttonPanel.add(botonIgual);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private class DigitosActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = (JButton) e.getSource();
            textField.setText(textField.getText() + boton.getText());
        }
    }

    private class OperadoresActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = (JButton) e.getSource();
            textField.setText(textField.getText() + " " + boton.getText() + " ");
        }
    }

    private class IgualActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Crea un nuevo socket cada vez que se presiona "="
                Socket serverSocket = new Socket("localhost", 5000);
                try (ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(serverSocket.getInputStream())) {

                    // Enviar la operación al servidor
                    out.writeObject(textField.getText());
                    out.flush();

                    // Esperar la respuesta del servidor
                    String resultado = (String) in.readObject();

                    // Mostrar el resultado en la calculadora
                    textField.setText(resultado);

                } finally {
                    serverSocket.close();
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculadoraGUI::new);
    }
}
