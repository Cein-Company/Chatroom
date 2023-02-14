package server.commandserver;

import client.ChatClientCLI;
import server.ChatClientHandler;

import java.util.Locale;

import static client.ChatClientCLI.getActiveUsersFromFile;
import static client.ChatClientCLI.getUsersFromFile;
import static server.ChatClientHandler.clients;
import static server.commandserver.ServerCommandHelp.banCmd;
import static server.commandserver.ServerCommandHelp.indicator;
import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class ServerCommandBan {
    protected static String banCommand(String[] commandTokens) {
        if (commandTokens.length == 2) {
            String bannedUser = commandTokens[1];
            String bannedUserColoredUsername;

            if (getActiveUsersFromFile().contains(bannedUser)) {
                bannedUserColoredUsername = getUsersFromFile().get(bannedUser).getColoredUsername();

                for (ChatClientHandler client : clients)
                    if (client.getClientModel().getUsername().equals(bannedUser)) {
                        client.sendMessageToClient("You were kicked out & banned from the chatroom forever.");
                        client.broadcastMessage(
                                RED_BOLD_BRIGHT + "SERVER: " + RESET +
                                        bannedUserColoredUsername +
                                        RED_BOLD_BRIGHT + " was kicked out & banned from the chatroom forever." + RESET);
                    }

                ServerCommandKick.kick(bannedUser);
                ban(bannedUser);

                return bannedUserColoredUsername + RED_BOLD_BRIGHT + " was kicked out & banned from the chatroom forever." + RESET;
            } else if (getUsersFromFile().containsKey(bannedUser)) {
                bannedUserColoredUsername = getUsersFromFile().get(bannedUser).getColoredUsername();

                for (ChatClientHandler client : clients)
                    if (client.getClientModel().getUsername().equals(bannedUser)) {
                        client.sendMessageToClient("You were banned from the chatroom forever.");
                        client.broadcastMessage(
                                RED_BOLD_BRIGHT + "SERVER: " + RESET +
                                        bannedUserColoredUsername +
                                        RED_BOLD_BRIGHT + " was banned from the chatroom forever." + RESET);
                    }

                ban(bannedUser);
                return bannedUserColoredUsername + RED_BOLD_BRIGHT + " was banned from the chatroom forever." + RESET;
            } else {
                return RED_BOLD_BRIGHT + "No such user was found." + RESET;
            }
        } else if (commandTokens.length == 3 && commandTokens[2].toLowerCase(Locale.ROOT).equals("-u")) {
            String bannedUser = commandTokens[1];
            ChatClientCLI.removeBannedUsers(bannedUser);

            return RED_BOLD_BRIGHT + bannedUser + " was unbanned from the chatroom." + RESET;
        } else
            return RED_BOLD_BRIGHT + "Please Use the /ban command correctly.\n" + RESET + indicator + banCmd;
    }

    private static void ban(String bannedUser) {
        ChatClientCLI.removeUsers(bannedUser);
        ChatClientCLI.addBannedUsers(bannedUser);
    }
}
