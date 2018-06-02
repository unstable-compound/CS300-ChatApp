
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {
        int port = 8801;
        try {

            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("About to test accepting an incoming connection.");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted Connection");
                ServerHelper helper = new ServerHelper(clientSocket);
                helper.start();
            }
        } catch (IOException e){// | //InterruptedException e) {
            e.printStackTrace();
        }
    }
}
