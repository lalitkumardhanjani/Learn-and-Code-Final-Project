package org.Services;

import org.Database.FeedbackDatabase;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private FeedbackDatabase feedbackDatabase;

    public FeedbackService(FeedbackDatabase feedbackDatabase) {
        this.feedbackDatabase = feedbackDatabase;
    }

    public void submitFoodFeedback(BufferedReader in, PrintWriter out, String[] parts) {
        if (parts.length < 5) {
            out.println("Invalid input for submitting feedback.");
            return;
        }

        int foodItemId = Integer.parseInt(parts[1]);
        int rating = Integer.parseInt(parts[2]);
        String comment = parts[3];
        int userId = Integer.parseInt(parts[4]);

        boolean isFeedbackSubmitted = feedbackDatabase.giveFoodFeedback(foodItemId, rating, comment, userId) == 1;
        out.println(isFeedbackSubmitted ? "Feedback Submitted Successfully" : "Feedback Submission has an issue");
    }

    public void displayFoodFeedbackHistory(PrintWriter out) throws SQLException {
        List<String> foodFeedbackHistory = feedbackDatabase.getFoodFeedbackHistory();
        printFeedbackHistory(out, foodFeedbackHistory);
    }

    private void printFeedbackHistory(PrintWriter out, List<String> feedbackHistory) {
        if (feedbackHistory.isEmpty()) {
            out.println("No feedback history available.");
        } else {
            out.println("----- Feedback Food History By Employees -----");
            out.println(String.format("%-15s %-20s %-10s %-15s %-20s %-10s %-15s %-20s %-10s",
                    "Id", "UserId", "UserName", "FoodItemId", "FoodItemName", "MealType", "Rating", "Comments", "DateTime"));
            out.println("---------------------------------------------------------------------------------------------");

            for (String feedback : feedbackHistory) {
                out.println(feedback);
            }
            out.println("---------------------------------------------------------------------------------------------");
        }
        out.println("END");
    }
}
