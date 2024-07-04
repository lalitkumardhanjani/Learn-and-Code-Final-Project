package org.Server;

import org.Authentication.AuthenticationService;
import org.Authentication.UserAuthByCredentials;
import org.Database.AuthenticationDatabase;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class AuthenticationHandler {
    private final AuthenticationDatabase authDatabase;
    private final Map<Socket, Integer> activeUsers;

    public AuthenticationHandler(AuthenticationDatabase authDatabase, Map<Socket, Integer> activeUsers) {
        this.authDatabase = authDatabase;
        this.activeUsers = activeUsers;
    }

    public void handleLogin(PrintWriter out, String[] parts, Socket clientSocket) {
        try {
            int role = Integer.parseInt(parts[1]);
            int userId = Integer.parseInt(parts[2]);
            String password = parts[3];

            AuthenticationService auth = new UserAuthByCredentials(userId, password, role, authDatabase);
            if (auth.login()) {
                out.println("Login successful: " + getRoleName(role));
                activeUsers.put(clientSocket, userId);
            } else {
                out.println("Invalid credentials");
            }
        } catch (Exception e) {
            out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleLogout(PrintWriter out, Socket clientSocket) {
        Integer userId = activeUsers.remove(clientSocket);
        if (userId == null) {
            out.println("Not logged in");
        } else {
            authDatabase.logLogoutAttempt(userId, true);
            out.println("Logout successful");
        }
    }

    private String getRoleName(int role) {
        switch (role) {
            case 1: return "admin";
            case 2: return "chef";
            case 3: return "Employee";
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
