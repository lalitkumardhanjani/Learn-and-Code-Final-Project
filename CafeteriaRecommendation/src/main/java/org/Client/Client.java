package org.Client;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private static final int PORT = 1234;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server on port " + socket.getLocalPort());

            while (true) {
                if (!displayMainMenuAndHandleLogin(scanner, in, out)) {
                    break;
                }
            }

            System.out.println("Disconnected from server on port " + socket.getLocalPort());

        } catch (ConnectException e) {
            System.err.println("Connection refused. Make sure the server is running.");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static boolean displayMainMenuAndHandleLogin(Scanner scanner, BufferedReader in, PrintWriter out) {
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
                scanner.nextLine();
                System.out.println("Enter password: ");
                String password = scanner.nextLine();

                if(password.isEmpty()){
                    password="";
                }
                System.out.println("Sending login request...");
                out.println("login:" + roleChoice + ":" + userId + ":" + password);

                String response = in.readLine();
                System.out.println(response);

                if (response.startsWith("Login successful")) {
                    String[] responseParts = response.split(":");
                    if (responseParts.length > 1) {
                        String role = responseParts[1].trim();
                        switch (role.toLowerCase()) {
                            case "admin":
                                AdminActions.showAdminMenu(scanner, in, out);
                                return true;
                            case "chef":
                                ChefActions.showChefMenu(scanner, in, out);
                                return true;
                            case "employee":
                                EmployeeActions.showEmployeeMenu(scanner, in, out, userId);
                                return true;
                            default:
                                System.out.println("Unknown role: " + role);
                                return false;
                        }
                    } else {
                        System.out.println("Role information missing in response.");
                        return false;
                    }
                } else {
                    System.out.println("Login failed. Please try again.");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input type: " + e.getMessage());
            scanner.nextLine();
            return false;
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return false;
        }
    }
}
