package com.muc;

public class ChatServer {
    public static void main(String[] args) {
        int port = 8801;
        Server server = new Server(port);
        server.start();

    }
}
