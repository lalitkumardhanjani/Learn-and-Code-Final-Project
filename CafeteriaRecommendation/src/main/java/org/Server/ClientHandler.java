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
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    if ("logout".equals(inputLine)) {
                        handleLogout(out);
                    } else if ("login".equals(inputLine.split(":")[0])) {
                        handleLogin(out, inputLine.split(":"));
                    } else {
                        handleRequest(inputLine, in, out);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    out.println("Invalid input format: " + e.getMessage());
                } catch (NumberFormatException e) {
                    out.println("Number format error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    out.println("Illegal argument: " + e.getMessage());
                } catch (SQLException e) {
                    out.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    out.println("I/O error: " + e.getMessage());
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    out.println("Runtime exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            System.err.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error occurred while handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("Client disconnected from port " + clientSocket.getPort());
        }
    }

    private void handleLogout(PrintWriter out) {
        try {
            if (activeUsers.get(clientSocket) == null) {
                out.println("not logged in");
            } else {
                authDatabase.logLogoutAttempt(userId, true);
                out.println("Logout successful");
                activeUsers.remove(clientSocket);
                userId = -1;
            }
        } catch (RuntimeException e) {
            out.println("Runtime error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogin(PrintWriter out, String[] parts) {
        try {
            int role = Integer.parseInt(parts[1]);
            int userId = Integer.parseInt(parts[2]);
            String password = parts[3];
            System.out.println(role);
            System.out.println(userId);
            System.out.println(password);
            AuthenticationService auth = new UserAuthByCredentials(userId, password, role, authDatabase);
            if (auth.login()) {
                out.println("Login successful: " + (role == 1 ? "admin" : (role == 2) ? "chef" : "Employee"));
                this.userId = userId;
                activeUsers.put(clientSocket, userId);
            } else {
                out.println("Invalid credentials");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            out.println("Login input format error: " + e.getMessage());
        } catch (NumberFormatException e) {
            out.println("Invalid number format for role or userId: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            out.println("Illegal argument during login: " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Runtime exception during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRequest(String inputLine, BufferedReader in, PrintWriter out) throws SQLException, IOException {
        try {
            String[] parts = inputLine.split(":");
            MenuService menuService = new MenuService(menuDatabase, feedbackDatabase);
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
        } catch (ArrayIndexOutOfBoundsException e) {
            out.println("Request input format error: " + e.getMessage());
        } catch (NumberFormatException e) {
            out.println("Number format error in request: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            out.println("Illegal argument in request: " + e.getMessage());
        } catch (RuntimeException e) {
            out.println("Runtime exception during request handling: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
