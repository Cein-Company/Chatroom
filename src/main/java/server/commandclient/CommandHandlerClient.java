package server.commandclient;

import client.models.ClientMessageModel;
import server.ChatClientHandler;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.Locale;

public class CommandHandlerClient {
    public static ServerMessageModel commandHandler(ChatClientHandler chatClientHandler, ClientMessageModel clientMessage) {
        String[] commandTokens = clientMessage.getMessage().split("\\s+");

        return switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/help" -> ClientCommandHelp.helpCommand(commandTokens);
            case "/message" -> ClientCommandMessage.messageCommand(clientMessage, commandTokens);
            case "/exit" -> ClientCommandExit.exitCommand(chatClientHandler, clientMessage, commandTokens);
            case "/poll" -> ClientCommandPoll.pollCommand(clientMessage.getSender(), commandTokens);
            default -> getInvalidCommandMsg();
        };
    }

    private static ServerMessageModel getInvalidCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.FromSerer, "Invalid Command.");
    }
}
