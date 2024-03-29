package chat;

import helper.ChatConstants;
import helper.ChatUtils;
import helper.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase para manejar una conexión, se lanza un hilo por cada cliente que se conecta al servidor.
 *
 * Se encarga de intercambiar información con el cliente y de
 * almacenar sus datos (nombre de usuario y sala)
 */
public class ConnectionHandler extends Thread implements ChatConstants {

    // Todos los hilos (ConnectionHandler) acceden a estas dos estructuras
    static List<ConnectionHandler> connections = new CopyOnWriteArrayList<>();
    static ConcurrentHashMap<Integer, List<String>> roomMessageMap = new ConcurrentHashMap<>();

    private final ServerSocket server;
    private Socket client;

    private boolean active;
    private boolean firstNameChange = true;
    private BufferedReader input;
    private PrintWriter output;

    private String clientNick;
    private int clientRoom;

    /**
     * Retransmite un mensaje a los usuarios de una sala
     */
    void broadcastMessage(String sender, String message, int room) {
        if (!roomMessageMap.containsKey(room)) {
            roomMessageMap.put(room, new ArrayList<>());
        }

        Logger.logMessage(sender, message, room);
        roomMessageMap.get(room).add(ChatUtils.formatMessage(sender, message)); // Almacena mensajes en ConcurrentHashMap
        connections.stream().filter(c -> c.clientRoom == room).forEach(c -> c.sendMessage(sender, message)); // Envía mensajes a clientes
    }

    /**
     * Retransmite un mensaje a todos los usuarios
     */
    void broadcastMessage(String message) {
        connections.forEach(con -> con.sendMessage("Servidor", message));
        getOpenRooms().forEach(room -> Logger.logMessage("Servidor", message, room));
    }

    public ConnectionHandler(int port) throws IOException {
        server = new ServerSocket(port);
        clientRoom = 0;
        clientNick = "Cliente " + port;
        active = true;
    }

    @Override
    public void run() {
        try {
            client = server.accept();
            System.out.println("Client connected to port " + server.getLocalPort());
            connections.add(this);

            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);

            output.println(WELCOME_MESSAGE);

            // Bucle principal: recibe información del cliente y la gestiona
            while (active) { // active será false cuando el cliente envíe el comando "/quit"
                String clientMessage = input.readLine();
                active = parseMessage(clientMessage);
            }
            closeConnection();
        } catch (IOException e) {
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
            Logger.logServerError(e.getMessage());
        }
    }

    /**
     * Cierra flujos de entrada/salida y cierra servidor
     */
    private void closeConnection() throws IOException {
        connections.remove(this);
        input.close();
        output.close();
        client.close();
        server.close();
    }


    /**
     * Gestiona la información recibida del cliente.
     * Si es un comando realiza una acción y si es un mensaje lo retransmite
     * a todos los clientes que estén en la misma.
     */
    private boolean parseMessage(String msg) {
        msg = msg.trim();
        if (msg.startsWith("/")) {
            return handleCommands(msg);
        }

        broadcastMessage(clientNick, msg, clientRoom);
        return true;
    }

    /**
     * Controla el sistema de comandos, los comandos están definidos en la clase
     * {@link ChatConstants}
     */
    private boolean handleCommands(String msg) {
        String[] args = msg.split(" ");
        switch (args[0]) {
            case COMMAND_HELP -> sendMessage("Servidor", ChatConstants.HELP_MESSAGE);
            case COMMAND_QUIT -> {
                broadcastMessage(clientNick + " abandonó el chat, ¡Hasta la vista!");
                return false;
            }
            case COMMAND_NICK -> changeNick(args);
            case COMMAND_ROOM -> changeRoom(args);
            case COMMAND_CONNECTED_USERS -> showUsers();
            case COMMAND_ACTIVE_ROOMS -> showActiveRooms();
            case COMMAND_NET_INFO -> showNetInfo();
            case COMMAND_SECRET -> sendMessage("Servidor", CREDITS);
            default -> sendMessage("Servidor", "Comando \"" + msg + "\" no reconocido");
        }
        return true;

    }

    /**
     * Envía un mensaje al cliente asociado a esta conexión
     */
    private void sendMessage(String sender, String message) {
        output.println(ChatUtils.formatMessage(sender, message));
    }

    // Métodos activados por Comandos:
    private void changeNick(String[] msg) {
        if (msg.length > 1) {
            String oldNick = clientNick;
            clientNick = String.join(" ", Arrays.copyOfRange(msg, 1, msg.length));
            sendMessage("Servidor", "Tu nick ahora es " + clientNick);
            output.println("!nick=" + clientNick); // informa que nick cambió, para mostrarlo en la UI
            if (!firstNameChange)
                broadcastMessage("Servidor", oldNick + " cambió su nombre por " + clientNick, clientRoom);
            firstNameChange = false;
        } else {
            sendMessage("Servidor", "Tu nick actualmente es \"" + clientNick + "\"");
        }
    }

    private void changeRoom(String[] msg) {
        if (msg.length == 1) {
            sendMessage("Servidor", "Te encuentras en la sala " + clientRoom);
        } else {
            try {
                clientRoom = Integer.parseInt(msg[1]);
                sendMessage("Servidor", "Te has unido a la sala " + clientRoom);
                sendMessage("Servidor", "Recuperando mensajes...");
                output.println("!room=" + clientRoom);

                // Devuelve todos los mensajes de la sala al cliente recién conectado
                if (roomMessageMap.containsKey(clientRoom))
                    roomMessageMap.get(clientRoom).forEach(output::println);

                broadcastMessage("Servidor", clientNick + " acaba de unirse a la sala " + clientRoom, clientRoom);
            } catch (NumberFormatException ex) {
                sendMessage("Servidor", "Error. Operación no permitida");
            }
        }
    }

    private void showUsers() {
        sendMessage("Servidor", connections.size() + " usuarios conectados:");
        connections.forEach(ch -> output.printf(String.format(
                "Nick: %-40s Sala: %s\n", ch.clientNick, ch.clientRoom
        )));
    }

    private void showActiveRooms() {
        String msg = String.format("%d sala(s) activas: %s", getOpenRooms().size(), getOpenRooms());
        sendMessage("Servidor", msg);
    }

    private List<Integer> getOpenRooms() {
        Set<Integer> uniqueRooms = new HashSet<>();
        connections.forEach(ch -> uniqueRooms.add(ch.clientRoom));
        return uniqueRooms.stream().sorted().toList();
    }

    private void showNetInfo() {
        String message = String.format("""
                        \tInformación:
                        Usuarios Conectados: %d
                        Salas abiertas: %d
                        IP del servidor: %s
                        Puerto del servidor: %d
                        IP del cliente: %s
                        Puerto del cliente: %s
                        """,
                connections.size(),
                getOpenRooms().size(),
                server.getLocalSocketAddress(),
                server.getLocalPort(),
                client.getInetAddress(),
                client.getPort()
        );
        sendMessage("Servidor", message);
    }
}
