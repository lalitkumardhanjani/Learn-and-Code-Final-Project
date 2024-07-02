package org.Authentication;

import org.Database.AuthenticationDatabase;

public class UserAuthByCredentials implements AuthenticationService {
    private final int userId;
    private final String password;
    private final int role;
    private final AuthenticationDatabase database;

    public UserAuthByCredentials(int userId, String password, int role, AuthenticationDatabase database) {
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
            return database.checkCredentials(userId, password, role);
        } catch (Exception e) {
            // Log the exception (you might use a logger here)
            e.printStackTrace();
            return false;
        }
    }
}
