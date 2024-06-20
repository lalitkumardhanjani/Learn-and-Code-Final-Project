package Authentication;

import Database.Database;
public class UserAuthByCredentials implements Authentication {
    private int userId;
    private String password;
    private Database database;

    public UserAuthByCredentials(int userId, String password, Database database) {
        this.userId = userId;
        this.password = password;
        this.database = database;
    }

    @Override
    public boolean login() {
        return database.checkCredentials(userId, password);
    }
}