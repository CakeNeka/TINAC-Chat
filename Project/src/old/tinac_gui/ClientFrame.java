package old.tinac_gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientFrame extends JFrame {

    Client client;
    public ClientFrame(String title, Client client) throws HeadlessException {
        super(title);
        this.client = client;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,700);
        setLayout(new BorderLayout(10,10));

        initComponents();
        setVisible(true);
    }

    JTextArea textArea;
    JTextField userInputTf;

    private void initComponents() {
        JPanel sidebarPanel = new JPanel();
        JPanel textHolderPanel = new JPanel(new BorderLayout());
        JPanel textFieldPanel = new JPanel(new BorderLayout());

        sidebarPanel.setBackground(Color.pink);
        textHolderPanel.setBackground(Color.blue);
        textFieldPanel.setBackground(Color.CYAN);

        sidebarPanel.setPreferredSize(new Dimension(60,100));
        textHolderPanel.setPreferredSize(new Dimension(100,100));
        textFieldPanel.setPreferredSize(new Dimension(100,60));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(300, 300));
        textArea.setMargin(new Insets(10,10,10,10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        textHolderPanel.add(scrollPane, BorderLayout.CENTER);

        JButton submitBtn = new JButton();
        submitBtn.setText("Enviar!");
        submitBtn.setPreferredSize(new Dimension(130, 20));
        submitBtn.setMargin(new Insets(30,30,30,30));
        submitBtn.addActionListener(e -> sendMessage());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                if (e.getKeyCode() == 1);
            }
        });

        userInputTf = new JTextField();
        textFieldPanel.add(userInputTf, BorderLayout.CENTER);
        textFieldPanel.add(submitBtn, BorderLayout.EAST);

        add(textHolderPanel,BorderLayout.CENTER);
        add(textFieldPanel, BorderLayout.PAGE_END);
        add(sidebarPanel, BorderLayout.EAST);
    }

    void receiveMessage(String message) {
        textArea.append(message+'\n');
    }

    // Called on 'send' button pressed
    void sendMessage(){
        client.sendMessage(userInputTf.getText());
        userInputTf.setText("");
    }

    static void launchWindow(ClientFrame frame) {
    }

}
