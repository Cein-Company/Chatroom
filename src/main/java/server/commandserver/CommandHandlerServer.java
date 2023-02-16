package server.commandserver;

import server.ChatServer;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.Locale;

// TODO: Rename /exit to /close for server closing
// TODO: Ask for confirmation on server closing

public class CommandHandlerServer {
    private static final ServerMessageModel serverInvalidMsgModel =
            new ServerMessageModel(ServerMessageMode.ToAdminister, "Invalid Command.");
    private static final ServerMessageModel serverExitMsg =
            new ServerMessageModel(ServerMessageMode.ToAdminister, "You have closed the Sever.");

    public static ServerMessageModel commandHandler(String serverMessage) {
        String[] commandTokens = serverMessage.split("\\s+");

        switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/help":
                return ServerCommandHelp.helpCommand(commandTokens);
            case "/log":
                return ServerCommandLog.logCommand(commandTokens);
            case "/members":
                return ServerCommandMembers.membersCommand(commandTokens);
            case "/kick":
                return ServerCommandKick.kickCommand(commandTokens);
            case "/ban":
                return ServerCommandBan.banCommand(commandTokens);
            case "/message":
                return ServerCommandMessage.messageCommand(commandTokens);
            case "/poll":
                return ServerCommandPoll.messageCommand(commandTokens);
            case "/exit":
                ChatServer.closeServerSocket();
                return serverExitMsg;
            default:
                return serverInvalidMsgModel;
        }
    }
}
