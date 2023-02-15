package server.commandserver;

import client.models.ClientModel;
import files.MyActiveUsersFiles;
import files.MyUsersFiles;
import server.ChatClientHandler;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.Locale;

import static server.ChatClientHandler.getClientHandlers;

public class ServerCommandBan {
    protected static ServerMessageModel banCommand(String[] commandTokens) {
        if (commandTokens.length == 2) {
            String bannedUser = commandTokens[1];
            ClientModel bannedClientModel;

            if (MyActiveUsersFiles.contains(bannedUser)) {
                bannedClientModel = MyUsersFiles.getUserByName(bannedUser);

                ChatClientHandler chatClientHandler = null;
                for (ChatClientHandler client : getClientHandlers())
                    if (client.getClientUsername().equals(bannedClientModel.getUsername())) {
                        chatClientHandler = client;
                    }

                if (chatClientHandler != null) {
                    bannedClientModel.setBanned(true);
                    chatClientHandler.sendMessageToClient(getUserBanMsg());
                    chatClientHandler.broadcastMessageToOthers(getUserBanMsgToAll(true, bannedClientModel));
                }

                ServerCommandKick.kick(bannedClientModel);

                MyUsersFiles.remove(bannedClientModel);
                MyUsersFiles.save(bannedClientModel);

                return getUserBanMsgToAll(true, bannedClientModel);
            } else if (MyUsersFiles.contains(bannedUser)) {
                bannedClientModel = MyUsersFiles.getUserByName(bannedUser);

                if (bannedClientModel != null && !bannedClientModel.isBanned()) {
                    // Just takes a random client to send the ban message to all clients
                    for (ChatClientHandler client : getClientHandlers()) {
                        client.broadcastMessageToAll(getUserBanMsgToAll(false, bannedClientModel));
                        break;
                    }

                    bannedClientModel.setBanned(true);

                    MyUsersFiles.remove(bannedClientModel);
                    MyUsersFiles.save(bannedClientModel);

                    return getUserBanMsgToAll(false, bannedClientModel);
                } else
                    return getUserAlreadyBanned(bannedClientModel);
            } else
                return getUserNotFoundMsg();
        } else if (commandTokens.length == 3 && commandTokens[2].toLowerCase(Locale.ROOT).equals("-u")) {
            String unBannedUser = commandTokens[1];
            if (MyUsersFiles.contains(unBannedUser)) {
                ClientModel unBannedClientModel = MyUsersFiles.getUserByName(unBannedUser);
                ;
                ;

                if (unBannedClientModel != null && unBannedClientModel.isBanned()) {
                    // Just takes a random client to send the unbanned message to all clients
                    for (ChatClientHandler client : getClientHandlers()) {
                        client.broadcastMessageToOthers(getUserUnBannedMsgToAll(unBannedClientModel));
                        break;
                    }

                    unBannedClientModel.setBanned(false);

                    MyUsersFiles.remove(unBannedClientModel);
                    MyUsersFiles.save(unBannedClientModel);

                    return getUserUnBannedMsgToAll(unBannedClientModel);
                } else
                    return getUseWasntBanned(unBannedClientModel);
            } else
                return getUserNotFoundMsg();
        } else
            return getInvalidBanCommandMsg();
    }

    private static ServerMessageModel getUserBanMsg() {
        return new ServerMessageModel(ServerMessageMode.ServerKickMsg, "You were kicked out & banned from the chatroom forever.");
    }

    private static ServerMessageModel getUserBanMsgToAll(boolean online, ClientModel bannedClientModel) {
        if (online)
            return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, bannedClientModel, " was kicked out & banned from the chatroom forever.");
        else
            return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, bannedClientModel, " was banned from the chatroom forever.");
    }

    private static ServerMessageModel getUserAlreadyBanned(ClientModel bannedClientModel) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, bannedClientModel, " is already banned from the chatroom.");
    }

    private static ServerMessageModel getUserUnBannedMsgToAll(ClientModel unBannedClientModel) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, unBannedClientModel, " was unbanned from the chatroom.");
    }

    private static ServerMessageModel getUseWasntBanned(ClientModel bannedClientModel) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, bannedClientModel, " wasn't banned from the chatroom.");
    }

    private static ServerMessageModel getUserNotFoundMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No such user was found in the server.");
    }

    private static ServerMessageModel getInvalidBanCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /ban command correctly.");
    }
}
