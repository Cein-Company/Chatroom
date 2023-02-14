package server.commandserver;

import server.ChatServer;

import java.util.Locale;

import static utils.ConsoleDetail.*;

public class CommandHandlerServer {
    public static String commandHandler(String serverMessage) {
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
            case "/config":
                // Config
            case "/poll":
                // Poll
            case "/exit":
                ChatServer.closeServerSocket();
                return RED_BOLD_BRIGHT + "You have closed the Sever." + RESET;
            default:
                return RED_BOLD_BRIGHT + "INVALID COMMAND" + RESET;
        }
    }
}
