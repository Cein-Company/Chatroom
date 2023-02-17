package server.entrance;

import client.models.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import utils.ConsoleDetail;

import java.util.UUID;

import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class EntranceHandler {

    public static final String USERNAME_TAKEN = RED_BOLD_BRIGHT + "Username taken. Please try again." + RESET;
    public static final String NO_SUCH_CLIENT = RED_BOLD_BRIGHT + "No such username was found. Please try again." + RESET;
    public static final String CLIENT_BANNED = RED_BOLD_BRIGHT + "This user was banned from the chatroom." + RESET;
    public static final String ACTIVE_CLIENT = RED_BOLD_BRIGHT + "User is already in the chatroom." + RESET;
    private static final String INCORRECT_PASSWORD = RED_BOLD_BRIGHT + "Password incorrect. Please try again." + RESET;

    public static void register(ClientModel newClient) throws Exception {
        String usernameResult = checkUsername(newClient.getUsername());
        if (usernameResult != null)
            throw new Exception(usernameResult);

        String passwordResult = checkPassword(newClient.getPassword());
        if (passwordResult != null)
            throw new Exception(passwordResult);

        newClient = new ClientModel(
                newClient.getUsername(),
                newClient.getPassword(),
                UUID.randomUUID(),
                ConsoleDetail.getRandomBBColor());
        MyUsersFiles.save(newClient);
    }

    public static boolean login(ClientModel client) throws Exception {
        String result = null;
        if (!MyUsersFiles.contains(client.getUsername())) {
            result = NO_SUCH_CLIENT;
        } else if (MyUsersFiles.getUserByName(client.getUsername()).isBanned()) {
            result = CLIENT_BANNED;
        } else if (MyActiveUsersFiles.contains(client.getUsername())) {
            result = ACTIVE_CLIENT;
        } else if (!MyUsersFiles.getUserByName(client.getUsername()).getPassword().equals(client.getPassword())) {
            result = INCORRECT_PASSWORD;
        }
        if (result != null)
            throw new Exception(result);
        return true;
    }

    private static String checkUsername(String username) {
        if (MyUsersFiles.contains(username)) {
            return USERNAME_TAKEN;
        }
        return null;
    }

    private static String checkPassword(String password) {
        return null;
    }

}
