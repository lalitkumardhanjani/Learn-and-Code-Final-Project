package org.Services;

import org.Database.MenuManagementDatabase;
import org.Database.FeedbackDatabase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private final MenuManagementDatabase menuDatabase;
    private final FeedbackDatabase feedbackDatabase;

    public MenuService(MenuManagementDatabase menuDatabase, FeedbackDatabase feedbackDatabase) {
        this.menuDatabase = menuDatabase;
        this.feedbackDatabase = feedbackDatabase;
    }

    public void handleMenuCreation(PrintWriter out, String[] parts, Integer userId) {
        if (userId == null) {
            out.println("Not authorized.");
            return;
        }

        String name = parts[1];
        double price = Double.parseDouble(parts[2]);
        Integer mealType = Integer.parseInt(parts[3]);
        int availability = Integer.parseInt(parts[4]);

        menuDatabase.createMenuItem(name, price, mealType, availability);
        out.println("Menu item created successfully.");
    }

    public void handleUpdateMenuItem(PrintWriter out, String[] parts, Integer userId) {
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
    }

    public void handleDeleteMenuItem(PrintWriter out, String[] parts, Integer userId) {
        if (userId == null) {
            out.println("Not authorized.");
            return;
        }

        int menuId = Integer.parseInt(parts[1]);
        boolean isDeleted = menuDatabase.deleteMenuItem(menuId);
        if (isDeleted) {
            out.println("Menu item deleted successfully.");
        } else {
            out.println("Failed to delete menu item.");
        }
    }

    public void handleViewMenu(PrintWriter out, Integer userId) {
        if (userId == null) {
            out.println("Not authorized.");
            return;
        }

        List<String> menuItems = menuDatabase.getMenuItems();
        printMenu(out, menuItems, "----- Cafeteria Menu -----");
    }

    public void generateRecommendation(PrintWriter out, Integer userId) {
        if (userId == null) {
            out.println("Not authorized.");
            return;
        }

        List<String> recommendedMenuItems = menuDatabase.generateRecommendedMenu();
        printMenu(out, recommendedMenuItems, "----- Recommended Cafeteria Menu -----");
    }

    public void rolloutRecommendationMenu(PrintWriter out) {
        int isRollout = menuDatabase.rolloutRecommendation();
        out.println(isRollout == 1 ? "Roll out successfully" : "Roll out have some issue");
    }

    public void generateFinalizedMenu(PrintWriter out, String[] parts) throws SQLException {
        int breakfastMenuItemId = Integer.parseInt(parts[1]);
        int lunchMenuItemId = Integer.parseInt(parts[2]);
        int dinnerMenuItemId = Integer.parseInt(parts[3]);

        List<String> finalizedMenu = menuDatabase.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
        printMenu(out, finalizedMenu, "----- Finalized Cafeteria Menu -----");
    }

    public void rolloutFinalizedMenu(PrintWriter out) {
        int isRollout = menuDatabase.rolloutFinalizedMenusStatusUpdate();
        out.println(isRollout == 1 ? "Roll out successfully" : "Roll out have some issue");
    }

    public void viewRecommendationMenu(PrintWriter out) throws SQLException {
        List<String> recommendedMenu = menuDatabase.getRecommendedMenu();
        printMenu(out, recommendedMenu, "----- Recommended Cafeteria Menu -----");
    }

    public void selectNextDayFoodItems(PrintWriter out, String[] parts) {
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
        if (isSelected == 1) {
            out.println("Selected food items successfully");
        } else {
            out.println("Selected food items have some issue");
        }
    }

    public void viewSelectedFoodItemsEmployees(PrintWriter out) throws SQLException {
        List<String> selectedFoodItemsByEmployees = feedbackDatabase.getSelectedFoodItemsEmployees();
        printMenu(out, selectedFoodItemsByEmployees, "----- Selected Food Items By Employees -----");
    }

    public void viewFinalizedMenu(PrintWriter out) throws SQLException {
        // Assuming getFinalizedMenu is intended to be called on menuDatabase
        List<String> finalizedMenu = menuDatabase.getFinalizedMenu();
        printMenu(out, finalizedMenu, "----- Finalized Menu -----");
    }

    private void printMenu(PrintWriter out, List<String> menuItems, String header) {
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
    }
}
