package org.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.Constant.Constant;


public class Server {

    public static void main(String[] args) {
        ConcurrentMap<Socket, Integer> activeUsers = new ConcurrentHashMap<>();
        Server server = new Server();
        server.startServer(activeUsers);
    }

    public void startServer(ConcurrentMap<Socket, Integer> activeUsers) {
        try (ServerSocket serverSocket = new ServerSocket(Constant.SERVER_PORT)) {
            System.out.printf(Constant.SERVER_START_MESSAGE + "%n", serverSocket.getLocalPort());

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.printf(Constant.CLIENT_CONNECT_MESSAGE + "%n", clientSocket.getPort());

                    Thread clientThread = new Thread(new ClientHandler(clientSocket));
                    clientThread.start();
                } catch (IOException ioException) {
                    System.err.println("I/O error occurred while accepting client connection: " + ioException.getMessage());
                } catch (SecurityException securityException) {
                    System.err.println("Security exception occurred: " + securityException.getMessage());
                } catch (IllegalArgumentException illegalArgumentException) {
                    System.err.println("Illegal argument provided: " + illegalArgumentException.getMessage());
                } catch (RuntimeException runtimeException) {
                    System.err.println("Runtime exception occurred: " + runtimeException.getMessage());
                }
            }
        } catch (IOException ioException) {
            System.err.println("I/O error occurred while starting the server: " + ioException.getMessage());
        } catch (SecurityException securityException) {
            System.err.println("Security exception occurred: " + securityException.getMessage());
        } catch (RuntimeException runtimeException) {
            System.err.println("Runtime exception occurred: " + runtimeException.getMessage());
        }
    }
}
