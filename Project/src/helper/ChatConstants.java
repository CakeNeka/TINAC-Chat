package helper;

/**
 * Contiene constantes
 * Todos los campos de una interfaz son por defecto static final
 */
public interface ChatConstants {
    /* Connection parameters */
    String SERVER_IP = "127.0.0.1";
    int SERVER_PORT = 60000;
    int MIN_PORT = 60000;
    int MAX_PORT = 70000;

    /* Commands */
    String COMMAND_HELP = "/help";
    String COMMAND_QUIT = "/quit";
    String COMMAND_NICK = "/nick";
    String COMMAND_ROOM = "/room"; // no arg -> shows current room. arg -> changes room
    String COMMAND_CONNECTED_USERS = "/users";
    String COMMAND_ACTIVE_ROOMS = "/rooms";
    String COMMAND_NET_INFO = "/netinfo";
    /* Secret commands (client side) */
    String COMMAND_SECRET = "/mcr"; // does something unexpected
    String COMMAND_BACKGROUND_CHANGE = "/bg"; // Changes background

    /* Messages */

    String WELCOME_MESSAGE = "Bienvenide al chat más diver 🖤";
    String HELP_MESSAGE = """
            
            Comandos:
            /help Muestra los comandos
            /quit Sale del chat
            /nick Muestra el nick actual/cambia el nick
            /room Muestra sala actual/cambia de sala
            /users Muestra los usuarios conectados
            /rooms Muestra las salas activas
            /netinfo Muestra información sobre la conexión
            """;
}
