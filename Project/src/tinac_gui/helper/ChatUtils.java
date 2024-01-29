package tinac_gui.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatUtils {
    public static String getTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * Muestra hora de envío, mensaje y emisor en un formato claro
     *
     * @param messenger Quien envía el mensaje
     * @param message El mensaje
     * @return
     */
    public static String formatMessage(String messenger, String message) {
        return String.format("[%s] %s: %s", getTime(), messenger, message);
    }
}
