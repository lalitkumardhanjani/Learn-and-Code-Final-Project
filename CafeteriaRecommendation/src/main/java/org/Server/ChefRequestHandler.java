package org.Server;
import org.Services.ChefService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChefRequestHandler {
    public void handleRequest(String inputLine, BufferedReader inputReader, PrintWriter outputWriter) throws IOException {
        try {
            String[] requestParts = inputLine.split(":");
            ChefService chefService = new ChefService();
            System.out.println();
            switch (requestParts[1]) {
                case "viewMenu":
                    chefService.viewMenu(outputWriter);
                case "generateRecommendationMenu":
                    chefService.generateRecommendationMenu(outputWriter);
                    break;
                case "rolloutRecommendationMenu":
                    chefService.rolloutRecommendationMenu(outputWriter);
                    break;
                case "generateFinalizedMenu":
                    chefService.generateFinalizedMenu(outputWriter, requestParts);
                    break;
                case "rolloutFinalizedMenu":
                    chefService.rolloutFinalizedMenu(outputWriter);
                    break;
                case "viewSelectedFoodItemsEmployees":
                    chefService.viewSelectedFoodItemsEmployees(outputWriter);
                    break;
                case "viewFoodFeedbackHistory":
                    chefService.viewFoodFeedbackHistory(outputWriter);
                    break;
                case "viewDiscardMenuItems":
                    chefService.viewDiscardMenuItems(outputWriter);
                    break;
                case "sendImprovementQuestionsForDiscardFoodItems":
                    chefService.sendImprovementQuestionsForDiscardFoodItems(outputWriter);
                    break;
                case "getDiscardFoodItemIds":
                    chefService.getDiscardFoodItemIds(outputWriter);
                    break;
                case "viewImprovementQuestionandAnswers":
                    chefService.viewImprovementQuestionandAnswers(outputWriter);
                case "deleteMenuItem":
                    chefService.deleteMenuItem(outputWriter,requestParts);
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
