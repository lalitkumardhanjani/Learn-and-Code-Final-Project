package org.Services;

import org.Database.IFeedbackDatabase;
import org.Database.IMenuManagementDatabase;
import org.Database.SqlServerDatabase;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ChefService {
    private static SqlServerDatabase sqlServerDatabase;
    public ChefService() {
        this.sqlServerDatabase = new SqlServerDatabase();
    }

    public static void viewFoodFeedbackHistory(PrintWriter outputWriter) {
        try {
            List<String> foodFeedbackHistory = sqlServerDatabase.getFoodFeedbackHistory();
            printFeedbackHistory(outputWriter, foodFeedbackHistory);
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while retrieving feedback history: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while displaying feedback history: " + runtimeException.getMessage());
        }
    }

    private static void printFeedbackHistory(PrintWriter outputWriter, List<String> feedbackHistory) {
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

    public void generateRecommendationMenu(PrintWriter outputWriter) {
        try {
            List<String> recommendedMenuItems = sqlServerDatabase.generateRecommendedMenu();
            if (recommendedMenuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else if (Objects.equals(recommendedMenuItems.get(0), "Recommendation Menu already generated.")) {
                outputWriter.println("Recommendation Menu already generated.");
            } else {
                outputWriter.println("----- Recommended Cafeteria Menu -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenuItems) {
                    String[] menuItemData = menuItem.split("\\^");
                    if (menuItemData.length >= 6) {
                        outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s",
                                menuItemData[0], menuItemData[1], menuItemData[2], menuItemData[3], menuItemData[4], menuItemData[5]));
                    } else {
                        outputWriter.println("Invalid format for menu item: " + menuItem);
                    }
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while generating recommendation menu: " + sqlException.getMessage());
        } catch (IOException ioException) {
            outputWriter.println("I/O error while generating recommendation menu: " + ioException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while generating recommendation menu: " + runtimeException.getMessage());
        }
    }

    public void rolloutRecommendationMenu(PrintWriter outputWriter) {
        try {
            int rolloutStatus = sqlServerDatabase.rolloutRecommendation();
            outputWriter.println(rolloutStatus == 1 ? "Roll out successful" : "Roll out encountered issues");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while rolling out recommendation menu: " + runtimeException.getMessage());
        }
    }

    public void generateFinalizedMenu(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 4) {
                outputWriter.println("Invalid input for generating finalized menu.");
                return;
            }

            int breakfastMenuItemId = Integer.parseInt(menuItemData[1]);
            int lunchMenuItemId = Integer.parseInt(menuItemData[2]);
            int dinnerMenuItemId = Integer.parseInt(menuItemData[3]);

            List<String> finalizedMenu = sqlServerDatabase.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
            printMenu(outputWriter, finalizedMenu, "----- Finalized Cafeteria Menu -----");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while generating finalized menu: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while generating finalized menu: " + runtimeException.getMessage());
        }
    }

    public void rolloutFinalizedMenu(PrintWriter outputWriter) {
        try {
            int rolloutStatus = sqlServerDatabase.rolloutFinalizedMenusStatusUpdate();
            outputWriter.println(rolloutStatus == 1 ? "Roll out successful" : "Roll out encountered issues");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while rolling out finalized menu: " + runtimeException.getMessage());
        }
    }


    public void viewSelectedFoodItemsEmployees(PrintWriter outputWriter) {
        try {
            List<String> selectedFoodItemsByEmployees = sqlServerDatabase.getSelectedFoodItemsEmployees();
            if (selectedFoodItemsByEmployees.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println("----- Selected Food Items By Employees -----");
                outputWriter.println(String.format("%-15s %-20s %-10s ", "Id", "Item", "VoteCount"));
                outputWriter.println("--------------------------------------------");
                for (String menuItem : selectedFoodItemsByEmployees) {
                    outputWriter.println(menuItem);
                }
                outputWriter.println("--------------------------------------------");
            }
            outputWriter.println("END");
        } catch (SQLException sqlException) {
            outputWriter.println("Database error while viewing selected food items by employees: " + sqlException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing selected food items by employees: " + runtimeException.getMessage());
        }
    }

    public void viewDiscardMenuItems(PrintWriter outputWriter) {
        try {
            List<String> discardMenuItems = sqlServerDatabase.generateDiscardMenuItems();
            if (discardMenuItems.isEmpty()) {
                outputWriter.println("No menu items available.");
            } else {
                outputWriter.println("----- Discard Cafeteria Menu Items -----");
                outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s", "Id", "Name", "Price", "Availability", "Meal Type"));
                outputWriter.println("-----------------------------------------------------------------");
                for (String menuItem : discardMenuItems) {
                    String[] menuItemData = menuItem.split("\\^");
                    if (menuItemData.length >= 5) {
                        outputWriter.println(String.format("%-15s %-20s %-10s %-20s %-15s",
                                menuItemData[0], menuItemData[1], menuItemData[2], menuItemData[3], menuItemData[4]));
                    } else {
                        outputWriter.println("Invalid format for menu item: " + menuItem);
                    }
                }
                outputWriter.println("-----------------------------------------------------------------");
            }
            outputWriter.println("END");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing discard menu items: " + runtimeException.getMessage());
        }
    }
    public void sendImprovementQuestionsForDiscardFoodItems(PrintWriter outputWriter) {
        boolean isSendSuccess = sqlServerDatabase.updateStatusOfDiscardItem();
        if (isSendSuccess) {
            outputWriter.println("Successfully sent the Improvement Questions to Employees");
        } else {
            outputWriter.println("There was an issue while sending the Improvement Questions");
        }
    }

    public void getDiscardFoodItemIds(PrintWriter outputWriter) throws SQLException {
        String discardFoodItemIds = sqlServerDatabase.getDiscardFoodItemIds();
        outputWriter.println(discardFoodItemIds);
    }
}
