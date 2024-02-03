package helper;

/**
 * Contiene constantes
 * Todos los campos de una interfaz son por defecto static final
 */
public interface ChatConstants {
    String SERVER_IP = "127.0.0.1";
    int SERVER_PORT = 60000;
    int MIN_PORT = 60000;
    int MAX_PORT = 70000;

    String COMMAND_HELP = "/help";
    String COMMAND_QUIT = "/quit";
    String COMMAND_NICK = "/nick";
    String COMMAND_ROOM = "/room"; // no arg -> shows current room. arg -> changes room
    String COMMAND_CONNECTED_USERS = "/all";
    String COMMAND_ACTIVE_ROOMS = "/rooms";
    String COMMAND_SECRET = "/mcr"; // does something unexpected


    String WELCOME_MESSAGE = "Bienvenide al chat más diver 🖤";
}
