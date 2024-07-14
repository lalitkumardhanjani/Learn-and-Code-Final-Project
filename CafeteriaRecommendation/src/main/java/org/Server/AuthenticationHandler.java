package org.Server;

import org.Authentication.IAuthentication;
import org.Authentication.UserAuthByCredentials;
import org.Database.IAuthenticationDatabase;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class AuthenticationHandler {
    private final IAuthenticationDatabase authenticationDatabase;

    public AuthenticationHandler(IAuthenticationDatabase authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public void handleLogin(PrintWriter outputWriter, String[] loginData, Socket clientConnectionSocket) {
        try {
            int userRole = Integer.parseInt(loginData[2]);
            int userId = Integer.parseInt(loginData[3]);
            String userPassword = loginData[4];

            IAuthentication authenticationService = new UserAuthByCredentials(userId, userPassword, userRole, authenticationDatabase);
            if (authenticationService.login()) {
                outputWriter.println("Login successful: " + authenticationService.getUserRole(userId));
            } else {
                outputWriter.println("Invalid credentials");
            }
        } catch (NumberFormatException numberFormatException) {
            outputWriter.println("Error during login: Invalid number format. " + numberFormatException.getMessage());
        } catch (Exception loginException) {
            outputWriter.println("Error during login: " + loginException.getMessage());
        }
    }
}
