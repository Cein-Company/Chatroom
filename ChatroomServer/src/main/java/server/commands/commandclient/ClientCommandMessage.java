package server.commands.commandclient;

import models.clientmodels.ClientMessageModel;
import models.clientmodels.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.Arrays;
import java.util.Locale;

import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class ClientCommandMessage {
    protected static ServerMessageModel messageCommand(ClientMessageModel clientMessage, String[] commandTokens) {
        if (commandTokens.length >= 3 && commandTokens[2].startsWith("'") && commandTokens[commandTokens.length - 1].endsWith("'")) {
            String receiver = commandTokens[1];

            if (MyActiveUsersFiles.contains(receiver) || receiver.toLowerCase(Locale.ROOT).equals("server")) {
                String message = join(commandTokens, commandTokens[2]);

                if (!receiver.toLowerCase(Locale.ROOT).equals("server") &&
                        MyUsersFiles.getUserByName(receiver).getUsername().equals(clientMessage.getSender().getUsername()))
                    return getCantPMYourselfMsg();

                return receiver.toLowerCase(Locale.ROOT).equals("server") ?
                        getPMToServer(clientMessage, message) : getPMToClient(clientMessage, receiver, message);
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

    private static ServerMessageModel getPMToServer(ClientMessageModel clientMessage, String message) {
        return new ServerMessageModel(ServerMessageMode.PMFromClientToServer, clientMessage, message);
    }

    private static ServerMessageModel getPMToClient(ClientMessageModel clientMessage, String receiver, String message) {
        return new ServerMessageModel(ServerMessageMode.PMFromClientToClient, clientMessage, MyUsersFiles.getUserByName(receiver), message);
    }

    private static ServerMessageModel getNotOnlineMsg(ClientModel clientAbout) {
        return new ServerMessageModel(ServerMessageMode.FromServerToClient,
                clientAbout.getColoredUsername() + RED_BOLD_BRIGHT + " is not online at the moment." + RESET);
    }

    private static ServerMessageModel getCantPMYourselfMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServerToClient, "You can't send a private message to yourself.");
    }

    private static ServerMessageModel getUserNotFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServerToClient, "No such user was found in the server.");
    }

    private static ServerMessageModel getInvalidMessageCommand() {
        return new ServerMessageModel(ServerMessageMode.FromServerToClient, "Please Use the /message command correctly.");
    }
}
