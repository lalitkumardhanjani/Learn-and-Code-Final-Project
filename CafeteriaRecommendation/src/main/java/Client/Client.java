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
                System.out.println("Welcome to Cafeteria Management System");
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
                } else if (choice == 2) {
                    out.println("logout");
                    String response = in.readLine();
                    if ("not logged in".equals(response)) {
                        System.out.println("You are not logged in. Please log in first.");
                    } else {
                        System.out.println(response);
                        break;
                    }
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
