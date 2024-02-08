package chat;

import com.formdev.flatlaf.*;
import helper.ChatConstants;

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

public class Client extends JFrame implements ChatConstants {

    // UI components
    private JTextArea chatArea;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private TitledBorder titledBorder;
    private ImagePanel imagePanel;

    // UI Data
    List<BufferedImage> backgrounds;
    private String clientNick;
    private String clientRoom;


    // Connection objects
    private Socket server;
    private PrintWriter output;
    private BufferedReader input;
    private String ip;
    private int port;
    private Thread serverListener;
    private boolean active = true;

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
    }

    private void connect() throws IOException {
        server = new Socket(ip, port);
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        output = new PrintWriter(server.getOutputStream(), true);
    }

    private void close() throws IOException {
        input.close();
        output.close();
        server.close();
        dispose();
    }

    private void start() throws IOException {
        connect();
        DataInputStream input = new DataInputStream(server.getInputStream());
        port = input.readInt();
        close();
        connect();
        initComponents();
        startListenerThread();
    }

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
                throw new RuntimeException(e);
            }
        });
        serverListener.start();
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        boolean isClientCommand = handleClientCommands(text);
        if (!isClientCommand && !text.isEmpty()) {
            output.println(text); // Send text to server
        }
        inputField.setText(""); // Clear input field
        active = !text.equals(COMMAND_QUIT);
    }

    private boolean handleClientCommands(String text) {
        if (text.equals(COMMAND_BACKGROUND_CHANGE)) {
            updateBackground();
            return true;
        }
        return false;
    }

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

    public void initComponents() {

        // Initialize components
        chatArea = new JTextArea(10, 30);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(false);
        chatArea.setFocusable(false);
        chatArea.setOpaque(false);
        chatArea.setBackground(new Color(100,100,100,100));
        scrollPane = new JScrollPane(chatArea);
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

        // Set background image
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


        /*
        // Set a custom font for the title
        Font titleFont = new Font("Comic Sans ms", Font.BOLD, 20);
        System.out.println(titleFont);
        inputPanel.setFont(titleFont);
        getRootPane().setFont(titleFont);
        */

        // Al cerrar la ventana envía al servidor el comando "/quit"
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                output.println(COMMAND_QUIT);
            }
        });

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setMinimumSize(new Dimension(300,300));
        // setResizable(false);
        setTitle("Star-crossed chat (>'-'<)");
        setLocationRelativeTo(null);
        setVisible(true);
    }

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

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(() -> {
            try {
                new Client(SERVER_IP, SERVER_PORT).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

class ImagePanel extends JPanel {
    private Image backgroundImage;
    private int imageHeight;
    private int imageWidth;

    public ImagePanel(Image backgroundImage) {
        setBackground(backgroundImage);
    }

    public void setBackground(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        imageHeight = backgroundImage.getHeight(this);
        imageWidth = backgroundImage.getWidth(this);
    }

    public BufferedImage getBackgroundImage() {
        return (BufferedImage) backgroundImage;
    }

    // Tiled background =)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        for (int x = 0; x <= panelWidth; x += imageWidth) {
            for (int y = 0; y <= panelHeight; y += imageHeight) {
                g.drawImage(backgroundImage, x, y, this);
            }
        }
    }
}
