package org.Server;

import org.Database.IMenuManagementDatabase;
import org.Database.IFeedbackDatabase;
import org.Database.INotificationDatabase;
import org.Database.SqlServerDatabase;
import org.Services.FeedbackService;
import org.Services.MenuService;
import org.Services.NotificationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
public class RequestHandler {
    private final IMenuManagementDatabase menuManagementDatabase;
    private final IFeedbackDatabase feedbackDatabase;
    private final INotificationDatabase notificationDatabase;

    public RequestHandler(SqlServerDatabase sqlServerDatabase) {
        this.menuManagementDatabase = sqlServerDatabase;
        this.feedbackDatabase = sqlServerDatabase;
        this.notificationDatabase = sqlServerDatabase;
    }

    public void handleRequest(String inputLine, BufferedReader inputReader, PrintWriter outputWriter) throws IOException {
        try {
            String[] requestParts = inputLine.split(":");
            MenuService menuService = new MenuService(menuManagementDatabase, feedbackDatabase);
            FeedbackService feedbackService = new FeedbackService(feedbackDatabase);
            NotificationService notificationService = new NotificationService(notificationDatabase);

            switch (requestParts[0]) {
                case "createMenuItem":
                    menuService.processMenuCreation(outputWriter, requestParts);
                    break;
                case "updateMenuItem":
                    menuService.processMenuItemUpdate(outputWriter, requestParts);
                    break;
                case "deleteMenuItem":
                    menuService.processMenuItemDeletion(outputWriter, requestParts);
                    break;
                case "viewMenu":
                    menuService.viewMenu(outputWriter);
                    break;
                case "generateRecommendationMenu":
                    menuService.generateRecommendationMenu(outputWriter);
                    break;
                case "rolloutRecommendationMenu":
                    menuService.rolloutRecommendationMenu(outputWriter);
                    break;
                case "generateFinalizedMenu":
                    menuService.generateFinalizedMenu(outputWriter, requestParts);
                    break;
                case "rolloutFinalizedMenu":
                    menuService.rolloutFinalizedMenu(outputWriter);
                    break;
                case "viewRecommendationMenu":
                    menuService.viewRecommendationMenu(outputWriter, requestParts);
                    break;
                case "selectNextDayFoodItems":
                    menuService.selectNextDayFoodItems(outputWriter, requestParts);
                    break;
                case "viewSelectedFoodItemsEmployees":
                    menuService.viewSelectedFoodItemsEmployees(outputWriter);
                    break;
                case "giveFeedbackToAnyFoodItem":
                    feedbackService.submitFoodFeedback(outputWriter, requestParts);
                    break;
                case "viewFoodFeedbackHistory":
                    feedbackService.viewFoodFeedbackHistory(outputWriter);
                    break;
                case "viewFinalizedMenu":
                    menuService.viewFinalizedMenu(outputWriter);
                    break;
                case "viewNotifications":
                    notificationService.viewNotifications(outputWriter);
                    break;
                case "viewDiscardMenuItems":
                    menuService.viewDiscardMenuItems(outputWriter);
                    break;
                case "sendImprovementQuestionsForDiscardFoodItems":
                    menuService.sendImprovementQuestionsForDiscardFoodItems(outputWriter);
                    break;
                case "getDiscardFoodItemIds":
                    menuService.getDiscardFoodItemIds(outputWriter);
                    break;
                case "improvementAnswers":
                    menuService.fillEmployeeImprovementAnswers(inputReader, outputWriter, requestParts);
                    break;
                case "viewImprovementQuestionandAnswers":
                    menuService.viewImprovementQuestionAndAnswers(inputReader, outputWriter);
                    break;
                case "makeEmployeeProfile":
                    menuService.makeEmployeeProfile(inputReader, outputWriter, requestParts);
                    break;
                default:
                    outputWriter.println("Invalid input.");
                    break;
            }
        } catch (Exception exception) {
            outputWriter.println("Error processing request: " + exception.getMessage());
        }
    }
}
