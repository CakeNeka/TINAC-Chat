package helper;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ChatUtils {
    /**
     * String con la hora actual
     * @return La hora actual
     */
    public static String getTime() {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * Devuelve la fecha en formato año mes día
     * @return
     */
    public static String getDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
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

}
