package Authentication;

import Database.Database; // Import the Database class

public class UserAuthByCredentials implements AuthenticationService {
    private final int userId;
    private final String password;
    private final int role;
    private final Database database;

    public UserAuthByCredentials(int userId, String password, int role, Database database) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.database = database;
    }

    @Override
    public boolean login() {
        return database.checkCredentials(userId, password, role); // Pass an empty string for role
    }

}
