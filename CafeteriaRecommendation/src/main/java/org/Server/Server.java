package org.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private static final int PORT = 1234;
    private static final String SERVER_START_MESSAGE = "Server started on port %d. Waiting for clients...";
    private static final String CLIENT_CONNECT_MESSAGE = "Client connected from port %d";

    public static void main(String[] args) {
        ConcurrentMap<Socket, Integer> activeUsers = new ConcurrentHashMap<>();
        Server server = new Server();
        server.startServer(activeUsers);
    }

    public void startServer(ConcurrentMap<Socket, Integer> activeUsers) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(String.format(SERVER_START_MESSAGE, serverSocket.getLocalPort()));

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(String.format(CLIENT_CONNECT_MESSAGE, clientSocket.getPort()));

                    Thread clientThread = new Thread(new ClientHandler(clientSocket, activeUsers));
                    clientThread.start();
                } catch (IOException e) {
                    System.err.println("I/O error occurred while accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                } catch (SecurityException e) {
                    System.err.println("Security exception occurred: " + e.getMessage());
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.err.println("Illegal argument provided: " + e.getMessage());
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    System.err.println("Runtime exception occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error occurred while starting the server: " + e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {
            System.err.println("Security exception occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Runtime exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
