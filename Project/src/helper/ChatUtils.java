package helper;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatUtils {
    /**
     * Devuelve la hora actual en formato String
     * @return La hora actual
     */
    public static String getTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * Muestra hora de envío, mensaje y emisor en un formato claro
     *
     * @param sender Quien envía el mensaje
     * @param message El mensaje
     * @return
     */
    public static String formatMessage(String sender, String message) {
        return String.format("[%s] %s: %s", getTime(), sender, message);
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
}
