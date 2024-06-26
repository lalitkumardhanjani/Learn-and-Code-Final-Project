package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final int PORT = 1234;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server on port " + socket.getLocalPort());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);

            displayMainMenu();
            int roleChoice = readIntInput(scanner);

            System.out.println("Enter userId: ");
            int userId = readIntInput(scanner);
            scanner.nextLine();
            System.out.println("Enter password: ");
            String password = scanner.nextLine();

            out.println(roleChoice + ":" + userId + ":" + password);

            String response = in.readLine();
            System.out.println(response);

            if (response.startsWith("Login successful")) {
                String[] responseParts = response.split(":");
                if (responseParts.length > 1) {
                    String role = responseParts[1].trim();
                    if ("admin".equalsIgnoreCase(role)) {
                        showAdminMenu(scanner, in, out);
                    } else if("chef".equalsIgnoreCase(role)){
                        showChefMenu(scanner, in, out);
                    } else if("employee".equalsIgnoreCase(role)){
                        showEmployeeMenu(scanner,in,out);
                    }
                } else {
                    System.out.println("Role information missing in response.");
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

    private static void displayMainMenu() {
        System.out.println("Welcome to Cafeteria Management System");
        System.out.println("Select your role:");
        System.out.println("1. Admin");
        System.out.println("2. Chef");
        System.out.println("3. Employee");
        System.out.print("Enter your choice: ");
    }

    private static void showAdminMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            displayAdminMenu();
            int choice = readIntInput(scanner);

            switch (choice) {
                case 1:
                    createMenuItem(scanner, in, out);
                    break;
                case 2:
                    viewMenu(in, out);
                    break;
                case 3:
                    updateMenuItem(scanner, in, out);
                    break;
                case 4:
                    deleteMenuItem(scanner,in,out);
                    break;
                case 5:
                    logout(out);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void deleteMenuItem(Scanner scanner,BufferedReader in,PrintWriter out) throws IOException {
        System.out.println("Enter menu item Id: ");
        int menuId = scanner.nextInt();

        out.println("deleteMenuItem:" + menuId);
        String response = in.readLine();
        System.out.println(response);
    }
    private static void displayAdminMenu() {
        System.out.println("Admin Menu:");
        System.out.println("1. Create Menu Item");
        System.out.println("2. View Menu Items");
        System.out.println("3. Update Menu Item");
        System.out.println("4. Delete Menu Item");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
    }

    private static void createMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        scanner.nextLine(); // consume newline
        System.out.println("Enter menu item name: ");
        String name = scanner.nextLine();
        System.out.println("Enter menu item price: ");
        double price = scanner.nextDouble();
        System.out.println("Enter menu item Type (1-Breakfast, 2-Lunch, 3-Dinner): ");
        int mealType = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter item availability (1-Available, 0-Not Available): ");
        int availability = scanner.nextInt();

        out.println("createMenuItem:" + name + ":" + price + ":" + mealType + ":" + availability);
        String response = in.readLine();
        System.out.println(response);
    }

    private static void viewMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("viewMenu");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }

    private static void updateMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Enter the Menu Item ID:");
        int menuItemId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Enter Menu Item Name (Blank to skip):");
        String menuItemName = scanner.nextLine();

        System.out.println("Enter Menu Item Price (Blank to skip):");
        String priceInput = scanner.nextLine();
        Double menuItemPrice = priceInput.isEmpty() ? null : Double.parseDouble(priceInput);

        System.out.println("Enter Menu Item Availability (1/0) (Blank to skip):");
        int menuItemAvailability = scanner.nextInt();

        out.println("updateMenuItem:" + menuItemId + ":" + menuItemName + ":" + menuItemPrice + ":" + menuItemAvailability);
        String response = in.readLine();
        System.out.println(response);
    }


    private static void showChefMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            displayChefMenu();
            int choice = readIntInput(scanner);

            switch (choice) {
                case 1:
                    viewMenu(in, out);
                    break;
                case 2:
                    generateRecommendationMenu(in, out);
                    break;
                case 3:
                    rolloutRecommendationMenu(in,out);
                    break;
                case 4:
                    generateFinalizedMenu(scanner,in,out);
                    break;
                case 5:
                    rolloutFinalizedMenu(in,out);
                    break;
                case 6:
                    logout(out);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void generateFinalizedMenu(Scanner scanner,BufferedReader in,PrintWriter out) throws IOException{

        System.out.println("Enter the Menu Item Id for the Finalized Breakfast");
        int breakfastMenuItemId=scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Finalized Lunch");
        int lunchMenuItemId = scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Finalized Dinner");
        int dinnerMenuItemId = scanner.nextInt();
        out.println("generateFinalizedMenu:"+breakfastMenuItemId+":"+lunchMenuItemId+":"+dinnerMenuItemId);
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }

    private static void  rolloutFinalizedMenu(BufferedReader in,PrintWriter out) throws  IOException{
        out.println("rolloutFinalizedMenu");
        String response = in.readLine();
        System.out.println(response);
    }
    private static void  rolloutRecommendationMenu(BufferedReader in,PrintWriter out) throws  IOException{
        out.println("rolloutRecommendationMenu");
        String response = in.readLine();
        System.out.println(response);
    }
    private static void generateRecommendationMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("generateRecommendationMenu");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }
    private static void displayChefMenu() {
        System.out.println("Chef Menu:");
        System.out.println("1. View Menu");
        System.out.println("2. Generate Food Recommendation Menu");
        System.out.println("3. Rollout Food Recommendation Menu");
        System.out.println("4. Generate Finalized Menu");
        System.out.println("5. Rollout Finalized Menu");
        System.out.print("Enter your choice: ");
    }

    private static int readIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void logout(PrintWriter out) {
        out.println("logout");
        System.out.println("Logged out successfully.");
    }
}
