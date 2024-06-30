package Client;

import java.io.*;
import java.util.Scanner;

public class EmployeeActions {
    public static void showEmployeeMenu(Scanner scanner, BufferedReader in, PrintWriter out, int userId) throws IOException {
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
    }

    private static void viewNotifications(BufferedReader in, PrintWriter out) throws IOException {
        out.println("viewNotifications");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }

    private static void viewFinalizedMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("viewFinalizedMenu");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }

    private static void giveFeedbackToAnyFoodItem(Scanner scanner, BufferedReader in, PrintWriter out, int userId) throws IOException {
        System.out.println("Enter the Food Item Id for which you want to give the feedback");
        int foodItemId = scanner.nextInt();
        System.out.println("Enter the Rating (1-5) for the Food item");
        int foodItemRating = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the Feedback Comment for the food");
        String comment = scanner.nextLine();
        out.println("giveFeedbackToAnyFoodItem:" + foodItemId + ":" + foodItemRating + ":" + comment + ":" + userId);
        String response;
        response = in.readLine();
        System.out.println(response);
    }

    private static void selectNextDayFoodItems(Scanner scanner, BufferedReader in, PrintWriter out, int userId) throws IOException {
        System.out.println("Enter the Menu Item Id for the Breakfast");
        int breakfastMenuItemId = scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Lunch");
        int lunchMenuItemId = scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Dinner");
        int dinnerMenuItemId = scanner.nextInt();
        out.println("selectNextDayFoodItems:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId + ":" + userId);
        String response;
        response = in.readLine();
        System.out.println(response);
    }

    private static void viewRecommendationMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("viewRecommendationMenu");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }
}
