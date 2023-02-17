package server.commandserver;

import client.models.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import server.ChatClientHandler;
import server.models.servermessage.ServerMessageMode;
import server.models.servermessage.ServerMessageModel;

import java.util.ArrayList;

import static server.ChatClientHandler.getClientHandlers;

public class ServerCommandKick {
    protected static ServerMessageModel kickCommand(String[] commandTokens) {
        if (commandTokens.length == 2) {
            String kickedUser = commandTokens[1];
            ClientModel kickedUserModel;

            if (MyActiveUsersFiles.contains(kickedUser)) {
                kickedUserModel = MyUsersFiles.getUserByName(kickedUser);

                ChatClientHandler chatClientHandler = null;
                for (ChatClientHandler client : getClientHandlers())
                    if (client.getClientUsername().equals(kickedUserModel.getUsername())) {
                        chatClientHandler = client;
                    }

                if (chatClientHandler != null) {
                    chatClientHandler.sendMessageToClient(getUserKickedMsg());
                    chatClientHandler.broadcastMessageToOthers(getUserKickedMsgToAll(kickedUserModel));
                }

                kick(kickedUserModel);
                return getUserKickedMsgToAll(kickedUserModel);
            } else if (MyUsersFiles.contains(kickedUser))
                return getNotOnline(MyUsersFiles.getUserByName(kickedUser));
            else
                return getUserNotFoundMsg();
        } else
            return getInvalidKickCommandMsg();
    }

    protected static void kick(ClientModel kickedUser) {
        ArrayList<ChatClientHandler> tempClients = new ArrayList<>(getClientHandlers());
        for (ChatClientHandler clientHandler : tempClients)
            if (clientHandler.getClientModel().getUsername().equals(kickedUser.getUsername()))
                clientHandler.closeEverything();
    }

    private static ServerMessageModel getUserKickedMsg() {
        return new ServerMessageModel(ServerMessageMode.ServerKickMsg, "You were kicked out from the chatroom.");
    }

    private static ServerMessageModel getUserKickedMsgToAll(ClientModel kickedUserModel) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, kickedUserModel, " was kicked out from the chatroom.");
    }

    private static ServerMessageModel getNotOnline(ClientModel kickedUser) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, kickedUser, " is not online at the moment.");
    }

    private static ServerMessageModel getUserNotFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No such user was found in the server.");
    }

    private static ServerMessageModel getInvalidKickCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /kick command correctly.");
    }
}
