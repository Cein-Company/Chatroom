package server.commands.commandserver;

import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.Locale;

public class CommandHandlerServer {
    public static ServerMessageModel commandHandler(String serverMessage) {
        String[] commandTokens = serverMessage.split("\\s+");

        return switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/help" -> ServerCommandHelp.helpCommand(commandTokens);
            case "/log" -> ServerCommandLog.logCommand(commandTokens);
            case "/members" -> ServerCommandMembers.membersCommand(commandTokens);
            case "/kick" -> ServerCommandKick.kickCommand(commandTokens);
            case "/ban" -> ServerCommandBan.banCommand(commandTokens);
            case "/message" -> ServerCommandMessage.messageCommand(commandTokens);
            case "/poll" -> ServerCommandPoll.pollCommand(commandTokens);
            case "/shutdown" -> ServerCommandShutdown.closeCommand();
            default -> getServerInvalidMsgModel();
        };
    }


    private static ServerMessageModel getServerInvalidMsgModel() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Invalid Command.");
    }
}
