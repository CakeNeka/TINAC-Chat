package chat;

import helper.ChatConstants;
import helper.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * El servidor principal que está constantemente a la espera de conexiones.
 * 1. Acepta conexión con cliente
 * 2. Busca un puerto disponible en el rango establecido en {@link ChatConstants}
 * 3. Lanza un nuevo hilo {@link ConnectionHandler} que abre un servidor en el puerto elegido
 * 4. Informa al cliente del nuevo puerto
 * 5. Cierra conexión con el cliente
 * 6. Vuelve a empezar
 */
public class Server implements ChatConstants {
    private ServerSocket serverSocket;
    private Socket client;
    private boolean listening = true;


    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Devuelve el primer puerto disponible en el rango especificado, -1 si
     * no hay ningún puerto libre en ese rango
     */
    public int getNextAvailablePort(int minPort, int maxPort ) {
        for (int i = minPort; i < maxPort; i++) {
            if (available(i)) return i;
        }
        return -1;
    }

    /**
     * Intenta crear un ServerSocket en el puerto pasado por parámetro.
     * Si la operación falla, devuelve false (el puerto está ocupado)
     * Si la operación tiene éxito, devuelve true (el puerto está libre)
     * @param port Puerto a comprobar
     */
    private boolean available(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            // code in this block is always executed
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) { }
            }
        }
        return false;
    }

    /**
     * Bucle principal, espera conexiones y lanza hilos.
     */
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

