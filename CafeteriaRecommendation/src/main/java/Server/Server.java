package Server;

import Authentication.AuthenticationService;
import Authentication.*;
import Database.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static Map<Socket, Integer> activeUsers = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started on port " + serverSocket.getLocalPort() + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from port " + clientSocket.getPort());

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int userId = -1;
        private Database database = new SqlServerDatabase();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
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
                        handleLoginAndMenuCreation(in,out, inputLine);
                    }
                }
            } catch (SocketException e) {
                System.err.println("Client disconnected unexpectedly: " + e.getMessage());
            } catch (IOException e) {
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

        private void handleLoginAndMenuCreation(BufferedReader in, PrintWriter out, String inputLine) throws IOException {
            String[] parts = inputLine.split(":");
            if (parts.length == 3) {
                handleLogin(out, parts);
            } else if (parts.length == 5 && "createMenuItem".equals(parts[0])) {
                handleMenuCreation(out, parts);
            } else if (parts.length == 1 && "viewMenu".equals(parts[0])) {
                handleViewMenu(out);
            } else if (parts.length == 5 && "updateMenuItem".equals(parts[0])) {
                handleUpdateMenuItem(out, parts);
            } else if (parts.length ==2 && "deleteMenuItem".equals(parts[0])) {
                handleDeleteMenuItem(out,parts);
            } else {
                out.println("Invalid input.");
            }
        }

        private void handleDeleteMenuItem(PrintWriter out,String [] parts){
            int menuId = Integer.parseInt(parts[1]);
            System.out.println(menuId);
            if (activeUsers.get(clientSocket) != null) {
                boolean isDelete = database.deleteMenuItem(menuId);
                if(isDelete) {
                    out.println("Menu item deleted successfully.");
                }
            } else {
                out.println("Not authorized.");
            }
        }

        private void handleLogin(PrintWriter out, String[] parts) {
            int role = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            String password = parts[2];

            AuthenticationService auth = new UserAuthByCredentials(userId, password, role, database);
            if (auth.login()) {
                out.println("Login successful: " + (role == 1 ? "admin" : (role==2) ? "chef" : "Employee"));
                this.userId = userId;
                activeUsers.put(clientSocket, userId);
            } else {
                out.println("Invalid credentials");
            }
        }

        private void handleMenuCreation(PrintWriter out, String[] parts) {
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            Integer mealType = Integer.parseInt(parts[3]);
            int availability = Integer.parseInt(parts[4]);

            if (activeUsers.get(clientSocket) != null) {
                database.createMenuItem(name, price, mealType, availability);
                out.println("Menu item created successfully.");
            } else {
                out.println("Not authorized.");
            }
        }

        private void handleUpdateMenuItem(PrintWriter out, String[] parts) {
            if (parts.length < 5) {
                out.println("Invalid input for updating menu item.");
                return;
            }

            int menuId = Integer.parseInt(parts[1]);
            String newName = parts[2];
            Double newPrice = Double.parseDouble(parts[3]);
            int newAvailability = Integer.parseInt(parts[4]);

            if (activeUsers.get(clientSocket) != null) {
                database.updateMenuItem(menuId, newName, newPrice, newAvailability);
                out.println("Menu item updated successfully.");
            } else {
                out.println("Not authorized.");
            }
        }


        private void handleUpdateMenuItem(BufferedReader in, PrintWriter out) throws IOException {

        }


        private void handleViewMenu(PrintWriter out) {
            if (activeUsers.get(clientSocket) != null) {
                // Retrieve the menu items from the database and send them to the client
                List<String> menuItems = database.getMenuItems();

                if (menuItems.isEmpty()) {
                    out.println("No menu items available.");
                } else {
                    out.println("----- Cafeteria Menu -----");
                    out.println(String.format("%-15s %-20s %-10s %-15s %-15s","Id", "Item", "Price", "Meal ID","IsAvailable"));
                    out.println("--------------------------------------------");

                    for (String menuItem : menuItems) {
                        out.println(menuItem);
                    }
                    out.println("--------------------------------------------");
                    out.println("END");
                }
            } else {
                out.println("Not authorized.");
            }
        }



    }
}
