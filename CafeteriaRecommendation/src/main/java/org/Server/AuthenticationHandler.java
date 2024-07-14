package org.Server;

import org.Authentication.IAuthentication;
import org.Authentication.UserAuthByCredentials;
import org.Database.IAuthenticationDatabase;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class AuthenticationHandler {
    private final IAuthenticationDatabase authenticationDatabase;
    private final Map<Socket, Integer> activeUserMap;

    public AuthenticationHandler(IAuthenticationDatabase authenticationDatabase, Map<Socket, Integer> activeUserMap) {
        this.authenticationDatabase = authenticationDatabase;
        this.activeUserMap = activeUserMap;
    }

    public void handleLogin(PrintWriter outputWriter, String[] loginData, Socket clientConnectionSocket) {
        try {
            int userRole = Integer.parseInt(loginData[1]);
            int userId = Integer.parseInt(loginData[2]);
            String userPassword = loginData[3];

            IAuthentication authenticationService = new UserAuthByCredentials(userId, userPassword, userRole, authenticationDatabase);
            if (authenticationService.login()) {
                outputWriter.println("Login successful: " + getRoleName(userRole));
                activeUserMap.put(clientConnectionSocket, userId);
            } else {
                outputWriter.println("Invalid credentials");
            }
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error during login: Invalid number format. " + numberFormatException.getMessage());
        } catch (Exception loginException) {
            outputWriter.println("Error during login: " + loginException.getMessage());
        }
    }

    public void handleLogout(PrintWriter outputWriter, Socket clientConnectionSocket) {
        Integer userId = activeUserMap.remove(clientConnectionSocket);
        if (userId == null) {
            outputWriter.println("Not logged in");
        } else {
            authenticationDatabase.logLogoutAttempt(userId, true);
            outputWriter.println("Logout successful");
        }
    }

    private String getRoleName(int role) {
        switch (role) {
            case 1: return "Admin";
            case 2: return "Chef";
            case 3: return "Employee";
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
