package Services;

import Database.Database;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private Database database;

    public MenuService(Database database) {
        this.database = database;
    }

    public void handleMenuCreation(PrintWriter out, String[] parts, Integer userId) {
        String name = parts[1];
        double price = Double.parseDouble(parts[2]);
        Integer mealType = Integer.parseInt(parts[3]);
        int availability = Integer.parseInt(parts[4]);

        if (userId != null) {
            database.createMenuItem(name, price, mealType, availability);
            out.println("Menu item created successfully.");
        } else {
            out.println("Not authorized.");
        }
    }

    public void handleUpdateMenuItem(PrintWriter out, String[] parts, Integer userId) {
        if (parts.length < 5) {
            out.println("Invalid input for updating menu item.");
            return;
        }

        int menuId = Integer.parseInt(parts[1]);
        String newName = parts[2];
        Double newPrice = Double.parseDouble(parts[3]);
        int newAvailability = Integer.parseInt(parts[4]);

        if (userId != null) {
            database.updateMenuItem(menuId, newName, newPrice, newAvailability);
            out.println("Menu item updated successfully.");
        } else {
            out.println("Not authorized.");
        }
    }

    public void handleDeleteMenuItem(PrintWriter out, String[] parts, Integer userId) {
        int menuId = Integer.parseInt(parts[1]);
        if (userId != null) {
            boolean isDelete = database.deleteMenuItem(menuId);
            if (isDelete) {
                out.println("Menu item deleted successfully.");
            }
        } else {
            out.println("Not authorized.");
        }
    }

    public void handleViewMenu(PrintWriter out, Integer userId) {
        if (userId != null) {
            List<String> menuItems = database.getMenuItems();

            if (menuItems.isEmpty()) {
                out.println("No menu items available.");
                out.println("END");
            } else {
                out.println("----- Cafeteria Menu -----");
                out.println(String.format("%-15s %-20s %-10s %-15s %-15s", "Id", "Item", "Price", "Meal ID", "IsAvailable"));
                out.println("--------------------------------------------");

                for (String menuItem : menuItems) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
                out.println("END");
            }
        } else {
            out.println("Not authorized.");
        }
    }

    public void generateRecommendation(PrintWriter out, Integer userId) {
        if (userId != null) {
            List<String> recommendedMenuItems = database.generateRecommendedMenu();
            if (recommendedMenuItems.isEmpty()) {
                out.println("No menu items available.");
                out.println("END");
            } else {
                out.println("----- Recommended Cafeteria Menu -----");
                out.println(String.format("%-15s %-20s %-10s %-15s", "Id", "Item", "Price", "MealType"));
                out.println("--------------------------------------------");

                for (String menuItem : recommendedMenuItems) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
                out.println("END");
            }
        } else {
            out.println("Not authorized.");
        }
    }

    public void rolloutRecommendationMenu(PrintWriter out) {
        int isRollout = database.rolloutRecommendation();
        if (isRollout == 1) {
            out.println("Roll out successfully");
        } else {
            out.println("Roll out have some issue");
        }
    }

    public void generateFinalizedMenu(BufferedReader in, PrintWriter out, String[] parts) throws SQLException {
        int breakfastMenuItemId = Integer.parseInt(parts[1]);
        int lunchMenuItemId = Integer.parseInt(parts[2]);
        int dinnerMenuItemId = Integer.parseInt(parts[3]);

        List<String> finalizedMenu = database.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
        if (finalizedMenu.isEmpty()) {
            out.println("No menu items available.");
            out.println("END");
        } else {
            out.println("----- Finalized Cafeteria Menu -----");
            out.println(String.format("%-15s %-20s %-10s %-15s", "Id", "Item", "Price", "MealType"));
            out.println("--------------------------------------------");

            for (String menuItem : finalizedMenu) {
                out.println(menuItem);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }

    public void rolloutFinalizedMenu(PrintWriter out) {
        int isRollout = database.rolloutFinalizedMenusStatusUpdate();
        if (isRollout == 1) {
            out.println("Roll out successfully");
        } else {
            out.println("Roll out have some issue");
        }
    }

    public void viewRecommendationMenu(PrintWriter out) throws SQLException {
        List<String> getRecommendationMenu = database.getRecommendedMenu();
        if (getRecommendationMenu.isEmpty()) {
            out.println("No menu items available.");
            out.println("END");
        } else {
            out.println("----- Recommended Cafeteria Menu -----");
            out.println(String.format("%-15s %-20s %-10s %-15s", "Id", "Item", "Price", "MealType"));
            out.println("--------------------------------------------");

            for (String menuItem : getRecommendationMenu) {
                out.println(menuItem);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }

    public void selectNextDayFoodItems(BufferedReader in, PrintWriter out, String[] parts) {
        int breakfastMenuItemId = Integer.parseInt(parts[1]);
        int lunchMenuItemId = Integer.parseInt(parts[2]);
        int dinnerMenuItemId = Integer.parseInt(parts[3]);
        int userId = Integer.parseInt(parts[4]);
        List<Integer> Ids = new ArrayList<>();
        Ids.add(breakfastMenuItemId);
        Ids.add(lunchMenuItemId);
        Ids.add(dinnerMenuItemId);
        Ids.add(userId);
        int isSelected = database.insertSelectedFoodItemsInDB(in, out, Ids);
        if (isSelected == 1) {
            out.println("Selected food items successfully");
        } else {
            out.println("Selected food items have some issue");
        }
    }

    public void viewSelectedFoodItemsEmployees(PrintWriter out) throws SQLException {
        List<String> selectedFoodItemsByEmployees = database.getSelectedFoodItemsEmployees();
        if (selectedFoodItemsByEmployees.isEmpty()) {
            out.println("No menu items available.");
            out.println("END");
        } else {
            out.println("----- Selected Food Items By Employees-----");
            out.println(String.format("%-15s %-20s %-10s", "FoodItemId", "Name", "VoteCount"));
            out.println("--------------------------------------------");

            for (String menuItem : selectedFoodItemsByEmployees) {
                out.println(menuItem);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }

    public void viewFinalizedMenu(PrintWriter out) throws SQLException {
        List<String> finalizedMenu = database.getFinalizedMenu();
        if (finalizedMenu.isEmpty()) {
            out.println("No menu items available.");
            out.println("END");
        } else {
            out.println("----- Finalized Menu-----");
            out.println(String.format("%-15s %-20s %-10s ", "Id", "FoodId", "Name"));
            out.println("--------------------------------------------");

            for (String menuItem : finalizedMenu) {
                out.println(menuItem);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }
}
