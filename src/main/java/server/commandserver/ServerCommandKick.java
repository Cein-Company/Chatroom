package server.commandserver;

import server.ChatClientHandler;

import java.util.ArrayList;

import static client.ChatClientCLI.*;
import static server.ChatClientHandler.clients;
import static server.commandserver.ServerCommandHelp.*;
import static utils.ConsoleDetail.*;
import static utils.ConsoleDetail.RESET;

public class ServerCommandKick {
    protected static String kickCommand(String[] commandTokens) {
        if (commandTokens.length == 2) {
            String kickedUser = commandTokens[1];
            String kickedUserColoredUsername;

            if (getActiveUsersFromFile().contains(kickedUser)) {
                kickedUserColoredUsername = getUsersFromFile().get(kickedUser).getColoredUsername();

                for (ChatClientHandler client : clients)
                    if (client.getClientModel().getUsername().equals(kickedUser)) {
                        client.sendMessageToClient("You were kicked out from the chatroom.");
                        client.broadcastMessage(
                                RED_BOLD_BRIGHT + "SERVER: " + RESET +
                                        kickedUserColoredUsername +
                                        RED_BOLD_BRIGHT + " was kicked out from the chatroom." + RESET);
                    }

                kick(kickedUser);
                return getUsersFromFile().get(kickedUser).getColoredUsername() + RED_BOLD_BRIGHT + " was kicked out from the server." + RESET;
            } else if (getUsersFromFile().containsKey(kickedUser)) {
                kickedUserColoredUsername = getUsersFromFile().get(kickedUser).getColoredUsername();

                return kickedUserColoredUsername + RED_BOLD_BRIGHT + " is not online at the moment." + RESET;
            } else {
                return RED_BOLD_BRIGHT + "No such user was found." + RESET;
            }
        } else
            return RED_BOLD_BRIGHT + "Please Use the /kick command correctly.\n" + RESET + indicator + kickCmd;
    }

    protected static void kick(String kickedUser) {
        ArrayList<ChatClientHandler> tempClients = new ArrayList<>(clients);
        for (ChatClientHandler client : tempClients)
            if (client.getClientModel().getUsername().equals(kickedUser))
                client.closeEverything(client.getSocket(), client.getBufferedReader(), client.getBufferedWriter());
    }
}
