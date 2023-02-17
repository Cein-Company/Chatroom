package models.clientmodels;

import java.io.Serializable;
import java.util.UUID;

import static utils.ConsoleDetail.RESET;

public class ClientModel implements Serializable {
    private final UUID clientId;
    private final String username;
    private final String password;
    private final String coloredUsername;
    private final String CLIENT_COLOR;

    private boolean banned;

    public ClientModel(String username, String password, UUID clientId, String CLIENT_COLOR) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.CLIENT_COLOR = CLIENT_COLOR;
        this.coloredUsername = CLIENT_COLOR + this.username + RESET;
        this.banned = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getColoredUsername() {
        return coloredUsername;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public static ClientModel factory(String username, String password) {
        return new ClientModel(username, password, null, "");
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getCLIENT_COLOR() {
        return CLIENT_COLOR;
    }
}
