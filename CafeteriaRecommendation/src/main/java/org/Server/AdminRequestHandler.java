package org.Server;

import org.Services.AdminService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminRequestHandler {
    public void handleRequest(String inputLine, BufferedReader inputReader, PrintWriter outputWriter) throws IOException {
        try {
            String[] requestParts = inputLine.split(":");
            AdminService adminService = new AdminService();
            switch (requestParts[1]) {
                case "createMenuItem":
                    adminService.processMenuItemCreation(outputWriter, requestParts);
                    break;
                case "updateMenuItem":
                    adminService.processMenuItemUpdate(outputWriter, requestParts);
                    break;
                case "deleteMenuItem":
                    adminService.processMenuItemDeletion(outputWriter, requestParts);
                    break;
                case "viewMenu":
                    adminService.viewMenu(outputWriter);
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
