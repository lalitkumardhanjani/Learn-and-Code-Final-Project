package org.Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final int PORT = 1234;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server on port " + socket.getLocalPort());

            UserAuthenticationHandler authHandler = new UserAuthenticationHandler(scanner, in, out);
            while (authHandler.handleAuthentication());

            System.out.println("Disconnected from server on port " + socket.getLocalPort());

        } catch (ConnectException e) {
            System.err.println("Connection refused. Make sure the server is running.");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
