package chat;

import helper.ChatConstants;
import helper.ChatUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionHandler extends Thread implements ChatConstants {
    static List<ConnectionHandler> connections = new ArrayList<>(); // TODO use map <room,ConHandler>
    static ConcurrentHashMap<Integer, List<String>> roomMessageMap = new ConcurrentHashMap<>();
    private ServerSocket server;
    private Socket client;

    private boolean active;
    private BufferedReader input;
    private PrintWriter output;

    private String clientNick;
    private int clientRoom;

    static void broadcastMessage(String sender, String msg, int room) {
        if (!roomMessageMap.containsKey(room)) {
            roomMessageMap.put(room, new ArrayList<>());
        }
        roomMessageMap.get(room).add(ChatUtils.formatMessage(sender, msg));
        connections.stream().filter(c -> c.clientRoom == room).forEach(c -> c.sendMessage(sender, msg));
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
            while (active) {
                String clientMessage = input.readLine();
                System.out.println(clientMessage);
                active = manageMessage(clientMessage);
            }

            input.close();
            output.close();
            client.close();
            server.close();
            connections.remove(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean manageMessage(String msg) {
        msg = msg.trim();
        if (msg.startsWith("/")) {
            return handleCommands(msg);
        }

        broadcastMessage(clientNick, msg, clientRoom);
        return true;
    }

    private boolean handleCommands(String msg) {
        String[] args = msg.split(" ");
        switch (args[0]) {
            case COMMAND_HELP -> sendMessage("Servidor",ChatConstants.HELP_MESSAGE);
            case COMMAND_QUIT -> {
                return false;
            }
            case COMMAND_NICK -> changeNick(args);
            case COMMAND_ROOM -> changeRoom(args);
            case COMMAND_CONNECTED_USERS -> showUsers();
            case COMMAND_ACTIVE_ROOMS -> showActiveRooms();
            case COMMAND_NET_INFO -> showNetInfo();
            default -> sendMessage("Servidor", "Comando \"" + msg + "\" no reconocido");
        }
        return true;

    }


    private void sendMessage(String sender, String message) {
        output.println(ChatUtils.formatMessage(sender,message));
    }

    private void changeNick(String[] msg) {
        if (msg.length > 1) {
            String oldNick = clientNick;
            clientNick = String.join(" ", Arrays.copyOfRange(msg,1,msg.length));
            sendMessage("Servidor", "Tu nick ahora es " + clientNick);
            broadcastMessage("Servidor", oldNick + " cambió su nombre por " + clientNick,clientRoom);
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
                sendMessage("Servidor", "Has cambiado a la sala " + clientRoom);
                sendMessage("Servidor", "Recuperando mensajes...");

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
        connections.forEach(ch -> output.println(String.format(
                "Nick: %-40s Sala: %s",ch.clientNick, ch.clientRoom
        )));
    }

    private void showActiveRooms() {
        List<Integer> openRooms = getOpenRooms();
        String msg = String.format("%d salas activas: %s", openRooms.size(), openRooms);

        sendMessage("Servidor", msg);
    }

    private List<Integer> getOpenRooms(){
        Set<Integer> uniqueRooms = new HashSet<>();
        connections.forEach(ch -> uniqueRooms.add(ch.clientRoom));
        return uniqueRooms.stream().sorted().toList(); // TODO: esto no es muy eficiente
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
                server.getInetAddress().toString(),
                server.getLocalPort(),
                client.getInetAddress(),
                client.getLocalPort() + " | " + client.getLocalPort() // Todo: ¿Cual es el bueno?
        );
        sendMessage("Servidor" , message);
    }
}
