/* Modificaciones sobre la primera versión
    - Se envían mensajes en lugar de bytes.
    - Se informa de la ip del cliente
    - Se aceptan multiples conexiones y se identifican los mensajes.

Queda Pendiente:
    x Atender conexiones en un puerto, gestionar el servicio en otro.
    x El servidor guardará el listado de las conexiones activas.
    x Los mensajes recibidos se reenviarán a todos los clientes.
    x Los clientes podrán realizar las siguientes acciones:
            * Enviar mensajes.
            * Conectarse.
            * Desconectarse.
            - Cambiar su nombre de usuario.
            - Consultar usuarios conectados.
*/
package old.tinac_gui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static helper.ChatUtils.*;


public class Server implements ConnectionParams {

    private List<String> messages = new LinkedList<>();

    /**
     * Este entero incrementa en uno con cada conexión establecida.
     * Representa el puerto que se asignará al próximo cliente
     * que se conecte.
     */
    private int nextPort; // TODO: Seleccionar puertos libres
    private ServerSocket serverSocket; // Socket que atiende a conexiones
    private Socket socket;
    private List<ConnectionHandler> connections = new ArrayList<>();


    public Server(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        nextPort = puerto + 1;
    }

    public void start() throws IOException {
        // 1. Conectar con nuevo cliente
        socket = serverSocket.accept();

        // 2. Iniciar servidor en otro puerto
        ConnectionHandler connection = new ConnectionHandler(nextPort, this);
        connection.start(); // Lanza el hilo
        connections.add(connection);

        // 3. Enviar al cliente el nuevo puerto
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(nextPort);
        // Aquí el cliente se desconecta y reconecta con el nuevo puerto

        // 4. Cerrar conexión con cliente y establecer siguiente puerto
        out.close();
        socket.close();
        nextPort++;
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public void broadcastMessage(String messenger, String msg, ConnectionHandler skip) {
        addToList(formatMessage(messenger, msg));
        for (ConnectionHandler ch : connections) {
            // if (ch == skip) continue;
            ch.sendMessageToClient(messenger,msg);
        }
    }

    public static void main(String[] args) {
        try {
            // Creamos un servidor que estará siempre a la espera de conexiones
            Server server = new Server(DEFAULT_PORT);
            while (true) { // TODO: Cambiar while true >:(
                server.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Operaciones atómicas.

    /**
     * Escribe el mensaje en el archivo de log
     * @param message
     */
    public synchronized void saveToLogFile(String message) {
        try {
            FileWriter fw = new FileWriter("chat.txt", true);
            fw.write("\r\n" + message);
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    /**
     * Solo nos interesa guardar los mensajes que reciben todos los clientes
     * Cuando se conecte un nuevo cliente, le enviamos todos los mensajes
     * @param message
     */
    public synchronized void addToList(String message) {
        messages.add(message);
    }
}