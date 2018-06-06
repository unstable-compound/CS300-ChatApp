package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();

    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8801);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);

            }
        });
        client.addMessageListener(new MessageListener(){
            @Override
            public void onMessage(String fromLogin, String message) {
                System.out.println("You got a message from: " + fromLogin + "==> " + message);
            }
        });
        if(!client.connect()){
            System.err.println("Connection failed");
        }
        else{
            System.out.println("Connection Successful");
            if(client.login("guest", "guest")){
                System.out.println("Login successful");

                client.msg("curtis" , "Hello World!");
            }else{
                System.err.println("Login Failed");
            }
            //client.logoff();
        }
    }

    public void msg(String sendTo, String message) throws IOException {
        String command = "msg " + sendTo + " " + message + "\n";
        serverOut.write(command.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "logoff \n";
        serverOut.write(cmd.getBytes());

    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if(response.equalsIgnoreCase("you are logged in.")){
            startMessageReader();
            return true;
        }else {
            return false;
        }

    }

    private void startMessageReader() {
        Thread thread = new Thread(() -> readMessageLoop());
        thread.start();
        /*

            Thread thread = new Thread(){
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        thread.start();;
         */
    }

    private void readMessageLoop() {
        try {
            String currentLine;
            while ((currentLine = bufferedIn.readLine()) != null) {
                String[] words = StringUtils.split(currentLine);//an array of the characters that were separated by white space.
                if (words != null && words.length > 0) {
                    String clientCommand = words[0];//the cmd will be the first thing entered.
                    if("online".equalsIgnoreCase(clientCommand))
                    {
                        handleOnline(words);
                    } else if("offline".equalsIgnoreCase(clientCommand)){
                        handleOffline(words);
                    }
                    else if ("msg".equalsIgnoreCase(clientCommand)){
                        //instead of seperating all by whitespace, only seperate the first and second whitespace.
                        //That way, you have words[0] = 'command', words[1] = 'target_user', and words[2] = 'msg_body'.
                        String [] messageWords = StringUtils.split(currentLine, null, 3);
                        handleMessage(messageWords);
                    }

                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] words) {
        String login = words[1];
        String message = words[2];

        for(MessageListener listener: messageListeners){
            listener.onMessage(login, message);
        }


    }

    private void handleOffline(String[] words) {
        String login = words[1];
        for(UserStatusListener listener: userStatusListeners){
            listener.offline(login);
        }
    }

    private void handleOnline(String[] words) {
        String login = words[1];
        for(UserStatusListener listener: userStatusListeners){
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            //if successful
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        //if unsuccessful
        return false;
    }
    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener){
        userStatusListeners.remove(listener);
    }
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}
