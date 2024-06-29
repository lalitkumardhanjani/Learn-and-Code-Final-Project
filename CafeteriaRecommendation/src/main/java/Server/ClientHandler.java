package Server;

import Database.Database;
import Database.SqlServerDatabase;
import Authentication.AuthenticationService;
import Authentication.UserAuthByCredentials;
import Services.MenuService;
import Services.FeedbackService;
import Services.NotificationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int userId = -1;
    private Database database = new SqlServerDatabase();
    private Map<Socket, Integer> activeUsers;

    public ClientHandler(Socket clientSocket, Map<Socket, Integer> activeUsers) {
        this.clientSocket = clientSocket;
        this.activeUsers = activeUsers;
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
                    handleLoginAndMenuCreation(in, out, inputLine);
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
            database.logLogoutAttempt(userId, true);
            out.println("Logged out.");
            activeUsers.remove(clientSocket);
            userId = -1;
        }
    }

    private void handleLoginAndMenuCreation(BufferedReader in, PrintWriter out, String inputLine) throws IOException, SQLException {
        System.out.println(inputLine);
        String[] parts = inputLine.split(":");
        switch (parts[0]) {
            case "login":
                System.out.println("Hi");
                handleLogin(out, parts);
                break;
            case "createMenuItem":
                new MenuService(database).handleMenuCreation(out, parts, activeUsers.get(clientSocket));
                break;
            case "updateMenuItem":
                new MenuService(database).handleUpdateMenuItem(out, parts, activeUsers.get(clientSocket));
                break;
            case "deleteMenuItem":
                new MenuService(database).handleDeleteMenuItem(out, parts, activeUsers.get(clientSocket));
                break;
            case "viewMenu":
                new MenuService(database).handleViewMenu(out, activeUsers.get(clientSocket));
                break;
            case "generateRecommendationMenu":
                new MenuService(database).generateRecommendation(out, activeUsers.get(clientSocket));
                break;
            case "rolloutRecommendationMenu":
                new MenuService(database).rolloutRecommendationMenu(out);
                break;
            case "generateFinalizedMenu":
                new MenuService(database).generateFinalizedMenu(in, out, parts);
                break;
            case "rolloutFinalizedMenu":
                new MenuService(database).rolloutFinalizedMenu(out);
                break;
            case "viewRecommendationMenu":
                new MenuService(database).viewRecommendationMenu(out);
                break;
            case "selectNextDayFoodItems":
                new MenuService(database).selectNextDayFoodItems(in, out, parts);
                break;
            case "viewSelectedFoodItemsEmployees":
                new MenuService(database).viewSelectedFoodItemsEmployees(out);
                break;
            case "giveFeedbackToAnyFoodItem":
                new FeedbackService(database).giveFeedbackToAnyFoodItem(in, out, parts);
                break;
            case "viewFoodFeedbackHistory":
                new FeedbackService(database).viewFoodFeedbackHistory(out);
                break;
            case "viewFinalizedMenu":
                new MenuService(database).viewFinalizedMenu(out);
                break;
            case "viewNotifications":
                new NotificationService(database).viewNotifications(out);
                break;
            default:
                out.println("Invalid input.");
                break;
        }
    }

    // Handle login request
    private void handleLogin(PrintWriter out, String[] parts) {
        int role = Integer.parseInt(parts[1]);
        int userId = Integer.parseInt(parts[2]);
        String password = parts[3];

        AuthenticationService auth = new UserAuthByCredentials(userId, password, role, database);
        if (auth.login()) {
            out.println("Login successful: " + (role == 1 ? "admin" : (role == 2) ? "chef" : "Employee"));
            this.userId = userId;
            activeUsers.put(clientSocket, userId);
        } else {
            out.println("Invalid credentials");
        }
    }

}
