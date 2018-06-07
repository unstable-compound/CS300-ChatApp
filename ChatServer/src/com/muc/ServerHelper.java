package com.muc;

import java.io.*;
import java.net.Socket;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;

public class ServerHelper extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private AccountManager accountManager;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    //private com.muc.AccountManager accountManager = new com.muc.AccountManager();

    public String getLogin() {
        return login;
    }

    private String login = null;

    public ServerHelper(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        accountManager = null;
    }

    private void clientSocketHandler() throws IOException, InterruptedException{
        this.outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine;
        while((currentLine = reader.readLine()) != null)
        {
            String [] words = StringUtils.split(currentLine);//an array of the characters that were separated by white space.
            if(words !=null &&words.length >0) {
                String clientCommand = words[0];//the cmd will be the first thing entered.
                if (clientCommand.equalsIgnoreCase("logoff") || clientCommand.equalsIgnoreCase("quit")) {
                    handleLogoff();
                    break;
                }
                else if(clientCommand.equalsIgnoreCase("login")) {
                    handleLogin(outputStream, words);
                }
                else if(clientCommand.equalsIgnoreCase("all"))//message all
                {
                    String [] messageWords = StringUtils.split(currentLine, null, 2);
                    //words[0] = all, words[1] = messagebody
                    handleGroupMessage(messageWords);
                }
                else if(clientCommand.equalsIgnoreCase("msg")){
                    //instead of seperating all by whitespace, only seperate the first and second whitespace.
                    //That way, you have words[0] = 'command', words[1] = 'target_user', and words[2] = 'msg_body'.
                    String [] messageWords = StringUtils.split(currentLine, null, 3);
                    handleMessage(messageWords);
                }
                else if(clientCommand.equalsIgnoreCase("join"))
                {
                    handleJoin(words);
                }
                else if(clientCommand.equalsIgnoreCase("reg")){
                    //registration only allows 3 fields: the command: "reg"
                    //the username, and the password. More than three fields is an invalid registration.
                    if(words.length == 3)
                    {
                        String username = words[1];
                        String password = words[2];
                        handleRegister(username, password);//need to verify this.
                    }
                    else {
                        outputStream.write("Error registering\n".getBytes());
                    }
                }
                else if(clientCommand.equalsIgnoreCase("leave")){
                    handleLeave(words);
                }
                else
                {
                    String message = "The command: '" + clientCommand + "' is an unknown command.\n";
                    outputStream.write(message.getBytes());
                }

            }
        }

        clientSocket.close();
    }

    private void handleRegister(String username, String password) throws IOException {
        accountManager = server.getAccountManager();
        if(!accountManager.addAccount(username,password))
        {
            outputStream.write(("Error" + "\n").getBytes());
            System.out.println("Error registering connection with attempted username: " + username);
        }
        else//update servers registered users
        {
            server.updateManager(accountManager);
            outputStream.write("success\n".getBytes());
            System.out.println("User: " + username + " added to the registry.");
        }

    }

    private void handleGroupMessage(String[] messagewords) throws IOException {
        List<ServerHelper> helperList = server.getHelperList();
        String sendTo ="Group Chat";
        String body = new String();
        StringBuilder bodybuilder = new StringBuilder(body);
        for (int i = 1; i< messagewords.length; ++i){
            bodybuilder.append(messagewords[i] + " ");
        }
        body = bodybuilder.toString();


        for (ServerHelper helper: helperList) {
            String outMsg = sendTo + " " + login + "  " + body + "\n";
            helper.send(outMsg);
        }
    }

    private void handleLeave(String[] words) {
        if(words.length > 1)
        {
            String topic = words[1];
            topicSet.remove(topic);//doesnt actually 'leave' the topic, just removes it.
        }

    }

    public boolean isMemberOfTopicSet(String topic)
    {
        return topicSet.contains(topic);
    }
    private void handleJoin(String[] words) {
        if(words.length > 1)
        {
            String topic = words[1];
            topicSet.add(topic);

        }
    }


    //format_1: "msg" "login" body.....
    //format_2: "msg" "#topic" body...
    private void handleMessage(String[] words) throws IOException {
        String sendTo = words[1];
        String body = words[2];

        boolean isTopic = sendTo.charAt(0) == '#';


        List<ServerHelper> helperList = server.getHelperList();
        for(ServerHelper helper: helperList){
            if(isTopic){
                if(helper.isMemberOfTopicSet(sendTo)){
                    String outMsg = "msg " + sendTo + " " + login + "  " + body + "\n";
                    helper.send(outMsg);
                }

            }else{
                if(sendTo.equalsIgnoreCase(helper.getLogin())){
                    String outMsg = "msg " + login + " " + body + "\n";
                    helper.send(outMsg);
                }
            }
        }

    }

    private void handleLogoff() throws IOException {
        server.removeHelper(this);

        List<ServerHelper> helperList = server.getHelperList();
        //send other online users current users status.
        String onlineMessage = "Offline " + login + "\n";
        for(ServerHelper helper: helperList){
            if(!login.equals(helper.getLogin())) {
                helper.send(onlineMessage);
            }
        }
        clientSocket.close();
    }


    private void handleLogin(OutputStream outputStream, String[] words) throws IOException {

        if(words.length == 3) {
            accountManager = server.getAccountManager();
            String new_login = words[1];
            String password = words[2];
            //if((new_login.equals("guest") && password.equals("guest")) || new_login.equals("curtis") && password.equals("curtis"))
            if(accountManager.isValidLogin(new_login, password))
            {
                String msg = "You are logged in.\n";
                outputStream.write(msg.getBytes());
                this.login = new_login;
                System.out.println("User logged in successfully: " + new_login);
                //

                List<ServerHelper> helperList = server.getHelperList();

                //send current user all other online logins
                for(ServerHelper helper: helperList){
                    if(helper.getLogin() != null) {
                        if(!login.equals(helper.getLogin())) {
                            String onlineMessage = "Online " + helper.getLogin() + "\n";
                            send(onlineMessage);
                        }
                    }

                }
                //send other online users current users status.
                String onlineMessage = "Online " + login + "\n";
                for(ServerHelper helper: helperList){
                    if(!login.equals(helper.getLogin())) {
                        helper.send(onlineMessage);
                    }
                }
            }
            else{
                String msg = "Error logging in.\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for: " + login);
            }
        }
    }

    private void send(String message) throws IOException {
        if(login != null)
            outputStream.write(message.getBytes());

    }


    @Override
    public void run() {
        try {
            clientSocketHandler();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
