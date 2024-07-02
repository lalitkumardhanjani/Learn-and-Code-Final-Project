package org.Client;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ChefActions {
    public static void showChefMenu(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            while (true) {
                MenuDisplay.displayChefMenu();
                int choice = Utility.readIntInput(scanner);

                switch (choice) {
                    case 1:
                        viewMenu(in, out);
                        break;
                    case 2:
                        generateRecommendationMenu(in, out);
                        break;
                    case 3:
                        rolloutRecommendationMenu(in, out);
                        break;
                    case 4:
                        viewSelectedFoodItemsEmployees(in, out);
                        break;
                    case 5:
                        generateFinalizedMenu(scanner, in, out);
                        break;
                    case 6:
                        rolloutFinalizedMenu(in, out);
                        break;
                    case 7:
                        viewFoodFeedbackHistory(in, out);
                        break;
                    case 8:
                        Utility.logout(out);
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewMenu");
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

    private static void generateRecommendationMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("generateRecommendationMenu");
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

    private static void rolloutRecommendationMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("rolloutRecommendationMenu");
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewSelectedFoodItemsEmployees(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewSelectedFoodItemsEmployees");
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

    private static void generateFinalizedMenu(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            System.out.println("Enter the Menu Item Id for the Finalized Breakfast");
            int breakfastMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Finalized Lunch");
            int lunchMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Finalized Dinner");
            int dinnerMenuItemId = scanner.nextInt();
            out.println("generateFinalizedMenu:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId);
            String response;
            while (!(response = in.readLine()).equals("END")) {
                System.out.println(response);
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void rolloutFinalizedMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println("rolloutFinalizedMenu");
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewFoodFeedbackHistory(BufferedReader in, PrintWriter out) {
        try {
            out.println("viewFoodFeedbackHistory");
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
