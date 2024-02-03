package old.tinac;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Para cada cliente
 * 1 hilo
 * 1 puerto
 */
public class ConnectionHandler extends Thread implements ConnectionParams {
    int port;
    ServerSocket serverSocket;
    Server sv; // Lo necesitamos porque tiene referencias a todos los clientes
    Socket client;
    String clientNick;
    boolean active = true; // true hasta que el cliente haya finalizado

    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(int port, Server sv) throws IOException {
        this.port = port;
        this.sv = sv;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            // 1. Abrir conexión por primera vez
            // Antes de entrar en el bucle, preguntamos al usuario por su nombre
            openConnection();

            sendMessage("Servidor", "Introduce un nombre de usuario");
            clientNick = in.readLine();
            logMessageToServer("Servidor", clientNick + " Se acaba de conectar, ¡a pasarlo súper a tope!");
            sendMessage("Servidor", "Te has conectado con el nombre de usuario " + clientNick);
            sv.broadcastMessage("Servidor", clientNick + " entró al chat",this);
            out.println("ACK (" + getTime() + ")");
            // TODO: broadcast/retransmitir mensaje a todos los clientes
            closeStreams();

            while (active) {
                openConnection();

                // 2. Esperar mensaje del cliente
                String clientMessage = in.readLine();
                logMessageToServer(clientNick, clientMessage); // muestra y almacena mensaje
                sv.broadcastMessage(clientNick, clientMessage, this);
                active = !clientMessage.equals(EXIT_COMMAND);

                // 3. Envío de confirmación (Acknowledge)
                out.println("ACK (" + getTime() + ")");

                // 4. Cerrar canales
                closeStreams();
            }
            endConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        // 1. Aceptar conexión
        client = serverSocket.accept();

        // 2. Mostrar mensaje de conexión establecida
         System.out.printf("[%s] Servidor: cliente %s se ha conectado a puerto %d\n",
              getTime(), client.getRemoteSocketAddress(), port);

        // 3. Abrir streams para comunicación
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    private String getTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }

    // Muestra en la aplicación servidor un mensaje del cliente
    private void logMessageToServer(String messenger, String message) {
        String log = String.format("[%s] %s: %s", getTime(), messenger , message);
        System.out.println(log);
        saveMessage(log);
    }

    // Envía un mensaje al cliente
    public void sendMessage(String messenger, String message) {
        String msg = String.format("[%s] %s: %s", getTime(), messenger, message);
        out.println(msg);
        saveMessage(msg);
    }

    private void saveMessage(String mensaje) {
        try {
            FileWriter fw = new FileWriter("chat.txt", true);
            fw.write("\r\n" + mensaje);
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String identificarHost(SocketAddress IPcliente) {
        return IPcliente.toString().split("\\.")[3].split(":")[0];
    }

    public void endConnection() throws IOException {
        closeStreams();
        client.close();
        serverSocket.close();
    }

    public void closeStreams() throws IOException {
        in.close();
        out.close();
    }
}
