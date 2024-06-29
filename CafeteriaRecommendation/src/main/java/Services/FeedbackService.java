package Services;

import Database.Database;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private Database database;

    public FeedbackService(Database database) {
        this.database = database;
    }

    public void giveFeedbackToAnyFoodItem(BufferedReader in, PrintWriter out, String[] parts) {
        int foodItemId = Integer.parseInt(parts[1]);
        int rating = Integer.parseInt(parts[2]);
        String comment = parts[3];
        int userId = Integer.parseInt(parts[4]);
        int isFeedbackSubmitted = database.giveFoodFeedback(foodItemId, rating, comment, userId);
        if (isFeedbackSubmitted == 1) {
            out.println("Feedback Submitted Successfully");
        } else {
            out.println("Feedback Submission have an issue");
        }
    }

    public void viewFoodFeedbackHistory(PrintWriter out) throws SQLException {
        List<String> foodFeedbackHistory = database.getFoodFeedbackHistory();
        if (foodFeedbackHistory.isEmpty()) {
            out.println("No menu items available.");
        } else {
            out.println("----- Feedback Food History By Employees-----");
            out.println(String.format("%-15s %-20s %-10s %-15s %-20s %-10s %-15s %-20s %-10s", "Id", "UserId", "UserName", "FoodItemId", "FoodItemName", "MealType", "Rating", "Comments", "DateTime"));
            out.println("--------------------------------------------");

            for (String menuItem : foodFeedbackHistory) {
                out.println(menuItem);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }
}
