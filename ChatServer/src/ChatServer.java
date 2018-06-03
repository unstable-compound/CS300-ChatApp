
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {
        int port = 8801;
        Server server = new Server(port);
        server.start();

    }
}
