package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Connected to server on port " + socket.getLocalPort());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Login");
                System.out.println("2. Logout");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.print("Enter userId: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    out.println(userId + ":" + password);

                    String response = in.readLine();
                    System.out.println(response);
                    if ("Login successful".equals(response)) {
                        // Continue processing after successful login
                    }
                } else if (choice == 2) {
                    out.println("logout");
                    System.out.println("Logged out.");
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }

            in.close();
            out.close();
            socket.close();
            System.out.println("Disconnected from server on port " + socket.getLocalPort());
        } catch (ConnectException e) {
            System.err.println("Connection refused. Make sure the server is running.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
