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
    String COMMAND_HELP = "/help"; // Ayuda
    String COMMAND_QUIT = "/quit"; // Sale de la aplicación
    String COMMAND_NICK = "/nick"; // Cambia o muestra nick de cliente
    String COMMAND_ROOM = "/room"; // Cambia o muestra la sala actual
    String COMMAND_CONNECTED_USERS = "/users"; // Usuarios activos
    String COMMAND_ACTIVE_ROOMS = "/rooms"; // Salas activas
    String COMMAND_NET_INFO = "/netinfo"; // Información sobre la conexión
    String COMMAND_SECRET = "/mcr"; // créditos
    String COMMAND_BACKGROUND_CHANGE = "/bg"; // Cambia fondo

    /* Messages */

    String WELCOME_MESSAGE = "Bienvenida al chat más super divertido 🖤";
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
    String CREDITS = """
            Chat desarrollado con mucho amor por Martina Victoria (✿◡‿◡)
            """;
}
