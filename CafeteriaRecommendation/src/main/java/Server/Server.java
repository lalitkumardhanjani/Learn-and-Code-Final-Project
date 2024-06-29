package Server;

import Authentication.AuthenticationService;
import Authentication.*;
import Database.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    private static Map<Socket, Integer> activeUsers = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from port " + clientSocket.getPort());

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int userId = -1;
        private Database database = new SqlServerDatabase();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                handleClient(clientSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleClient(Socket clientSocket) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("logout".equals(inputLine)) {
                        handleLogout(out);
                    } else {
                        handleLoginAndMenuCreation(in,out, inputLine);
                    }
                }
            } catch (SocketException e) {
                System.err.println("Client disconnected unexpectedly: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Client disconnected from port " + clientSocket.getPort());
            }
        }

        private void handleLogout(PrintWriter out) {
            if (activeUsers.get(clientSocket) == null) {
                out.println("not logged in");
            } else {
                database.logLogoutAttempt(userId, true);
                out.println("Logged out.");
                activeUsers.remove(clientSocket);
                userId = -1;
            }
        }

        private void handleLoginAndMenuCreation(BufferedReader in, PrintWriter out, String inputLine) throws IOException, SQLException {
            String[] parts = inputLine.split(":");
            if (parts.length == 3) {
                handleLogin(out, parts);
            } else if (parts.length == 5 && "createMenuItem".equals(parts[0])) {
                handleMenuCreation(out, parts);
            } else if (parts.length == 1 && "viewMenu".equals(parts[0])) {
                handleViewMenu(out);
            } else if (parts.length == 5 && "updateMenuItem".equals(parts[0])) {
                handleUpdateMenuItem(out, parts);
            } else if (parts.length ==2 && "deleteMenuItem".equals(parts[0])) {
                handleDeleteMenuItem(out, parts);
            } else if (parts.length == 1 && "generateRecommendationMenu".equals(parts[0])){
                generateRecommendation(out);
            } else if (parts.length==1 && "rolloutRecommendationMenu".equals(parts[0])) {
                rolloutRecommendationMenu(out);
            } else if (parts.length==4 && "generateFinalizedMenu".equals(parts[0])){
                generateFinalizedMenu(in,out,parts);
            } else if (parts.length ==1 && "rolloutFinalizedMenu".equals(parts[0])){
                rolloutFinalizedMenu(out);
            } else if (parts.length ==1 && "viewRecommendationMenu".equals(parts[0])){
                viewRecommendationMenu(out);
            } else if (parts.length ==5 && "selectNextDayFoodItems".equals(parts[0])){
                selectNextDayFoodItems(in,out,parts);
            } else if (parts.length == 1 && "viewSelectedFoodItemsEmployees".equals((parts[0]))){
                viewSelectedFoodItemsEmployees(out);
            } else if (parts.length == 5 && "giveFeedbackToAnyFoodItem".equals((parts[0]))){
                giveFeedbackToAnyFoodItem(in,out,parts);
            } else if (parts.length ==1 && "viewFoodFeedbackHistory".equals(parts[0])) {
                viewFoodFeedbackHistory(out);
            } else if (parts.length ==1 && "viewFinalizedMenu".equals(parts[0])) {
                viewFinalizedMenu(out);
            } else if (parts.length == 1 && "viewNotifications".equals(parts[0])) {
                viewNotifications(out);
            }
            else {
                out.println("Invalid input.");
            }
        }

        private void viewNotifications(PrintWriter out) {
            List<String> notifications = database.getNotifications();
            if (notifications.isEmpty()) {
                out.println("No notifications are present");
            } else {
                out.println("-------------- Notifications -------------");
                out.println("------------------------------------------");

                for (String menuItem : notifications) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
                out.println("END");
            }
        }

        private void viewFinalizedMenu(PrintWriter out) throws SQLException {
            List<String> finalizedMenu = database.getFinalizedMenu();
            if (finalizedMenu.isEmpty()) {
                out.println("No menu items available.");
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

        private void viewFoodFeedbackHistory(PrintWriter out) throws SQLException {
            List<String> foodFeedbackHistory = database.getFoodFeedbackHistory();
            if (foodFeedbackHistory.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println("----- Feedback Food History By Employees-----");
                out.println(String.format("%-15s %-20s %-10s %-15s %-20s %-10s %-15s %-20s %-10s", "Id", "UserId", "UserName","FoodItemId","FoodItemName","MealType","Rating","Comments","DateTime"));
                out.println("--------------------------------------------");

                for (String menuItem : foodFeedbackHistory) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
                out.println("END");
            }
        }

        private void giveFeedbackToAnyFoodItem(BufferedReader in, PrintWriter out,String [] parts){
            int foodItemId = Integer.parseInt(parts[1]);
            int rating = Integer.parseInt((parts[2]));
            String comment = parts[3];
            int userId = Integer.parseInt((parts[4]));
            int isFeedbackSubmitted = database.giveFoodFeedback(foodItemId,rating,comment,userId);
            if(isFeedbackSubmitted == 1){
                out.println("Feedback Submitted Successfully");
            }else{
                out.println("Feedback Submission have an issue");
            }

        }
        private void viewSelectedFoodItemsEmployees(PrintWriter out) throws SQLException {
            List<String> selectedFoodItemsByEmployees = database.getSelectedFoodItemsEmployees();
            if (selectedFoodItemsByEmployees.isEmpty()) {
                out.println("No menu items available.");
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

        private void selectNextDayFoodItems(BufferedReader in,PrintWriter out,String [] parts){
            int breakfastMenuItemId =Integer.parseInt(parts[1]);
            int lunchMenuItemId = Integer.parseInt(parts[2]);
            int dinnerMenuItemId = Integer.parseInt(parts[3]);
            int userId = Integer.parseInt(parts[4]);
            List<Integer> Ids = new ArrayList<>();
            Ids.add(breakfastMenuItemId);
            Ids.add(lunchMenuItemId);
            Ids.add(dinnerMenuItemId);
            Ids.add(userId);
            int isSelected = database.insertSelectedFoodItemsInDB(in,out,Ids);
            if(isSelected==1){
                out.println("Selected food items successfully");
            }else{
                out.println("Selected food items have some issue");
            }
        }
        private void viewRecommendationMenu(PrintWriter out) throws SQLException {
            List<String> getrecommendationMenu = database.getRecommendedMenu();
            if (getrecommendationMenu.isEmpty()) {
                out.println("No menu items available.");
            } else {
                out.println("----- Recommended Cafeteria Menu -----");
                out.println(String.format("%-15s %-20s %-10s %-15s", "Id", "Item", "Price", "MealType"));
                out.println("--------------------------------------------");

                for (String menuItem : getrecommendationMenu) {
                    out.println(menuItem);
                }
                out.println("--------------------------------------------");
                out.println("END");
            }
        }

        private  void generateFinalizedMenu(BufferedReader in,PrintWriter out,String [] parts) throws SQLException {
            int breakfastMenuItemId =Integer.parseInt(parts[1]);
            int lunchMenuItemId = Integer.parseInt(parts[2]);
            int dinnerMenuItemId = Integer.parseInt(parts[3]);

            List<String> finalizedMenu = database.getFinalizedMenu(breakfastMenuItemId,lunchMenuItemId,dinnerMenuItemId);
            if (finalizedMenu.isEmpty()) {
                out.println("No menu items available.");
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

        private void rolloutFinalizedMenu(PrintWriter out){
            int isRollout=database.rolloutFinalizedMenusStatusUpdate();
            if(isRollout==1){
                out.println("Roll out successfully");
            }else{
                out.println("Roll out have some issue");
            }
        }
        private void rolloutRecommendationMenu(PrintWriter out){
            int isRollout=database.rolloutRecommendation();
            if(isRollout==1){
                out.println("Roll out successfully");
            }else{
                out.println("Roll out have some issue");
            }
        }

        private void handleDeleteMenuItem(PrintWriter out,String [] parts){
            int menuId = Integer.parseInt(parts[1]);
            System.out.println(menuId);
            if (activeUsers.get(clientSocket) != null) {
                boolean isDelete = database.deleteMenuItem(menuId);
                if(isDelete) {
                    out.println("Menu item deleted successfully.");
                }
            } else {
                out.println("Not authorized.");
            }
        }

        private void handleLogin(PrintWriter out, String[] parts) {
            int role = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            String password = parts[2];

            AuthenticationService auth = new UserAuthByCredentials(userId, password, role, database);
            if (auth.login()) {
                out.println("Login successful: " + (role == 1 ? "admin" : (role==2) ? "chef" : "Employee"));
                this.userId = userId;
                activeUsers.put(clientSocket, userId);
            } else {
                out.println("Invalid credentials");
            }
        }

        private void handleMenuCreation(PrintWriter out, String[] parts) {
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            Integer mealType = Integer.parseInt(parts[3]);
            int availability = Integer.parseInt(parts[4]);

            if (activeUsers.get(clientSocket) != null) {
                database.createMenuItem(name, price, mealType, availability);
                out.println("Menu item created successfully.");
            } else {
                out.println("Not authorized.");
            }
        }

        private void handleUpdateMenuItem(PrintWriter out, String[] parts) {
            if (parts.length < 5) {
                out.println("Invalid input for updating menu item.");
                return;
            }

            int menuId = Integer.parseInt(parts[1]);
            String newName = parts[2];
            Double newPrice = Double.parseDouble(parts[3]);
            int newAvailability = Integer.parseInt(parts[4]);

            if (activeUsers.get(clientSocket) != null) {
                database.updateMenuItem(menuId, newName, newPrice, newAvailability);
                out.println("Menu item updated successfully.");
            } else {
                out.println("Not authorized.");
            }
        }

        private  void generateRecommendation(PrintWriter out){
            if(activeUsers.get(clientSocket)!=null){
                List<String> recommendedMenuItems =database.generateRecommendedMenu();
                if (recommendedMenuItems.isEmpty()) {
                    out.println("No menu items available.");
                } else {
                    out.println("----- Recommended Cafeteria Menu -----");
                    out.println(String.format("%-15s %-20s %-10s %-15s","Id", "Item", "Price", "MealType"));
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

        private void handleViewMenu(PrintWriter out) {
            if (activeUsers.get(clientSocket) != null) {
                // Retrieve the menu items from the database and send them to the client
                List<String> menuItems = database.getMenuItems();

                if (menuItems.isEmpty()) {
                    out.println("No menu items available.");
                } else {
                    out.println("----- Cafeteria Menu -----");
                    out.println(String.format("%-15s %-20s %-10s %-15s %-15s","Id", "Item", "Price", "Meal ID","IsAvailable"));
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



    }
}
