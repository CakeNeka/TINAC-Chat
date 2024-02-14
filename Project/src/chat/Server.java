package chat;

import helper.ChatConstants;
import helper.Logger;

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

    public static int getNextAvailablePort(int minPort, int maxPort ) {
        for (int i = minPort; i < maxPort; i++) {
            if (available(i)) return i;
        }
        return -1;
    }

    /**
     * Tries to create a ServerSocket on a given port. If that operation
     * throws an exception, this method returns false. If the ServerSocket
     * is created successfully, it returns true.
     * @param port
     * @return
     */
    private static boolean available(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            // code in this block is always executed
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) { }
            }
        }
        return false;
    }

    private void startListening() throws IOException {
        while (listening) {
            client = serverSocket.accept();
            int newPort = getNextAvailablePort(MIN_PORT,MAX_PORT);
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
            Logger.logServerError(e.getMessage());
        }
    }
}

