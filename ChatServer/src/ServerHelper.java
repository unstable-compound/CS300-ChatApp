import java.io.*;
import java.net.Socket;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class ServerHelper extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private OutputStream outputStream;

    public String getLogin() {
        return login;
    }

    private String login = null;

    public ServerHelper(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
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
                else if(clientCommand.equalsIgnoreCase("login"))
                    handleLogin(outputStream, words);
                else
                {
                    String message = "The command: '" + clientCommand + "' is an unknown command.\n";
                    outputStream.write(message.getBytes());
                }

            }
        }

        clientSocket.close();
    }

    private void handleLogoff() throws IOException {

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
            String new_login = words[1];
            String password = words[2];
            if(new_login.equalsIgnoreCase("guest") && password.equalsIgnoreCase("guest"))
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
                            String onlineMessage = "Online: " + helper.getLogin() + "\n";
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
