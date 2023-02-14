package client;

import java.io.Serializable;
import java.util.Random;

import static utils.ConsoleDetail.BOLD_BRIGHTS_COLORS;
import static utils.ConsoleDetail.RESET;

public class ClientModel implements Serializable {
    private final String username;
    private final String password;
    private final String coloredUsername;
    private final String CLIENT_COLOR;

    public ClientModel(String username, String password) {
        this.username = username;
        this.password = password;
        this.CLIENT_COLOR = BOLD_BRIGHTS_COLORS[new Random().nextInt(BOLD_BRIGHTS_COLORS.length)];
        this.coloredUsername = CLIENT_COLOR + this.username + RESET;
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

    public String getCLIENT_COLOR() {
        return CLIENT_COLOR;
    }
}
