package org.Authentication;

public interface IAuthentication {
    boolean login();
    String getUserRole(int userId);

}
