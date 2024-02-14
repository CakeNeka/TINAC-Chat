package chat;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        new LoginDialog();
    }
    public LoginDialog() {
        setLayout(null);

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(20,20,80,25);
        add(userLabel);

        JTextField userField = new JTextField(20);
        userField.setBounds(110,20,165,25);
        add(userField);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(20,50,80,25);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBounds(110,50,165,25);
        add(passwordField);

        JButton btn = new JButton("Iniciar sesión");
        btn.addActionListener(e -> {

        });
        btn.setBounds(30, 90, 110, 25);
        add(btn);

        JButton btn2 = new JButton("Registrarse");
        btn2.addActionListener(e -> {

        });
        btn2.setBounds(150, 90, 110, 25);
        add(btn2);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(315,200));
        setTitle("login! :)");
        setResizable(false);
        setVisible(true);
    }
}