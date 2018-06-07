package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GroupChatPane extends JPanel implements MessageListener {
    private final ChatClient client;
    private final String login;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messagelist = new JList<>(listModel);
    private JTextField inputField = new JTextField();

    public GroupChatPane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messagelist), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                try {
                    client.messageAll(message);
                    listModel.addElement("You: " + message);
                    inputField.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onMessage(String fromLogin, String message) {
        if(!login.equals(fromLogin))
        {
            String line = fromLogin + ": " + message;
            listModel.addElement(line);
        }

    }
}

