package Server;

import Authentication.*;
import Database.*;

import java.io.*;
import java.net.*;
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

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int userId = -1; // Default value for unauthenticated users

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                handleClient(clientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleClient(Socket clientSocket) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("logout".equals(inputLine)) {
                        if (activeUsers.get(clientSocket) == null) {
                            out.println("not logged in");
                        } else {
                            Database database = new SqlServerDatabase();
                            database.LogLogoutAttempt(userId, true);
                            out.println("Logged out.");
                            activeUsers.remove(clientSocket);
                            userId = -1;
                        }
                    } else {
                        String[] parts = inputLine.split(":");
                        if (parts.length == 2) {
                            int userId = Integer.parseInt(parts[0]);
                            String password = parts[1];

                            Authentication auth = new UserAuthByCredentials(userId, password, new SqlServerDatabase());
                            if (auth.login()) {
                                out.println("Login successful");
                                this.userId = userId;
                                activeUsers.put(clientSocket, userId);
                            } else {
                                out.println("Invalid credentials");
                            }
                        } else {
                            out.println("Invalid input.");
                        }
                    }
                }
            } catch (SocketException e) {
                System.err.println("Client disconnected unexpectedly: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Client disconnected from port " + clientSocket.getPort());
            }
        }
    }
}
