import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerHelper extends Thread{

    private final Socket clientSocket;

    public ServerHelper(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void clientSocketHandler() throws IOException, InterruptedException{
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine;
        while((currentLine = reader.readLine()) != null)
        {
            if(currentLine.equalsIgnoreCase("quit"))
                break;
            String message = "You typed: " + currentLine + "\n";
            outputStream.write(message.getBytes());

        }

        clientSocket.close();
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
