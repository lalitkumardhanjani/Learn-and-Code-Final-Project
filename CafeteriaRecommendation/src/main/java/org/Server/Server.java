package org.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 1234;
    private static final String SERVER_START_MESSAGE = "Server started on port %d. Waiting for clients...";
    private static final String CLIENT_CONNECT_MESSAGE = "Client connected from port %d";

    public static void main(String[] args) {
        Map<Socket, Integer> activeUsers = new HashMap<>();
        Server server = new Server();
        server.startServer(activeUsers);
    }

    public void startServer(Map<Socket, Integer> activeUsers) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(String.format(SERVER_START_MESSAGE, serverSocket.getLocalPort()));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(String.format(CLIENT_CONNECT_MESSAGE, clientSocket.getPort()));

                Thread clientThread = new Thread(new ClientHandler(clientSocket, activeUsers));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
