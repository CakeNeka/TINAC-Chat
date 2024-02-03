package chat;

import helper.ChatConstants;
import helper.ChatUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements ChatConstants {
    private ServerSocket serverSocket;
    private Socket client;
    private boolean listening = true;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    private void startListening() throws IOException {
        while (listening) {
            client = serverSocket.accept();
            int newPort = ChatUtils.getNextAvailablePort(MIN_PORT,MAX_PORT);
            launchHandlerThreadInPort(newPort);
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            output.writeInt(newPort);
            output.close();
            client.close();
        }
    }

    private void launchHandlerThreadInPort(int newPort) throws IOException {
        ConnectionHandler conHandler = new ConnectionHandler(newPort);
        conHandler.start();
    }

    public static void main(String[] args) {
        try {
            new Server(SERVER_PORT).startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

