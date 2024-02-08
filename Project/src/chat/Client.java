package chat;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import helper.ChatConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ChatConstants {

    // UI components
    private JTextArea chatArea;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private TitledBorder titledBorder;

    // UI Data
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
                    if (msg.startsWith("!room"))
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
        if (!text.isEmpty()) {
            output.println(text); // Send text to server
            inputField.setText(""); // Clear input field
            if (text.equals(COMMAND_QUIT)) {
                active = false;
            }
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
        scrollPane = new JScrollPane(chatArea);

        // Autoscroll
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));

        scrollPane.setFocusable(false);
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
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
        ImagePanel imagePanel = null;
        try {
            BufferedImage image = ImageIO.read(new File("files/resources/background.gif"));
            imagePanel = new ImagePanel(image);
            imagePanel.setFocusable(false);
            imagePanel.setLayout(new BorderLayout());
            imagePanel.add(scrollPane, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            setLayout(new BorderLayout());
            if (imagePanel != null) {
                add(imagePanel, BorderLayout.CENTER);
            } else {
                add(scrollPane, BorderLayout.CENTER);
            }
        }

        JPanel inputPanel = new JPanel();
        titledBorder = BorderFactory.createTitledBorder("Usuario: | Sala: ");
        inputPanel.setBorder(titledBorder);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(500, 50));
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

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setResizable(false);
        setTitle("Star-crossed chat (>'-'<)");
        setLocationRelativeTo(null);
        FlatDarculaLaf.updateUI();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        FlatDarculaLaf.setPreferredFontFamily("Comic Sans ms");
        SwingUtilities.invokeLater(() -> {
            try {
                new Client(SERVER_IP, SERVER_PORT).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class ImagePanel extends JPanel {
        private Image backgroundImage;
        private int imageHeight;
        private int imageWidth;

        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            imageHeight = backgroundImage.getHeight(this);
            imageWidth = backgroundImage.getWidth(this);

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

            // g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
