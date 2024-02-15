package chat;

import com.formdev.flatlaf.*;
import helper.ChatConstants;
import helper.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente. Intercambia información con el servidor, recoge entrada del usuario
 * y muestra la interfaz gráfica
 */
public class Client extends JFrame implements ChatConstants {

    // UI components
    private JTextArea chatArea;
    private JTextField inputField;
    private TitledBorder titledBorder;
    private ImagePanel imagePanel;

    // UI Data
    List<BufferedImage> backgrounds;
    private String clientNick;
    private String clientRoom;

    private String initialUsername;
    private int initialRoom;


    // Connection objects
    private Socket server;
    private PrintWriter output;
    private BufferedReader input;
    private final String ip;
    private int port;
    private Thread serverListener;
    private boolean active = true;

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Abre socket y flujos de entrada/salida
     */
    private void connect() throws IOException {
        server = new Socket(ip, port);
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        output = new PrintWriter(server.getOutputStream(), true);
    }

    /**
     * Cierra socket y flujos de entrada/salida
     */
    private void close() throws IOException {
        input.close();
        output.close();
        server.close();
        dispose();
    }

    /**
     * 1. Establece conexión con el servidor
     * 2. Recibe el nuevo puerto del servidor
     * 3. Cierra la conexión
     * 4. Vuelve a abrir la conexión en el puerto enviado por el servidor
     */
    public void start() throws IOException {
        connect(); // conecta con Server
        DataInputStream input = new DataInputStream(server.getInputStream());
        port = input.readInt();
        close();
        connect(); // conecta con ConnectionHandler
        initComponents(); // Construye la GUI
        login(); // muestra el diálogo de login
        startListenerThread(); // lanza el hilo encargado de recibir mensajes
        sendCredentials(); // envía las credenciales establecidas por el usuario en el diálogo de login
    }

    /**
     * Muestra el diálogo modal de inicio de sesión y espera a que
     * el usuario envíe la información
     */
    private void login() {
        JDialog login = new LoginDialog(this);
        login.setVisible(true);
    }

    /**
     * Envía las credenciales elegidas en el diálogo {@link LoginDialog}
     * al servidor
     */
    private void sendCredentials() {
        output.println("/nick " + initialUsername);
        output.println("/room " + initialRoom);
    }

    public void setInitialUsername(String user) {
        initialUsername = user;
    }

    public void setInitialRoom(int room) {
        initialRoom = room;
    }

    /**
     * Lanza el hilo encargado de escuchar los mensajes del servidor
     * "!room" y "!nick" son mensajes especiales del servidor, informan al cliente
     * de su nuevo nick o sala a la que se ha cambiado.
     */
    private void startListenerThread() {
        serverListener = new Thread(() -> {
            try {
                while (active) {
                    String msg = input.readLine();
                    if (msg == null)
                        active = false;
                    else if (msg.startsWith("!room"))
                        setClientRoom(msg.split("=")[1]);
                    else if (msg.startsWith("!nick"))
                        setClientNick(msg.split("=")[1]);
                    else
                        chatArea.append(msg + System.lineSeparator());
                }
                close();
            } catch (IOException e) {
                Logger.logClientError(e.getMessage());
            }
        });
        serverListener.start();
    }

    /**
     * Envía un mensaje al servidor
     */
    private void sendMessage() {
        String text = inputField.getText().trim();
        boolean isClientCommand = handleClientCommands(text);
        if (!isClientCommand && !text.isEmpty()) {
            output.println(text); // Envía texto al servidor
        }
        inputField.setText(""); // Vacía el texto del inputField
        active = !text.equals(COMMAND_QUIT);
    }

    /**
     * Gestiona comandos del cliente.
     * El único comando del cliente es el de cambiar el fondo del chat
     */
    private boolean handleClientCommands(String text) {
        if (text.equals(COMMAND_BACKGROUND_CHANGE)) {
            updateBackground();
            return true;
        }
        return false;
    }

    /**
     * Cambia el fondo del chat
     */
    private void updateBackground() {
        if (backgrounds != null && !backgrounds.isEmpty()) {
            int nextIndex = backgrounds.indexOf(imagePanel.getBackgroundImage()) + 1;
            if (nextIndex == backgrounds.size()) {
                nextIndex = 0;
            }
            imagePanel.setBackground(backgrounds.get(nextIndex));
            repaint();
        }
    }

    private void setClientNick(String clientNick) {
        this.clientNick = clientNick;
        updateClientData();
    }

    private void setClientRoom(String clientRoom) {
        this.clientRoom = clientRoom;
        updateClientData();
    }

    private void updateClientData() {
        titledBorder.setTitle(String.format("<html><b>Usuario:</b> %s | <b>Sala:</b> %s</html>", clientNick, clientRoom));
        repaint();
    }

    /**
     * Todo el código para generar la ventana
     */
    public void initComponents() {
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(false);
        chatArea.setFocusable(false);
        chatArea.setOpaque(false);
        chatArea.setBackground(new Color(100,100,100,100));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBackground(new Color(100,100,100,100));

        // Autoscroll
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));

        scrollPane.setFocusable(false);
        Border border = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        scrollPane.setViewportBorder(border);

        inputField = new JTextField(30);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            sendMessage();
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMessage();
            }
        });

        // Establece imagen de fondo si es posible cargarla
        imagePanel = null;
        backgrounds = loadImages();
        setLayout(new BorderLayout());
        if (backgrounds!=null && !backgrounds.isEmpty()){
            BufferedImage image = backgrounds.get(0);
            imagePanel = new ImagePanel(image);
            imagePanel.setFocusable(false);
            imagePanel.setLayout(new BorderLayout());
            imagePanel.add(scrollPane, BorderLayout.CENTER);
            add(imagePanel, BorderLayout.CENTER);
        } else {
            add(scrollPane, BorderLayout.CENTER);
        }

        JPanel inputPanel = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Usuario: | Sala: ");
        inputPanel.setBorder(titledBorder);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(500, 60));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Al cerrar la ventana envía al servidor el comando "/quit"
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                output.println(COMMAND_QUIT);
            }
        });

        // Propiedades de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setMinimumSize(new Dimension(300,300));
        setTitle("Star-crossed chat (>'-'<)");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Carga las imágenes que pueden usarse como fondo
     */
    private List<BufferedImage> loadImages() {
        String imgDir = "res/imgs/";
        List<BufferedImage> images = new ArrayList<>();
        try {
            List<Path> imagePaths = Files.list(Path.of(imgDir)).toList();
            for (Path imagePath : imagePaths) {
                BufferedImage image = ImageIO.read(getClass().getResource("/imgs/" + imagePath.getFileName())); // Remember to mark your resources folder
                images.add(image);
            }
            return images;
        } catch (Exception e) {
            return null;
        }
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> {
            try {
                new Client(SERVER_IP, SERVER_PORT).start();
            } catch (IOException e) {
                Logger.logClientError(e.getMessage());
            }
        });
    }

}