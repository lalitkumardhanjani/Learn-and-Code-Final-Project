package Client;

import java.io.*;
import java.util.Scanner;

public class AdminActions {
    public static void showAdminMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            MenuDisplay.displayAdminMenu();
            int choice = Utility.readIntInput(scanner);

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
                    deleteMenuItem(scanner, in, out);
                    break;
                case 5:
                    Utility.logout(out);
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
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

    private static void deleteMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Enter menu item Id: ");
        int menuId = scanner.nextInt();

        out.println("deleteMenuItem:" + menuId);
        String response = in.readLine();
        System.out.println(response);
    }
}
