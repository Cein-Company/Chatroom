package server.commandserver;

import server.ChatServer;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.util.Locale;

public class CommandHandlerServer {
    private static final ServerMessageModel serverInvalidMsgModel =
            new ServerMessageModel(ServerMessageMode.ToAdminister, "Invalid Command.");

    public static ServerMessageModel commandHandler(String serverMessage) {
        String[] commandTokens = serverMessage.split("\\s+");

        return switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/help" -> ServerCommandHelp.helpCommand(commandTokens);
            case "/log" -> ServerCommandLog.logCommand(commandTokens);
            case "/members" -> ServerCommandMembers.membersCommand(commandTokens);
            case "/kick" -> ServerCommandKick.kickCommand(commandTokens);
            case "/ban" -> ServerCommandBan.banCommand(commandTokens);
            case "/message" -> ServerCommandMessage.messageCommand(commandTokens);
            case "/poll" -> ServerCommandPoll.messageCommand(commandTokens);
            case "/shutdown" -> ServerCommandShutdown.closeCommand();
            default -> serverInvalidMsgModel;
        };
    }
}
