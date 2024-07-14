package org.Client;

import org.springframework.context.annotation.Import;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.Constant.*;

public class AdminActions {
    public static void showAdminMenu(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
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
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void createMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            scanner.nextLine();
            System.out.println("Enter menu item name: ");
            String name = Utility.readStringInput(scanner);
            System.out.println("Enter menu item price: ");
            double price = Utility.readDoubleInput(scanner);
            System.out.println("Enter menu item Type (1-Breakfast, 2-Lunch, 3-Dinner): ");
            int mealType = Utility.readIntInput(scanner);
            scanner.nextLine();
            System.out.println("Enter item availability (1-Available, 0-Not Available): ");
            int availability = Utility.readIntInput(scanner);
            scanner.nextLine();
            System.out.println("Enter the Dietary Preference(Vegetarian,Non Vegetarian)");
            String dietary = Utility.readStringInput(scanner);
            System.out.println("Enter the Spice Level(High,Medium,Low)");
            String spiceLevel = Utility.readStringInput(scanner);
            System.out.println("Enter the Cuisine(North Indian,South Indian)");
            String cuisine = Utility.readStringInput(scanner);
            System.out.println("Enter the sweeth tooth(1-true,0-false)");
            int isSweetTooth = Utility.readIntInput(scanner);
            out.println(Constant.adminRole+":"+"createMenuItem:" + name + ":" + price + ":" + mealType + ":" + availability+ ":" + dietary+ ":" + spiceLevel+ ":" + cuisine+ ":" + isSweetTooth);
            String response = in.readLine();
            System.out.println(response);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.adminRole+":"+"viewMenu");
            String response;
            while (!(response = in.readLine()).equals("END")) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void updateMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            System.out.println("Enter the Menu Item ID:");
            int menuItemId = Utility.readIntInput(scanner);
            scanner.nextLine(); // consume newline

            System.out.println("Enter Menu Item Name:");
            String menuItemName = Utility.readStringInput(scanner);

            System.out.println("Enter Menu Item Price:");
            String priceInput = Utility.readStringInput(scanner);
            Double menuItemPrice = priceInput.isEmpty() ? null : Double.parseDouble(priceInput);

            System.out.println("Enter Menu Item Availability:");
            String availabilityInput = Utility.readStringInput(scanner);
            Integer menuItemAvailability = availabilityInput.isEmpty() ? null : Integer.parseInt(availabilityInput);

            out.println(Constant.adminRole+":"+"updateMenuItem:" + menuItemId + ":" + menuItemName + ":" + menuItemPrice + ":" + menuItemAvailability);
            String response = in.readLine();
            System.out.println(response);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void deleteMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            System.out.println("Enter menu item Id: ");
            int menuId = Utility.readIntInput(scanner);
            out.println(Constant.adminRole+":"+"deleteMenuItem:" + menuId);
            String response = in.readLine();
            System.out.println(response);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine();
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
