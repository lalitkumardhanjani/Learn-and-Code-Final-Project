package org.Authentication;

import org.Database.AuthenticationDatabase;

public class UserAuthByCredentials implements AuthenticationService {
    private int userId;
    private String password;
    private int role;
    private AuthenticationDatabase database;

    public UserAuthByCredentials(int userId, String password, int role, AuthenticationDatabase database) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.database = database;
    }

    @Override
    public boolean login() {
        return database.checkCredentials(userId, password, role);
    }
}
