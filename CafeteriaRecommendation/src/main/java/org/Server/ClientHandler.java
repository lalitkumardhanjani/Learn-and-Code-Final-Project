package org.Server;

import org.Authentication.AuthenticationService;
import org.Authentication.UserAuthByCredentials;
import org.Database.AuthenticationDatabase;
import org.Database.FeedbackDatabase;
import org.Database.MenuManagementDatabase;
import org.Database.NotificationDatabase;
import org.Database.SqlServerDatabase;
import org.Services.FeedbackService;
import org.Services.MenuService;
import org.Services.NotificationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int userId = -1;
    private AuthenticationDatabase authDatabase;
    private MenuManagementDatabase menuDatabase;
    private FeedbackDatabase feedbackDatabase;
    private NotificationDatabase notificationDatabase;
    private Map<Socket, Integer> activeUsers;

    public ClientHandler(Socket clientSocket, Map<Socket, Integer> activeUsers) {
        this.clientSocket = clientSocket;
        this.activeUsers = activeUsers;
        SqlServerDatabase sqlDatabase = new SqlServerDatabase();
        this.authDatabase = sqlDatabase;
        this.menuDatabase = sqlDatabase;
        this.feedbackDatabase = sqlDatabase;
        this.notificationDatabase = sqlDatabase;
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
                } else if ("login".equals(inputLine.split(":")[0])) {
                    handleLogin(out, inputLine.split(":"));
                } else {
                    handleRequest(inputLine, in, out);
                }
            }
        } catch (SocketException e) {
            System.err.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
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
            authDatabase.logLogoutAttempt(userId, true);
            out.println("Logged out.");
            activeUsers.remove(clientSocket);
            userId = -1;
        }
    }

    private void handleLogin(PrintWriter out, String[] parts) {
        int role = Integer.parseInt(parts[1]);
        int userId = Integer.parseInt(parts[2]);
        String password = parts[3];

        AuthenticationService auth = new UserAuthByCredentials(userId, password, role, authDatabase);
        if (auth.login()) {
            out.println("Login successful: " + (role == 1 ? "admin" : (role == 2) ? "chef" : "Employee"));
            this.userId = userId;
            activeUsers.put(clientSocket, userId);
        } else {
            out.println("Invalid credentials");
        }
    }

    private void handleRequest(String inputLine, BufferedReader in, PrintWriter out) throws SQLException, IOException {
        String[] parts = inputLine.split(":");
        MenuService menuService = new MenuService(menuDatabase, feedbackDatabase); // Provide both databases

        FeedbackService feedbackService = new FeedbackService(feedbackDatabase);
        NotificationService notificationService = new NotificationService(notificationDatabase);
        Integer activeUserId = activeUsers.get(clientSocket);

        switch (parts[0]) {
            case "createMenuItem":
                menuService.handleMenuCreation(out, parts, activeUserId);
                break;
            case "updateMenuItem":
                menuService.handleUpdateMenuItem(out, parts, activeUserId);
                break;
            case "deleteMenuItem":
                menuService.handleDeleteMenuItem(out, parts, activeUserId);
                break;
            case "viewMenu":
                menuService.handleViewMenu(out, activeUserId);
                break;
            case "generateRecommendationMenu":
                menuService.generateRecommendation(out, activeUserId);
                break;
            case "rolloutRecommendationMenu":
                menuService.rolloutRecommendationMenu(out);
                break;
            case "generateFinalizedMenu":
                menuService.generateFinalizedMenu(out, parts);
                break;
            case "rolloutFinalizedMenu":
                menuService.rolloutFinalizedMenu(out);
                break;
            case "viewRecommendationMenu":
                menuService.viewRecommendationMenu(out);
                break;
            case "selectNextDayFoodItems":
                menuService.selectNextDayFoodItems(out, parts);
                break;
            case "viewSelectedFoodItemsEmployees":
                menuService.viewSelectedFoodItemsEmployees(out);
                break;
            case "giveFeedbackToAnyFoodItem":
                feedbackService.submitFoodFeedback(in, out, parts);
                break;
            case "viewFoodFeedbackHistory":
                feedbackService.displayFoodFeedbackHistory(out);
                break;
            case "viewFinalizedMenu":
                menuService.viewFinalizedMenu(out);
                break;
            case "viewNotifications":
                notificationService.viewNotifications(out);
                break;
            default:
                out.println("Invalid input.");
                break;
        }
    }
}
