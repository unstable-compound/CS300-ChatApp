package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private final ChatClient client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login: ");
    JButton registerButton = new JButton("Register");
    //JDialog dialog = new JDialog();


    public LoginWindow() {
        super("Login: ");

        this.client = new ChatClient("localhost", 8801);
        client.connect();


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(loginField);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        //panel.add(dialog);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });

        getContentPane().add(panel, BorderLayout.CENTER);

        pack();

        setVisible(true);

    }

    private void doRegister() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if(client.register(login, password)) {

                JOptionPane.showMessageDialog(this, "Successfully Registered.\nYou can login now.");
            }else //tell user of the error
            {
                JOptionPane.showMessageDialog(this, "Error.\n Try a different username.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {
                //pull up user list menu
                UserListPane userListPane = new UserListPane(client, login);
                JFrame frame = new JFrame("User List");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);

                //
                frame.add(new JLabel("Logged In As: " + login), BorderLayout.NORTH);
                //


                frame.getContentPane().add(userListPane, BorderLayout.CENTER);
                frame.setVisible(true);
                setVisible(false);
            } else {
                //show error message
                JOptionPane.showMessageDialog(this, "Invalid login/password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);


    }
}
