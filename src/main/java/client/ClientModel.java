package client;

import java.io.Serializable;

public class ClientModel implements Serializable {
    private String username;
    private String password;

    public ClientModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
