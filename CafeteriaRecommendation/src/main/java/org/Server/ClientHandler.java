package org.Server;

import org.Database.SqlServerDatabase;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket clientConnectionSocket;
    private final Map<Socket, Integer> activeUserMap;
    private AuthenticationHandler userAuthenticationHandler;
    private RequestHandler clientRequestHandler;

    public ClientHandler(Socket clientConnectionSocket, Map<Socket, Integer> activeUserMap) {
        this.clientConnectionSocket = clientConnectionSocket;
        this.activeUserMap = activeUserMap;
        initializeHandlers();
    }

    private void initializeHandlers() {
        SqlServerDatabase sqlServerDatabase = new SqlServerDatabase();
        userAuthenticationHandler = new AuthenticationHandler(sqlServerDatabase, activeUserMap);
        clientRequestHandler = new RequestHandler(sqlServerDatabase);
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (Exception unexpectedException) {
            System.err.println("Unexpected error occurred: " + unexpectedException.getMessage());
        }
    }

    private void handleClient() {
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientConnectionSocket.getInputStream()));
             PrintWriter outputWriter = new PrintWriter(clientConnectionSocket.getOutputStream(), true)) {

            String clientInputLine;
            while ((clientInputLine = inputReader.readLine()) != null) {
                try {
                    if (clientInputLine.equals("logout")) {
                        userAuthenticationHandler.handleLogout(outputWriter, clientConnectionSocket);
                    } else if (clientInputLine.startsWith("login:")) {
                        userAuthenticationHandler.handleLogin(outputWriter, clientInputLine.split(":"), clientConnectionSocket);
                    } else {
                        clientRequestHandler.handleRequest(clientInputLine, inputReader, outputWriter);
                    }
                } catch (Exception requestProcessingException) {
                    outputWriter.println("Error processing request: " + requestProcessingException.getMessage());
                }
            }
        } catch (SocketException clientSocketException) {
            System.err.println("Client disconnected unexpectedly: " + clientSocketException.getMessage());
        } catch (IOException clientHandlingIOException) {
            System.err.println("I/O error occurred while handling client: " + clientHandlingIOException.getMessage());
        } finally {
            closeClientSocket();
        }
    }

    private void closeClientSocket() {
        try {
            clientConnectionSocket.close();
        } catch (IOException clientSocketClosingIOException) {
            System.err.println("Error closing client socket: " + clientSocketClosingIOException.getMessage());
        }
        System.out.println("Client disconnected from port " + clientConnectionSocket.getPort());
    }
}
