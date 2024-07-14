package org.Client;

import org.Constant.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
                        viewRecommendationMenu(in, out,userId);
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
                        giveImprovementQuestionsAnswers(scanner, in, out, userId);
                        break;
                    case 7:
                        makeEmployeeProfile(scanner,in,out,userId);
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
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void makeEmployeeProfile(Scanner scanner,BufferedReader in, PrintWriter out, int userId) throws IOException {
        System.out.println("Creating Your Profile...");
        System.out.println("Enter your Dietery Preference(Vegetarian,Non Vegetarian,Both)");
        scanner.nextLine();
        String dietary = Utility.readStringInput(scanner);
        System.out.println("Enter your Spice Level(High,Medium,Low)");
        String spiceLevel = Utility.readStringInput(scanner);
        System.out.println("Enter you Cousine(North Indian,South Indian,Both)");
        String cousine = Utility.readStringInput(scanner);
        System.out.println("Enter your Sweeth Tooth(1 - Sweeth Tooth,0 - No Sweeth Tooth)");
        Integer isSweethTooth = Utility.readIntInput(scanner);

        System.out.println(dietary+spiceLevel+cousine+isSweethTooth+userId);

        out.println(Constant.employeeRole+":"+"makeEmployeeProfile:" + dietary + ":" + spiceLevel + ":" + cousine + ":" + isSweethTooth + ":" + userId);
        String response = in.readLine();
        System.out.println(response);

    }


    private static void giveImprovementQuestionsAnswers(Scanner scanner, BufferedReader in, PrintWriter out, int userId) throws IOException {
        out.println(Constant.employeeRole+":"+"getDiscardFoodItemIds");
        String response = in.readLine();
        System.out.println(response);
        String[] discardedItemIds = response.split(",");

        for (String foodItemId : discardedItemIds) {
            scanner.nextLine();
            System.out.println("Q1. What didn't you like about FoodId " + foodItemId + "?");
            String answer1 = Utility.readStringInput(scanner);

            System.out.println("Q2. How would you like FoodId " + foodItemId + " to taste?");
            String answer2  = Utility.readStringInput(scanner);

            System.out.println("Q3. Share your mom's recipe for FoodId " + foodItemId + "?");
            String answer3  = Utility.readStringInput(scanner);

            // Send improvement answers
            out.println("improvementAnswers:" + foodItemId + ":" + userId + ":" + answer1 + ":" + answer2 + ":" + answer3);

            System.out.println("Thank you for the giving the Feedback");
        }
    }

    private static void viewNotifications(BufferedReader in, PrintWriter out) {
        try {
            out.println(Constant.employeeRole+":"+"viewNotifications");
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
            out.println(Constant.employeeRole+":"+"viewFinalizedMenu");
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
            int foodItemId = Utility.readIntInput(scanner);
            System.out.println("Enter the Rating (1-5) for the Food item:");
            int foodItemRating = Utility.readIntInput(scanner);
            scanner.nextLine(); // Consume newline
            System.out.println("Enter the Feedback Comment for the food:");
            String comment = Utility.readStringInput(scanner);
            out.println(Constant.employeeRole+":"+"giveFeedbackToAnyFoodItem:" + foodItemId + ":" + foodItemRating + ":" + comment + ":" + userId);
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
            int breakfastMenuItemId = Utility.readIntInput(scanner);
            System.out.println("Enter the Menu Item Id for the Lunch:");
            int lunchMenuItemId = Utility.readIntInput(scanner);
            System.out.println("Enter the Menu Item Id for the Dinner:");
            int dinnerMenuItemId = Utility.readIntInput(scanner);
            out.println(Constant.employeeRole+":"+"selectNextDayFoodItems:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId + ":" + userId);
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

    private static void viewRecommendationMenu(BufferedReader in, PrintWriter out,int userId) {
        try {
            out.println(Constant.employeeRole+":"+"viewRecommendationMenu:"+userId);
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
