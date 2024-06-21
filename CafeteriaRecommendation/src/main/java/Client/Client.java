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

                    if (response.startsWith("Login successful")) {
                        String role = response.split(": ")[1];
                        if ("admin".equalsIgnoreCase(role)) {
                            showAdminMenu(scanner, in, out);
                        } else {
                            showUserMenu(scanner, in, out);
                        }
                    }
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

    private static void showAdminMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Create Menu Item");
            System.out.println("2. View Menu Items");
            System.out.println("3. Update Menu Item");
            System.out.println("4. Delete Menu Item");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createMenuItem(scanner, in, out);
                    break;
                case 2:
                    UpdateMenuItem(scanner,in,out);
                    break;
                case 3:
                    // Implementation for updating menu item
                    break;
                case 4:
                    // Implementation for deleting menu item
                    break;
                case 5:
                    out.println("logout");
                    String response = in.readLine();
                    System.out.println(response);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static void createMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.print("Enter menu item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter menu item price: ");
        double price = scanner.nextDouble();
        System.out.println("Enter menu item Type: ");
        Integer mealType = scanner.nextInt();

        out.println("createMenuItem:" + name + ":" + price + ":" + mealType);
        String response = in.readLine();
        System.out.println(response);
    }

    private  static void updateMenuItem(Scanner scanner,BufferedReader in,PrintWriter out) throws  IOException{
        System.out.println("Enter Menu Item Name: ");
        String menuItemName= scanner.nextLine();
        System.out.println("Enter the Updated Status of the food");
        String updatedStatus = scanner.nextLine();
        out.println("updatedStatus:" + menuItemName + ":" + updatedStatus);
        String response = in.readLine();
        System.out.println(response);
    }
    private static void showUserMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            System.out.println("User Menu:");
            System.out.println("1. View Menu");
            System.out.println("2. Order Food");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Implementation for viewing menu
                    break;
                case 2:
                    // Implementation for ordering food
                    break;
                case 3:
                    out.println("logout");
                    String response = in.readLine();
                    System.out.println(response);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
