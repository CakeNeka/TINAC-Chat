package chat;

import helper.ChatConstants;
import helper.ChatUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectionHandler extends Thread implements ChatConstants {
    static List<ConnectionHandler> connections = new ArrayList<>();
    private ServerSocket server;
    private Socket client;

    private boolean active;
    private BufferedReader input;
    private PrintWriter output;

    private String clientNick;
    private int clientRoom;

    static void broadcastMessage(String sender, String msg, int room) {
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
        String[] args = msg.split(" ");
        switch (args[0]) {
            case COMMAND_QUIT -> {
                return false;
            }
            case COMMAND_NICK -> {
                changeNick(args);
                return true;
            }
            case COMMAND_ROOM -> {
                setRoom(args);
                return true;
            }
        }

        broadcastMessage(clientNick, msg, clientRoom);
        return true;
    }

    private void changeNick(String[] msg) {
        if (msg.length > 1) {
            String oldNick = clientNick;
            clientNick = String.join(" ", Arrays.copyOfRange(msg,1,msg.length));
            sendMessage("Servidor", "Tu nick ahora es " + clientNick);
            broadcastMessage("Servidor", oldNick + " cambió su nombre por " + clientNick,clientRoom);
        } else {
            sendMessage("Servidor", "Has cometido un terrible error");
        }
    }

    private void sendMessage(String sender, String message) {
        output.println(ChatUtils.formatMessage(sender, message));
    }

    private void setRoom(String[] msg) {
        if (msg.length == 1) {
            sendMessage("Servidor", "Te encuentras en la sala" + clientRoom);
        } else {
            try {
                clientRoom = Integer.parseInt(msg[1]);
                sendMessage("Servidor", "Has cambiado a la sala " + clientRoom);
                broadcastMessage("Servidor", clientNick + " acaba de unirse a la sala " + clientRoom, clientRoom);
            } catch (NumberFormatException ex) {
                sendMessage("Servidor", "Error. Operación no permitida");
            }
        }
    }
}
