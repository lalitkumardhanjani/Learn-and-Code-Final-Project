package org.Services;

import org.Database.IFeedbackDatabase;
import org.Database.IMenuManagementDatabase;
import org.Database.SqlServerDatabase;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class AdminService {

    private static SqlServerDatabase sqlServerDatabase;

    public AdminService() {
        this.sqlServerDatabase = new SqlServerDatabase();
    }
    public static void processMenuItemCreation(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 5) {
                outputWriter.println("Invalid input for creating menu item.");
                return;
            }
            sqlServerDatabase.createMenuItem(menuItemData);
            outputWriter.println("Menu item created successfully.");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while creating menu item: " + runtimeException.getMessage());
        }
    }

    public static void processMenuItemUpdate(PrintWriter outputWriter, String[] menuItemData) {
        try {
            if (menuItemData.length < 5) {
                outputWriter.println("Invalid input for updating menu item.");
                return;
            }

            int menuItemId = Integer.parseInt(menuItemData[2]);
            String updatedName = menuItemData[3];
            double updatedPrice = Double.parseDouble(menuItemData[4]);
            int updatedAvailability = Integer.parseInt(menuItemData[5]);

            sqlServerDatabase.updateMenuItem(menuItemId, updatedName, updatedPrice, updatedAvailability);
            outputWriter.println("Menu item updated successfully.");
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while updating menu item: " + runtimeException.getMessage());
        }
    }

    public static void processMenuItemDeletion(PrintWriter outputWriter, String[] menuItemData) {
        try {
            int menuItemId = Integer.parseInt(menuItemData[2]);
            boolean isDeleted = sqlServerDatabase.deleteMenuItem(menuItemId);
            if (isDeleted) {
                outputWriter.println("Menu item deleted successfully.");
            } else {
                outputWriter.println("Failed to delete menu item.");
            }
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error: Invalid number format in input data. " + numberFormatException.getMessage());
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while deleting menu item: " + runtimeException.getMessage());
        }
    }

    public static void viewMenu(PrintWriter outputWriter) {
        try {
            List<String> menuItems = sqlServerDatabase.getMenuItems();
            printMenu(outputWriter, menuItems, "--------------- Cafeteria Menu ---------------");
        } catch (RuntimeException runtimeException) {
            outputWriter.println("Unexpected error while viewing menu: " + runtimeException.getMessage());
        }
    }

    private static void printMenu(PrintWriter outputWriter, List<String> menuItems, String header) {
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

    public static void setSqlServerDatabase(SqlServerDatabase sqlServerDatabase) {
        AdminService.sqlServerDatabase = sqlServerDatabase;
    }
}
