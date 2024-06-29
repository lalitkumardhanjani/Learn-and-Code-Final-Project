package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<Socket, Integer> activeUsers = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from port " + clientSocket.getPort());

                Thread t = new Thread(new ClientHandler(clientSocket, activeUsers));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
