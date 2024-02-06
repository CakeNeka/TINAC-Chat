package helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * logs messages into a text file
 */
public class ServerLogger {

    private static final String BASE_PATH = "files/logs";

    public static void logMessage(String sender, String message, int room) {
        try {
            Path path = getPath(room);
            Files.createDirectories(path.getParent());
            Files.writeString(path,ChatUtils.formatMessage(sender,message)+'\n', StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPath(int room){
        return Path.of(String.format("%s/%s_ROOM%d.txt",BASE_PATH, ChatUtils.getDate(), room));
    }

    public static void logError(String msg) {
        ChatUtils.getTime();
    }

}

