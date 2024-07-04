package org.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserAuthenticationHandler {
    private final Scanner scanner;
    private final BufferedReader in;
    private final PrintWriter out;

    public UserAuthenticationHandler(Scanner scanner, BufferedReader in, PrintWriter out) {
        this.scanner = scanner;
        this.in = in;
        this.out = out;
    }

    public boolean handleAuthentication() {
        try {
            while (true) {
                MenuDisplay.displayMainMenu();
                int roleChoice = Utility.readIntInput(scanner);

                if (roleChoice == 4) {
                    System.out.println("Exiting the program...");
                    return false;
                }

                System.out.println("Enter userId: ");
                int userId = Utility.readIntInput(scanner);
                scanner.nextLine(); // Consume newline left-over
                System.out.println("Enter password: ");
                String password = scanner.nextLine();

                System.out.println("Sending login request...");
                out.println("login:" + roleChoice + ":" + userId + ":" + (password.isEmpty() ? "" : password));

                String response = in.readLine();
                System.out.println(response);

                if (response.startsWith("Login successful")) {
                    handleRole(roleChoice, userId);
                    return true;
                } else {
                    System.out.println("Login failed. Please try again.");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine(); // Clear the invalid input
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return false;
    }

    private void handleRole(int roleChoice, int userId) {
        switch (roleChoice) {
            case 1:
                AdminActions.showAdminMenu(scanner, in, out);
                break;
            case 2:
                ChefActions.showChefMenu(scanner, in, out);
                break;
            case 3:
                EmployeeActions.showEmployeeMenu(scanner, in, out, userId);
                break;
            default:
                System.out.println("Unknown role choice: " + roleChoice);
                break;
        }
    }
}
