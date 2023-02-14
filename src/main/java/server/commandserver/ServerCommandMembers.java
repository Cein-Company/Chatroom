package server.commandserver;

import static client.ChatClientCLI.getUsers;
import static client.ChatClientCLI.getUsersFromFile;
import static server.commandserver.ServerCommandHelp.*;
import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class ServerCommandMembers {
    protected static String membersCommand(String[] commandTokens) {
        if (commandTokens.length == 1) {
            StringBuilder usersList = new StringBuilder();
            int usersCount = 0;
            for (String user : getUsersFromFile().keySet())
                usersList.append(++usersCount).append(". Username: ").append(user).append("\n");

            return usersList.toString();
        } else
            return RED_BOLD_BRIGHT + "SERVER: Please Use the /members command correctly.\n" + RESET + indicator + membersCmd;
    }
}
