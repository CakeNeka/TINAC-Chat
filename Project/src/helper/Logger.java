package helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;

/**
 * logs messages into a text file
 */
public class Logger {

    public static final String CHAT_LOG_PATH = "files/chat_logs";
    public static final String secretKey = "claveSuperSecreta";
    private static final String SERVER_ERROR_LOG_PATH = "files/error_logs/server_error.log";
    private static final String CLIENT_ERROR_LOG_PATH = "files/error_logs/client_error.log";

    public static void logMessage(String sender, String message, int room) {
        try {
            String encryptedMessage = EncryptionHelper.encriptar(ChatUtils.formatMessage(sender,message), secretKey);
            Path path = getPath(room);
            Files.createDirectories(path.getParent());
            Files.writeString(path, encryptedMessage + '\n', StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Unable to store message");
            logServerError(e.getMessage());
        } catch (GeneralSecurityException e) {
            System.err.println("Unable to encrypt message");
            logServerError(e.getMessage());
        }
    }

    private static Path getPath(int room){
        return Path.of(String.format("%s/%s_ROOM%d.txt", CHAT_LOG_PATH, ChatUtils.getDate(), room));
    }

    public static void logServerError(String msg) {
        logError(msg, SERVER_ERROR_LOG_PATH);
    }
    public static void logClientError(String msg) {
        logError(msg, CLIENT_ERROR_LOG_PATH);
    }
    private static void logError(String msg, String logPath) {
        String formattedError = String.format("%s [%s]: %s\n", ChatUtils.getDate(), ChatUtils.getTime(),msg);
        Path path = Path.of(logPath);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, formattedError+ '\n', StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Unable to log error (" + e.getMessage() + ")");
        }
    }

}

