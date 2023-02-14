package server.commandclient;

import client.models.ClientMessageModel;
import client.models.ClientModel;
import server.ChatClientHandler;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;


public class ClientCommandExit {
    protected static ServerMessageModel exitCommand(ChatClientHandler chatClientHandler, ClientMessageModel clientMessage, String[] commandTokens) {
        if (commandTokens.length == 1) {
            ClientModel leavingClient = clientMessage.getSender();

            chatClientHandler.sendMessageToClient(getLeavingMsg());
            chatClientHandler.broadcastMessageToOthers(getLeftMessageToAll(leavingClient));

            chatClientHandler.closeEverything();

            return getLeftMessageToAll(leavingClient);
        } else
            return getInvalidExitCommandMsg();
    }

    private static ServerMessageModel getLeavingMsg() {
        return new ServerMessageModel(ServerMessageMode.FromSerer, "You have left the chatroom. Goodbye.");
    }

    private static ServerMessageModel getLeftMessageToAll(ClientModel leavingClient) {
        return new ServerMessageModel(ServerMessageMode.FromServerAboutClient, leavingClient, " has left the chatroom.");
    }

    private static ServerMessageModel getInvalidExitCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.FromSerer, "Please Use the /exit command correctly.");
    }
}
