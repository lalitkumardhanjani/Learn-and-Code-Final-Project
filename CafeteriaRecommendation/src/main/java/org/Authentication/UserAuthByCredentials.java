package org.Authentication;

import org.Database.IAuthenticationDatabase;

public class UserAuthByCredentials implements IAuthentication {
    private final int userId;
    private final String password;
    private final int role;
    private final IAuthenticationDatabase database;

    public UserAuthByCredentials(int userId, String password, int role, IAuthenticationDatabase database) {
        if (userId <= 0 || password == null || password.isEmpty() || role < 0) {
            throw new IllegalArgumentException("Invalid user credentials or role");
        }
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.database = database;
    }

    @Override
    public boolean login() {
        try {
            return database.isValidUser(userId, password, role);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String getUserRole(int userId){
        try {
            return database.getUserRole(userId);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return "Not a valid user";
    }
}
