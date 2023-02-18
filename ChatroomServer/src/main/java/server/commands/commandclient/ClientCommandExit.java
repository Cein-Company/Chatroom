package server.commands.commandclient;

import models.clientmodels.ClientMessageModel;
import models.clientmodels.ClientModel;
import server.ChatClientHandler;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;


public class ClientCommandExit {
    protected static ServerMessageModel exitCommand(ChatClientHandler chatClientHandler, ClientMessageModel clientMessage, String[] commandTokens) {
        if (commandTokens.length == 1) {
            ClientModel leavingClient = clientMessage.getSender();

            chatClientHandler.sendMessageToClient(getLeavingMsg());

            chatClientHandler.closeEverything();

            return getLeftMessageToAll(leavingClient);
        } else
            return getInvalidExitCommandMsg();
    }

    private static ServerMessageModel getLeavingMsg() {
        return new ServerMessageModel(ServerMessageMode.GoodbyeFromServer, "You have left the chatroom. Goodbye.");
    }

    private static ServerMessageModel getLeftMessageToAll(ClientModel leavingClient) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, leavingClient, " has left the chatroom.");
    }

    private static ServerMessageModel getInvalidExitCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "Please Use the /exit command correctly.");
    }
}
