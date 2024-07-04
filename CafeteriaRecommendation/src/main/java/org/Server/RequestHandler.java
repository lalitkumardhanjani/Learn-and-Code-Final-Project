package org.Server;

import org.Database.MenuManagementDatabase;
import org.Database.FeedbackDatabase;
import org.Database.NotificationDatabase;
import org.Database.SqlServerDatabase;
import org.Services.FeedbackService;
import org.Services.MenuService;
import org.Services.NotificationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class RequestHandler {
    private final MenuManagementDatabase menuDatabase;
    private final FeedbackDatabase feedbackDatabase;
    private final NotificationDatabase notificationDatabase;
    private final Map<Socket, Integer> activeUsers;

    public RequestHandler(SqlServerDatabase sqlDatabase, Map<Socket, Integer> activeUsers) {
        this.menuDatabase = sqlDatabase;
        this.feedbackDatabase = sqlDatabase;
        this.notificationDatabase = sqlDatabase;
        this.activeUsers = activeUsers;
    }

    public void handleRequest(String inputLine, BufferedReader in, PrintWriter out, Socket clientSocket) throws SQLException, IOException {
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
                case "viewDiscardMenuItems":
                    menuService.viewDiscardMenuItems(out);
                    break;
                case "sendImprovementQuestionsForDiscardFoodItems":
                    menuService.sendImprovementQuestionsForDiscardFoodItems(out);
                    break;
                case "getDiscardFoodItemIds":
                    menuService.getDiscardFoodItemIds(out);
                    break;
                case "improvementAnswers":
                    menuService.fillEmployeeImprovementAnswers(in,out,parts);
                    break;
                case "viewImprovementQuestionandAnswers":
                    menuService.viewImprovementQuestionandAnswers(in,out);
                default:
                    out.println("Invalid input.");
                    break;
            }
        } catch (Exception e) {
            out.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
