package org.Services;

import org.Database.IFeedbackDatabase;
import org.Database.IMenuManagementDatabase;
import org.Database.SqlServerDatabase;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeService {
    private static SqlServerDatabase sqlServerDatabase;
    public EmployeeService() {
        this.sqlServerDatabase = new SqlServerDatabase();
    }


    public static void  submitFoodFeedback(PrintWriter outputWriter, String[] feedbackData) {
        try {
            if (feedbackData.length < 5) {
                outputWriter.println("Invalid input for submitting feedback.");
                return;
            }

            int foodItemId = Integer.parseInt(feedbackData[2]);
            int rating = Integer.parseInt(feedbackData[3]);
            String comment = feedbackData[4];
            int userId = Integer.parseInt(feedbackData[5]);

            boolean isFeedbackSubmitted = sqlServerDatabase.giveFoodFeedback(foodItemId, rating, comment, userId) == 1;
            outputWriter.println(isFeedbackSubmitted ? "Feedback Submitted Successfully" : "Feedback Submission has an issue");
        } catch (ArrayIndexOutOfBoundsException e) {
            outputWriter.println("Error: Missing input data for feedback submission. " + e.getMessage());
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error during feedback submission: " + runtimeException.getMessage());
        }
    }

    public static void setSqlServerDatabase(SqlServerDatabase sqlServerDatabase) {
        EmployeeService.sqlServerDatabase = sqlServerDatabase;
    }

    public void viewMenu(PrintWriter outputWriter) {
        try {
            List<String> menuItems = sqlServerDatabase.getMenuItems();
            printMenu(outputWriter, menuItems, "----- Cafeteria Menu -----");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing menu: " + runtimeException.getMessage());
        }
    }

    private void printMenu(PrintWriter outputWriter, List<String> menuItems, String header) {
        try {
            if (menuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println(header);
                outputWriter.println(String.format("%-15s %-20s %-10s %-15s %-15s", "Id", "Item", "Price", "Meal ID", "IsAvailable"));
                outputWriter.println("--------------------------------------------");
                for (String menuItem : menuItems) {
                    outputWriter.println(menuItem);
                }
                outputWriter.println("--------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while printing menu: " + runtimeException.getMessage());
        }
    }

    public void viewRecommendationMenu(PrintWriter outputWriter, String[] menuItemData) {
        try {
            int userId = Integer.parseInt(menuItemData[1]);
            List<String> recommendedMenu = sqlServerDatabase.getRecommendedMenu(userId);
            if (recommendedMenu.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println("----- Recommended Cafeteria Menu -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenu) {
                    outputWriter.println(menuItem);
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing recommendation menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing recommendation menu: " + runtimeException.getMessage());
        }
    }

    public void selectNextDayFoodItems(PrintWriter outputWriter, String[] menuItemData) {
        try {

            int breakfastMenuItemId = Integer.parseInt(menuItemData[2]);
            int lunchMenuItemId = Integer.parseInt(menuItemData[3]);
            int dinnerMenuItemId = Integer.parseInt(menuItemData[4]);
            int userId = Integer.parseInt(menuItemData[5]);

            List<Integer> ids = new ArrayList<>(Arrays.asList(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId, userId));
            int isSelected = sqlServerDatabase.insertSelectedFoodItemsInDB(ids);
            outputWriter.println(isSelected == 1 ? "Selected food items successfully" : "Selected food items have some issues");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while selecting next day food items: " + runtimeException.getMessage());
        }
    }

    public void viewFinalizedMenu(PrintWriter outputWriter) {
        try {
            List<String> finalizedMenu = sqlServerDatabase.getFinalizedMenu();
            printMenu(outputWriter, finalizedMenu, "----- Finalized Menu -----");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing finalized menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing finalized menu: " + runtimeException.getMessage());
        }
    }

    public void fillEmployeeImprovementAnswers(BufferedReader inputReader, PrintWriter outputWriter, String[] employeeItemData) {
        try {
            sqlServerDatabase.insertImprovementAnswersinDB(inputReader,outputWriter,employeeItemData);
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());

        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while inserting Improvement Questions and Answers: " + runtimeException.getMessage());
            // Handle or log the exception as per your project's requirements
        }
    }

    public void viewNotifications(PrintWriter outputWriter) {
        try {
            List<String> notificationList = sqlServerDatabase.getNotifications();
            if (notificationList.isEmpty()) {
                outputWriter.println("No notifications are present.");
            } else {
                outputWriter.println("-------------- Notifications -------------");
                outputWriter.println("------------------------------------------");

                for (String notification : notificationList) {
                    outputWriter.println(notification);
                }
                outputWriter.println("------------------------------------------");
            }
            outputWriter.println("END");
        } catch (Exception exception) {
            outputWriter.println("Error retrieving notifications: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public void viewImprovementQuestionAndAnswers(BufferedReader inputReader, PrintWriter outputWriter) {
        try {
            List<String> improvementQuestionAnswers = sqlServerDatabase.getImprovementQuestionsandAnswers();
            if (improvementQuestionAnswers.isEmpty()) {
                outputWriter.println("No improvement questions and answers available.");
            } else {
                outputWriter.println("----- Improvement Questions and Answers -----");
                outputWriter.println(String.format("%-50s %-50s %-50s %-50s %-50s", "FoodItemId", "UserId", "What didn't you like about Food?", "How would you like Food?", "Share your mom's recipe for Food?"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String questionAnswer : improvementQuestionAnswers) {
                    outputWriter.println(questionAnswer);
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing Improvement Questions and Answers: " + runtimeException.getMessage());
        }
    }
    public void makeEmployeeProfile(BufferedReader inputReader, PrintWriter outputWriter, String[] employeeItemData) {
        try {
            sqlServerDatabase.makeEmployeeProfile(inputReader, outputWriter, employeeItemData);
            outputWriter.println("Employee Profile added successfully");

        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while making Employee Profile: " + runtimeException.getMessage());
        }
    }


}
