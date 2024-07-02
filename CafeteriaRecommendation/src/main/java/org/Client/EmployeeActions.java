package org.Client;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EmployeeActions {

    public static void showEmployeeMenu(Scanner scanner, BufferedReader in, PrintWriter out, int userId) {
        try {
            while (true) {
                MenuDisplay.displayEmployeeMenu();
                int choice = Utility.readIntInput(scanner);

                switch (choice) {
                    case 1:
                        viewRecommendationMenu(in, out);
                        break;
                    case 2:
                        selectNextDayFoodItems(scanner, in, out, userId);
                        break;
                    case 3:
                        giveFeedbackToAnyFoodItem(scanner, in, out, userId);
                        break;
                    case 4:
                        viewFinalizedMenu(in, out);
                        break;
                    case 5:
                        viewNotifications(in, out);
                        break;
                    case 6:
                        Utility.logout(out);
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewNotifications(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewNotifications");
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

    private static void viewFinalizedMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewFinalizedMenu");
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

    private static void giveFeedbackToAnyFoodItem(Scanner scanner, BufferedReader in, PrintWriter out, int userId) {
        try {
            System.out.println("Enter the Food Item Id for which you want to give the feedback:");
            int foodItemId = scanner.nextInt();
            System.out.println("Enter the Rating (1-5) for the Food item:");
            int foodItemRating = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.println("Enter the Feedback Comment for the food:");
            String comment = scanner.nextLine();
            out.println("giveFeedbackToAnyFoodItem:" + foodItemId + ":" + foodItemRating + ":" + comment + ":" + userId);
            String response = in.readLine();
            System.out.println(response);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void selectNextDayFoodItems(Scanner scanner, BufferedReader in, PrintWriter out, int userId) {
        try {
            System.out.println("Enter the Menu Item Id for the Breakfast:");
            int breakfastMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Lunch:");
            int lunchMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Dinner:");
            int dinnerMenuItemId = scanner.nextInt();
            out.println("selectNextDayFoodItems:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId + ":" + userId);
            String response = in.readLine();
            System.out.println(response);
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewRecommendationMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewRecommendationMenu");
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
}
