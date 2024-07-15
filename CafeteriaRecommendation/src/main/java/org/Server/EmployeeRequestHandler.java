package org.Server;
import org.Services.EmployeeService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EmployeeRequestHandler {
    public void handleRequest(String inputLine, BufferedReader inputReader, PrintWriter outputWriter) throws IOException {
        try {
            String[] requestParts = inputLine.split(":");
            EmployeeService employeeService = new EmployeeService();

            switch (requestParts[1]) {
                case "viewRecommendationMenu":
                    employeeService.viewRecommendationMenu(outputWriter, requestParts);
                    break;
                case "selectNextDayFoodItems":
                    employeeService.selectNextDayFoodItems(outputWriter, requestParts);
                    break;
                case "giveFeedbackToAnyFoodItem":
                    employeeService.submitFoodFeedback(outputWriter, requestParts);
                    break;
                case "viewFinalizedMenu":
                    employeeService.viewFinalizedMenu(outputWriter);
                    break;
                case "viewNotifications":
                    employeeService.viewNotifications(outputWriter);
                    break;
                case "improvementAnswers":
                    employeeService.fillEmployeeImprovementAnswers(inputReader, outputWriter, requestParts);
                    break;
                case "viewImprovementQuestionandAnswers":
                    employeeService.viewImprovementQuestionAndAnswers(inputReader, outputWriter);
                    break;
                case "makeEmployeeProfile":
                    employeeService.makeEmployeeProfile(inputReader, outputWriter, requestParts);
                    break;
                case "getDiscardFoodItemIds":
                    employeeService.getDiscardedMenuItemIds(inputReader,outputWriter);
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
