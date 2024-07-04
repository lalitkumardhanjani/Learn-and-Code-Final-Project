package org.Server;

import org.Database.SqlServerDatabase;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Map<Socket, Integer> activeUsers;
    private int userId = -1;
    private AuthenticationHandler authenticationHandler;
    private RequestHandler requestHandler;

    public ClientHandler(Socket clientSocket, Map<Socket, Integer> activeUsers) {
        this.clientSocket = clientSocket;
        this.activeUsers = activeUsers;
        initializeHandlers();
    }

    private void initializeHandlers() {
        SqlServerDatabase sqlDatabase = new SqlServerDatabase();
        authenticationHandler = new AuthenticationHandler(sqlDatabase, activeUsers);
        requestHandler = new RequestHandler(sqlDatabase, activeUsers);
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    if (inputLine.equals("logout")) {
                        authenticationHandler.handleLogout(out, clientSocket);
                    } else if (inputLine.startsWith("login:")) {
                        authenticationHandler.handleLogin(out, inputLine.split(":"), clientSocket);
                    } else {
                        requestHandler.handleRequest(inputLine, in, out, clientSocket);
                    }
                } catch (Exception e) {
                    out.println("Error processing request: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            System.err.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error occurred while handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeClientSocket();
        }
    }

    private void closeClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Client disconnected from port " + clientSocket.getPort());
    }
}
