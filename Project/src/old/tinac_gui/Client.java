package old.tinac_gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements ConnectionParams {

    private String serverIP;
    private int serverPort;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public Client(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        socket = new Socket(serverIP, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void stop() throws IOException {
        closeStreams();
        socket.close();
    }

    public void closeStreams() throws IOException {
        in.close();
        out.close();
    }

    public String readMessage() throws IOException {
        return in.readLine();
    }

    public boolean canRead() throws IOException {
        return in.ready();
    }

    public void sendMessage(String mensaje) {
        out.println(mensaje);
    }


    /**
     * 1. Establece conexión con hilo principal
     * 2. Espera a que el servidor envíe el nuevo puerto
     * 3. Cierra la conexión con el servidor
     * 4. Establece nueva conexión con el puerto que ha recibido del servidor
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            Client cliente = new Client("localhost", DEFAULT_PORT);

            ClientFrame frame = new ClientFrame("T.I", cliente);
            frame.setVisible(true);
            System.out.println(frame);

            cliente.start(); // 1
            int newPort = Integer.parseInt(cliente.readMessage()); // 2
            cliente.stop(); // 3
            cliente.serverPort = newPort;

            String userInput;
            cliente.start(); // 4
            do {
                ServerListener serverListener = new ServerListener(cliente, frame);
                serverListener.start();

                /*
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                userInput = br.readLine(); // Recibe entrada por teclado
                cliente.sendMessage(userInput); // Envía mensaje al servidor
                */


                //Cerramos la comunicación y paramos el hilo que controla la entrada del servidor
                serverListener.join(); // Esperamos a que finalize el hilo
            } while (true);
           /*
            cliente.closeStreams();
            cliente.stop();
            */
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}

/**
 * Esta clase imprime los mensajes del servidor
 */
class ServerListener extends Thread {
    private Client client;
    private ClientFrame frame;

    ServerListener(Client client, ClientFrame frame) {
        this.client = client;
        this.frame = frame;
    }

    @Override
    public void run() {
        boolean acknowledged = false;
        try {
            while (!acknowledged) {
                if (client.canRead()) { // comprobación necesaria para que el hilo no quede a la espera
                    String receivedMessage = client.readMessage();
                    acknowledged = receivedMessage.startsWith("ACK");
                    if (!acknowledged) { // No imprimir el mensaje si es "ACK
                        System.out.println(receivedMessage);
                        frame.receiveMessage(receivedMessage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
