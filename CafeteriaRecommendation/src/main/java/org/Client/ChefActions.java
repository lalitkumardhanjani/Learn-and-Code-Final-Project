package org.Client;

import org.Constant.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
                        viewDiscardMenuItems(in, out);
                        break;
                    case 9:
                        sendImprovementQuestionsForDiscardFoodItems(in, out);
                        break;
                    case 10:
                        viewImprovementQuestionandAnswers(in, out);
                        break;
                    case 11:
                        deleteMenuItem(scanner, in, out);
                        break;
                    case 12:
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

    public static void deleteMenuItem(Scanner scanner, BufferedReader in, PrintWriter out) {
        try {
            System.out.println("Enter menu item Id: ");
            int menuId = Utility.readIntInput(scanner);
            out.println(Constant.chefRole+":"+"deleteMenuItem:" + menuId);
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

    private static void viewImprovementQuestionandAnswers(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.chefRole+":"+"viewImprovementQuestionandAnswers");
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

    private static void sendImprovementQuestionsForDiscardFoodItems(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.chefRole+":"+"sendImprovementQuestionsForDiscardFoodItems");
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewDiscardMenuItems(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.chefRole+":"+"viewDiscardMenuItems");
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

    private static void viewMenu(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.chefRole+":"+"viewMenu");
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
            out.println(Constant.chefRole+":"+"generateRecommendationMenu");
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
            out.println(Constant.chefRole+":"+"rolloutRecommendationMenu");
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
            out.println(Constant.chefRole+":"+"viewSelectedFoodItemsEmployees");
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
            int breakfastMenuItemId = Utility.readIntInput(scanner);
            System.out.println("Enter the Menu Item Id for the Finalized Lunch");
            int lunchMenuItemId = Utility.readIntInput(scanner);
            System.out.println("Enter the Menu Item Id for the Finalized Dinner");
            int dinnerMenuItemId = Utility.readIntInput(scanner);
            out.println(Constant.chefRole+":"+"generateFinalizedMenu:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId);
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
            out.println(Constant.chefRole+":"+"rolloutFinalizedMenu");
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
            out.println(Constant.chefRole+":"+"viewFoodFeedbackHistory");
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
