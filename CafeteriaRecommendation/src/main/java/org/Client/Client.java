package org.Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.Constant.Constant;

public class Client {


    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        try (Socket socket = new Socket(Constant.SERVER_ADDRESS, Constant.CLIENT_PORT);
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
            System.err.println("Unknown host: " + Constant.SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
