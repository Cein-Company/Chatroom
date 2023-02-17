package server.commands.commandserver;

import models.clientmodels.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.Locale;

import static utils.ConsoleDetail.*;

public class ServerCommandMembers {
    protected static ServerMessageModel membersCommand(String[] commandTokens) {
        if (commandTokens.length == 1) {
            if (MyUsersFiles.getAllUsersDuplicate().isEmpty())
                return getNoMembersFoundMsg();

            StringBuilder usersList = new StringBuilder();
            int usersCount = 0;
            for (ClientModel user : MyUsersFiles.getAllUsersDuplicate().values()) {
                usersList
                        .append(++usersCount)
                        .append(WHITE_BOLD_BRIGHT + ". Username: " + RESET)
                        .append(user.getColoredUsername());

                if (user.isBanned())
                    usersList.append(RED_BOLD_BRIGHT + " -- banned -- " + RESET);

                usersList.append("\n");
            }

            return getMembersListMsg(usersList);
        } else if (commandTokens.length == 2 && commandTokens[1].toLowerCase(Locale.ROOT).equals("-o")) {
            if (MyActiveUsersFiles.getAllActiveUsersDuplicate().isEmpty())
                return getNoOnlineUserFoundMsg();

            StringBuilder onlineUsersList = new StringBuilder();
            int onlineUsersCount = 0;
            for (ClientModel user : MyUsersFiles.getAllUsersDuplicate().values())
                if (MyActiveUsersFiles.contains(user))
                    onlineUsersList
                            .append(++onlineUsersCount)
                            .append(". Username: ")
                            .append(user.getColoredUsername())
                            .append("\n");

            return getOnlineUsersListMsg(onlineUsersList);
        } else
            return getInvalidMembersCommandMsg();
    }

    private static ServerMessageModel getMembersListMsg(StringBuilder usersList) {
        return new ServerMessageModel(ServerMessageMode.ListFromServer, usersList.toString());
    }

    private static ServerMessageModel getOnlineUsersListMsg(StringBuilder onlineUsersList) {
        return new ServerMessageModel(ServerMessageMode.ListFromServer, onlineUsersList.toString());
    }

    private static ServerMessageModel getNoMembersFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No one had joined the chatroom.");
    }

    private static ServerMessageModel getNoOnlineUserFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No online client was found in the server.");
    }

    private static ServerMessageModel getInvalidMembersCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /members command correctly.");
    }
}
