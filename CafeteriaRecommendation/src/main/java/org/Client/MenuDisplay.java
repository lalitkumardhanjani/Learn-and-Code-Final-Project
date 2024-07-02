package org.Client;

public class MenuDisplay {
    public static void displayMainMenu() {
        System.out.println("Welcome to Cafeteria Management System");
        System.out.println("Select your role:");
        System.out.println("1. Admin");
        System.out.println("2. Chef");
        System.out.println("3. Employee");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void displayAdminMenu() {
        System.out.println("Admin Menu:");
        System.out.println("1. Create Menu Item");
        System.out.println("2. View Menu Items");
        System.out.println("3. Update Menu Item");
        System.out.println("4. Delete Menu Item");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
    }

    public static void displayChefMenu() {
        System.out.println("Chef Menu:");
        System.out.println("1. View Menu");
        System.out.println("2. Generate Food Recommendation Menu");
        System.out.println("3. Rollout Food Recommendation Menu");
        System.out.println("4. View Selected Food Items by Employees");
        System.out.println("5. Generate Finalized Menu");
        System.out.println("6. Rollout Finalized Menu");
        System.out.println("7. View Food Feedback History By Employees");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");
    }

    public static void displayEmployeeMenu() {
        System.out.println("Employee Menu:");
        System.out.println("1. View recommendation Menu");
        System.out.println("2. Select Food items from Recommendation Menu");
        System.out.println("3. Give Feedback to any food item");
        System.out.println("4. View Finalized Menu");
        System.out.println("5. View Notifications");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
    }
}
