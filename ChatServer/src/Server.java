import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final int serverPort;

    private ArrayList<ServerHelper> helperList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }
    public List<ServerHelper> getHelperList(){
        return helperList;
    }

    @Override
    public void run() {
         try {

            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("About to test accepting an incoming connection.");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted Connection");
                ServerHelper helper = new ServerHelper(this, clientSocket);
                helperList.add(helper);
                helper.start();
            }
        } catch (IOException e){// | //InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeHelper(ServerHelper serverHelper) {
        helperList.remove(serverHelper);
    }
}
