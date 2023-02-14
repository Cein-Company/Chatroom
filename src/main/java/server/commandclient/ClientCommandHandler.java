package server.commandclient;

import java.util.Locale;

import static server.commandclient.ClientCommandHelp.helpCommand;
import static utils.consts.ConsoleDetail.*;

public class ClientCommandHandler {
    public static String commandHandler(String clientMessage) {
        String clientUsername = clientMessage.substring(clientMessage.indexOf("-> ") + 14, clientMessage.indexOf(": ") - 11);
        String clientCommandMessage = clientMessage.substring(clientMessage.indexOf(": ") + 13, clientMessage.length() - 4);

        String[] commandTokens = clientCommandMessage.split("\\s+");

        switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/poll":                                                   // -> /poll -j pollID  or  /poll -d pollID
                if (commandTokens[1].toLowerCase(Locale.ROOT).equals("-j")) {
                    // Accept pollID
                } else if (commandTokens[1].toLowerCase(Locale.ROOT).equals("-d")) {
                    return "SERVER: You have denied entering the 'pollNumber' poll.";
                }
            case "/help":
                return helpCommand(clientUsername);
            case "/message":                                           // -> /message -personName "Text to be delivered"
                if (commandTokens.length >= 3 && commandTokens[2].startsWith("'") && commandTokens[commandTokens.length - 1].endsWith("'"))
                    return ClientCommandMessage.messageCommand(clientUsername, commandTokens);
                else
                    return RED_BOLD_BRIGHT + "SERVER: Please Use the /message command correctly." + RESET;
            default:
                return "SERVER: Invalid Command.";
        }
    }
}
