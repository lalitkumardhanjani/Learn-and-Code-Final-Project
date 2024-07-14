package org.Services;

import org.Database.IFeedbackDatabase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private IFeedbackDatabase feedbackDatabase;

    public FeedbackService(IFeedbackDatabase feedbackDatabase) {
        this.feedbackDatabase = feedbackDatabase;
    }

    public void submitFoodFeedback(PrintWriter outputWriter, String[] feedbackData) {
        try {
            if (feedbackData.length < 5) {
                outputWriter.println("Invalid input for submitting feedback.");
                return;
            }

            int foodItemId = Integer.parseInt(feedbackData[1]);
            int rating = Integer.parseInt(feedbackData[2]);
            String comment = feedbackData[3];
            int userId = Integer.parseInt(feedbackData[4]);

            boolean isFeedbackSubmitted = feedbackDatabase.giveFoodFeedback(foodItemId, rating, comment, userId) == 1;
            outputWriter.println(isFeedbackSubmitted ? "Feedback Submitted Successfully" : "Feedback Submission has an issue");
        } catch (ArrayIndexOutOfBoundsException e) {
            outputWriter.println("Error: Missing input data for feedback submission. " + e.getMessage());
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error during feedback submission: " + runtimeException.getMessage());
        }
    }

    public void viewFoodFeedbackHistory(PrintWriter outputWriter) {
        try {
            List<String> foodFeedbackHistory = feedbackDatabase.getFoodFeedbackHistory();
            printFeedbackHistory(outputWriter, foodFeedbackHistory);
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while retrieving feedback history: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while displaying feedback history: " + runtimeException.getMessage());
        }
    }

    private void printFeedbackHistory(PrintWriter outputWriter, List<String> feedbackHistory) {
        try {
            if (feedbackHistory.isEmpty()) {
                outputWriter.println("No feedback history available.");
            } else {
                outputWriter.println("----- Feedback Food History By Employees -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-15s %-20s %-10s %-15s %-20s %-10s",
                        "Id", "UserId", "UserName", "FoodItemId", "FoodItemName", "MealType", "Rating", "Comments", "DateTime"));
                outputWriter.println("---------------------------------------------------------------------------------------------");

                for (String feedback : feedbackHistory) {
                    outputWriter.println(feedback);
                }
                outputWriter.println("---------------------------------------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while printing feedback history: " + runtimeException.getMessage());
        }
    }
}
