package server.commands.commandserver;

import models.clientmodels.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.Arrays;

public class ServerCommandMessage {
    protected static ServerMessageModel messageCommand(String[] commandTokens) {
        String receiver = commandTokens[1];

        if (commandTokens.length >= 3 && commandTokens[2].startsWith("'") && commandTokens[commandTokens.length - 1].endsWith("'")) {
            if (MyActiveUsersFiles.contains(receiver)) {
                String message = join(commandTokens, commandTokens[2]);

                return getPMFromServer(MyUsersFiles.getUserByName(receiver), message);
            } else if (MyUsersFiles.contains(receiver))
                return getNotOnlineMsg(MyUsersFiles.getUserByName(receiver));
            else
                return getUserNotFoundMsg();
        } else
            return getInvalidMessageCommand();
    }

    private static String join(String[] tokens, String from) {
        return String.join(
                " ",
                Arrays.copyOfRange(
                        tokens,
                        Arrays.asList(tokens).indexOf(from),
                        tokens.length));
    }

    private static ServerMessageModel getPMFromServer(ClientModel receiver, String message) {
        ServerMessageModel privateMessage = new ServerMessageModel(ServerMessageMode.PMFromServerToClient, message);
        privateMessage.setClientModelReceiver(receiver);

        return privateMessage;
    }

    private static ServerMessageModel getNotOnlineMsg(ClientModel clientAbout) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, clientAbout, "User is not online at the moment.");
    }

    private static ServerMessageModel getUserNotFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.FromSerer, "No such user was found in the server.");
    }

    private static ServerMessageModel getInvalidMessageCommand() {
        return new ServerMessageModel(ServerMessageMode.FromSerer, "Please Use the /message command correctly.");
    }
}
