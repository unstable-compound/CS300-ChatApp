package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener {

    private final ChatClient client;
    private JList<String> userListUI;
    private String login;
    private DefaultListModel<String> userListModel;
    private JButton logoutButton = new JButton("Logout");


    public UserListPane(ChatClient client, String login) {
        this.login = login;
        this.client = client;
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogout();
            }
        });

        userListUI.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() > 1){
                    String target_login = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, target_login);

                    JFrame f = new JFrame("Logged In As: " + login + "\tMessaging user: " + target_login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500,500);
                    f.getContentPane().add(messagePane,BorderLayout.CENTER);
                    //show the component!
                    f.setVisible(true);
                }
            }
        });


    }

    private void doLogout() {
        try {
            setVisible(false);
            client.logoff();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public static void main(String [] args){
        ChatClient client = new ChatClient("localhost", 8801);

        UserListPane userListPane = new UserListPane(client);
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);


        if(client.connect())
        {
            //test login
            try {
                client.login("guest",  "guest");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
*/
    @Override
    public void online(String login) {
        userListModel.addElement(login);

    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
