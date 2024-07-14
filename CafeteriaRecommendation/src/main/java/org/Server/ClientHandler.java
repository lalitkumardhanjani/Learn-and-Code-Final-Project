package org.Server;

import org.Database.SqlServerDatabase;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket clientConnectionSocket;
    private AuthenticationHandler userAuthenticationHandler;
    private AdminRequestHandler adminRequestHandler;
    private ChefRequestHandler chefRequestHandler;
    private EmployeeRequestHandler employeeRequestHandler;

    public ClientHandler(Socket clientConnectionSocket) {
        this.clientConnectionSocket = clientConnectionSocket;
        initializeHandlers();
    }

    private void initializeHandlers() {
        SqlServerDatabase sqlServerDatabase = new SqlServerDatabase();
        userAuthenticationHandler = new AuthenticationHandler(sqlServerDatabase);
        adminRequestHandler = new AdminRequestHandler();
        chefRequestHandler = new ChefRequestHandler();
        employeeRequestHandler = new EmployeeRequestHandler();
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
                    if (clientInputLine.startsWith("auth")) {
                        userAuthenticationHandler.handleLogin(outputWriter, clientInputLine.split(":"), clientConnectionSocket);
                    } else if(clientInputLine.startsWith("admin")){
                        adminRequestHandler.handleRequest(clientInputLine, inputReader, outputWriter);
                    } else if(clientInputLine.startsWith("chef")) {
                        chefRequestHandler.handleRequest(clientInputLine, inputReader, outputWriter);
                    } else if(clientInputLine.startsWith("employee")) {
                        employeeRequestHandler.handleRequest(clientInputLine, inputReader, outputWriter);
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
