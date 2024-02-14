package chat;

import helper.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LoginDialog extends JDialog {

    Client parent;
    public LoginDialog(Client parent) {
        super(parent);
        this.parent = parent;
        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/logo40x40.png"));
            JLabel logoLabel = new JLabel();
            logoLabel.setIcon(new ImageIcon(image));
            logoLabel.setBounds(143,10,40,40);
            panel.add(logoLabel);
        } catch (Exception e) {
            Logger.logClientError(e.getMessage());
        }

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(20,60,80,25);
        panel.add(userLabel);

        JTextField userField = new JTextField(20);
        userField.setText(String.valueOf(parent.getPort()));
        userField.setBounds(110,60,165,25);
        panel.add(userField);

        JLabel roomLabel = new JLabel("Sala");
        roomLabel.setBounds(20,90,80,25);
        panel.add(roomLabel);

        JTextField roomField = new JTextField(20);
        roomField.setText("0");
        roomField.setBounds(110,90,165,25);
        panel.add(roomField);

        JButton btn = new JButton("Unirse a la fiesta");
        btn.addActionListener(e -> sendUserInfo(userField, roomField) );
        btn.setBounds(40, 130, 220, 25);
        panel.add(btn);

        setSize(new Dimension(315,230));
        setTitle("login! :)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(parent);
        setModal(true);
        setResizable(false);
    }

    private void sendUserInfo(JTextField userField, JTextField roomField) {
        String user = userField.getText();
        String stringRoom = roomField.getText();
        if (validInput(user, stringRoom)) {
            parent.setInitialRoom(Integer.parseInt(stringRoom));
            parent.setInitialUsername(user);
            this.dispose();
        }
    }

    private boolean validInput(String user, String stringRoom) {
        try {
            Integer.parseInt(stringRoom);
        } catch (NumberFormatException e) {
            return false;
        }
        return !user.isEmpty();
    }
}