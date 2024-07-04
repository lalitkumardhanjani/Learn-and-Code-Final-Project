package org.Services;

import org.Database.MenuManagementDatabase;
import org.Database.FeedbackDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuService {
    private final MenuManagementDatabase menuDatabase;
    private final FeedbackDatabase feedbackDatabase;

    public MenuService(MenuManagementDatabase menuDatabase, FeedbackDatabase feedbackDatabase) {
        this.menuDatabase = menuDatabase;
        this.feedbackDatabase = feedbackDatabase;
    }

    public void handleMenuCreation(PrintWriter out, String[] parts, Integer userId) {
        try {
            if (userId == null) {
                out.println("Not authorized.");
                return;
            }

            if (parts.length < 5) {
                out.println("Invalid input for creating menu item.");
                return;
            }

            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            Integer mealType = Integer.parseInt(parts[3]);
            int availability = Integer.parseInt(parts[4]);

            menuDatabase.createMenuItem(name, price, mealType, availability);
            out.println("Menu item created successfully.");
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format in input data. " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error while creating menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleUpdateMenuItem(PrintWriter out, String[] parts, Integer userId) {
        try {
            if (userId == null) {
                out.println("Not authorized.");
                return;
            }

            if (parts.length < 5) {
                out.println("Invalid input for updating menu item.");
                return;
            }

            int menuId = Integer.parseInt(parts[1]);
            String newName = parts[2];
            Double newPrice = Double.parseDouble(parts[3]);
            int newAvailability = Integer.parseInt(parts[4]);

            menuDatabase.updateMenuItem(menuId, newName, newPrice, newAvailability);
            out.println("Menu item updated successfully.");
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format in input data. " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error while updating menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleDeleteMenuItem(PrintWriter out, String[] parts, Integer userId) {
        try {
            if (userId == null) {
                out.println("Not authorized.");
                return;
            }

            if (parts.length < 2) {
                out.println("Invalid input for deleting menu item.");
                return;
            }

            int menuId = Integer.parseInt(parts[1]);
            boolean isDeleted = menuDatabase.deleteMenuItem(menuId);
            if (isDeleted) {
                out.println("Menu item deleted successfully.");
            } else {
                out.println("Failed to delete menu item.");
            }
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format in input data. " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error while deleting menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleViewMenu(PrintWriter out, Integer userId) {
        try {
            if (userId == null) {
                out.println("Not authorized.");
                return;
            }

            List<String> menuItems = menuDatabase.getMenuItems();
            printMenu(out, menuItems, "----- Cafeteria Menu -----");
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateRecommendation(PrintWriter out, Integer userId) {
        try {
            if (userId == null) {
                out.println("Not authorized.");
                return;
            }

            List<String> recommendedMenuItems = menuDatabase.generateRecommendedMenu();
            if (recommendedMenuItems.isEmpty()) {
                out.println("No menu items available.");
            } else if (Objects.equals(recommendedMenuItems.get(0), "Recommendation Menu already generated.")){
                out.println("Recommendation Menu already generated.");
            }
            else {
                out.println("----- Recommended Cafeteria Menu -----");
                out.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                out.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenuItems) {
                    String[] parts = menuItem.split("\\^");
                    if (parts.length >= 6) {
                        out.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s",
                                parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                    } else {
                        out.println("Invalid format for menu item: " + menuItem);
                    }
                }
                out.println("-----------------------------------------------------------------");
            }
            out.println("END");
        } catch (SQLException e) {
            out.println("Database error while generating recommendation menu: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            out.println("I/O error while generating recommendation menu: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            out.println("Unexpected error while generating recommendation menu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void viewDiscardMenuItems(PrintWriter out) {
        try {
            List<String> recommendedMenu = menuDatabase.generateDiscardMenuItems();
            if (recommendedMenu.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println("----- Discard Cafeteria Menu Items -----");
                out.println(String.format("%-15s %-20s %-10s %-20s %-15s", "Id", "Name", "Price", "isAvailable", "MealType"));
                out.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenu) {
                    String[] parts = menuItem.split("\\^");
                    if (parts.length >= 5) {
                        out.println(String.format("%-15s %-20s %-10s %-20s %-15s",
                                parts[0], parts[1], parts[2], parts[3], parts[4]));
                    } else {
                        out.println("Invalid format for menu item: " + menuItem);
                    }
                }
                out.println("-----------------------------------------------------------------");
            }
            out.println("END");
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing recommendation menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendImprovementQuestionsForDiscardFoodItems(PrintWriter out){
        boolean isSendSuccess=menuDatabase.updateStatusOfDiscardItem();
        if(isSendSuccess)
            out.println("Successfully send the Improvement Questions to Employees");
        else
            out.println("There is some issue while sending the Improvement Questions");

    }

    public void getDiscardFoodItemIds(PrintWriter out) throws SQLException {
        String discardFoodItemIds=menuDatabase.getDiscardFoodItemIds();
        out.println(discardFoodItemIds);
    }
    public void rolloutRecommendationMenu(PrintWriter out) {
        try {
            int isRollout = menuDatabase.rolloutRecommendation();
            out.println(isRollout == 1 ? "Roll out successfully" : "Roll out has some issues");
        } catch (RuntimeException e) {
            out.println("Unexpected error while rolling out recommendation menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateFinalizedMenu(PrintWriter out, String[] parts) {
        try {
            if (parts.length < 4) {
                out.println("Invalid input for generating finalized menu.");
                return;
            }

            int breakfastMenuItemId = Integer.parseInt(parts[1]);
            int lunchMenuItemId = Integer.parseInt(parts[2]);
            int dinnerMenuItemId = Integer.parseInt(parts[3]);

            List<String> finalizedMenu = menuDatabase.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
            printMenu(out, finalizedMenu, "----- Finalized Cafeteria Menu -----");
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format in input data. " + e.getMessage());
        } catch (SQLException e) {
            out.println("Database error while generating finalized menu: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            out.println("Unexpected error while generating finalized menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void rolloutFinalizedMenu(PrintWriter out) {
        try {
            int isRollout = menuDatabase.rolloutFinalizedMenusStatusUpdate();
            out.println(isRollout == 1 ? "Roll out successfully" : "Roll out has some issues");
        } catch (RuntimeException e) {
            out.println("Unexpected error while rolling out finalized menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewRecommendationMenu(PrintWriter out) {
        try {
            List<String> recommendedMenu = menuDatabase.getRecommendedMenu();
            if (recommendedMenu.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println("----- Recommended Cafeteria Menu -----");
                out.println(String.format("%-15s %-20s %-10s %-20s %-15s %-15s", "Id", "Item", "Price", "Meal Type", "Avg Rating", "Avg Comment"));
                out.println("-----------------------------------------------------------------");
                for (String menuItem : recommendedMenu) {
                    out.println(menuItem);
                }
                out.println("-----------------------------------------------------------------");
            }
            out.println("END");
        } catch (SQLException e) {
            out.println("Database error while viewing recommendation menu: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing recommendation menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void selectNextDayFoodItems(PrintWriter out, String[] parts) {
        try {
            if (parts.length < 5) {
                out.println("Invalid input for selecting next day food items.");
                return;
            }

            int breakfastMenuItemId = Integer.parseInt(parts[1]);
            int lunchMenuItemId = Integer.parseInt(parts[2]);
            int dinnerMenuItemId = Integer.parseInt(parts[3]);
            int userId = Integer.parseInt(parts[4]);

            List<Integer> ids = new ArrayList<>();
            ids.add(breakfastMenuItemId);
            ids.add(lunchMenuItemId);
            ids.add(dinnerMenuItemId);
            ids.add(userId);

            int isSelected = feedbackDatabase.insertSelectedFoodItemsInDB(ids);
            out.println(isSelected == 1 ? "Selected food items successfully" : "Selected food items have some issues");
        } catch (NumberFormatException e) {
            out.println("Error: Invalid number format in input data. " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Unexpected error while selecting next day food items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewSelectedFoodItemsEmployees(PrintWriter out) {
        try {
            List<String> selectedFoodItemsByEmployees = feedbackDatabase.getSelectedFoodItemsEmployees();
            printMenu(out, selectedFoodItemsByEmployees, "----- Selected Food Items By Employees -----");
        } catch (SQLException e) {
            out.println("Database error while viewing selected food items by employees: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing selected food items by employees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewFinalizedMenu(PrintWriter out) {
        try {
            List<String> finalizedMenu = menuDatabase.getFinalizedMenu();
            printMenu(out, finalizedMenu, "----- Finalized Menu -----");
        } catch (SQLException e) {
            out.println("Database error while viewing finalized menu: " + e.getMessage());
            e.printStackTrace();
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing finalized menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printMenu(PrintWriter out, List<String> menuItems, String header) {
        try {
            if (menuItems.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println(header);
                out.println(String.format("%-15s %-20s %-10s %-15s %-15s", "Id", "Item", "Price", "Meal ID", "IsAvailable"));
                out.println("--------------------------------------------");
                for (String menuItem : menuItems) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
            }
            out.println("END");
        } catch (RuntimeException e) {
            out.println("Unexpected error while printing menu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void fillEmployeeImprovementAnswers(BufferedReader in, PrintWriter out, String[] parts) {
        int foodItemId = Integer.parseInt(parts[1]);
        int userId = Integer.parseInt(parts[2]);
        String ans1= parts[3];
        String ans2= parts[4];
        String ans3= parts[5];
        try {
            menuDatabase.getImprovementQuestionsandAnswers();
        } catch (RuntimeException e){
            out.println("Unexpected error while inserting Improvement Questions and Answers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewImprovementQuestionandAnswers(BufferedReader in, PrintWriter out) {
        try {
            List<String> improvementQuestionAnswers = menuDatabase.getImprovementQuestionsandAnswers();
            if (improvementQuestionAnswers.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println("----- Improvement Questions and Answers -----");
                out.println(String.format("%-50s %-50s %-50s %-50s %-50s", "FoodItemId", "UserId", "What didn't you like about Food?", "How would you like Food?", "Share your mom's recipe for Food?"));
                out.println("-----------------------------------------------------------------");
                for (String menuItem : improvementQuestionAnswers) {
                    out.println(menuItem);
                }
                out.println("-----------------------------------------------------------------");
            }
            out.println("END");
        } catch (RuntimeException e) {
            out.println("Unexpected error while viewing Improvement Questions and Answers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
