package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final int PORT = 1234;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            System.out.println("Connected to server on port " + socket.getLocalPort());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                if (!displayMainMenuAndHandleLogin(scanner, in, out)) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
            System.out.println("Disconnected from server on port " + socket.getLocalPort());

        } catch (ConnectException e) {
            System.err.println("Connection refused. Make sure the server is running.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean displayMainMenuAndHandleLogin(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        MenuDisplay.displayMainMenu();
        int roleChoice = Utility.readIntInput(scanner);

        System.out.println("Enter userId: ");
        int userId = Utility.readIntInput(scanner);
        scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Sending login request...");
        out.println("login:" + roleChoice + ":" + userId + ":" + password);

        String response = in.readLine();
        System.out.println(response);

        if (response.startsWith("Login successful")) {
            String[] responseParts = response.split(":");
            if (responseParts.length > 1) {
                String role = responseParts[1].trim();
                if ("admin".equalsIgnoreCase(role)) {
                    AdminActions.showAdminMenu(scanner, in, out);
                } else if ("chef".equalsIgnoreCase(role)) {
                    ChefActions.showChefMenu(scanner, in, out);
                } else if ("employee".equalsIgnoreCase(role)) {
                    EmployeeActions.showEmployeeMenu(scanner, in, out, userId);
                }
            } else {
                System.out.println("Role information missing in response.");
            }
        }

        System.out.println("Do you want to exit the application? (yes/no): ");
        String exitChoice = scanner.nextLine().trim().toLowerCase();
        return !exitChoice.equals("yes");
    }
}
