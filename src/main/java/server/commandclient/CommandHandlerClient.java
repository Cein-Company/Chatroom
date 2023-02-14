package server.commandclient;

import java.util.Locale;

public class CommandHandlerClient {
    public static String commandHandler(String clientMessage) {
        String clientUsername = clientMessage.substring(clientMessage.indexOf("-> ") + 14, clientMessage.indexOf(": ") - 11);
        String clientCommandMessage = clientMessage.substring(clientMessage.indexOf(": ") + 13, clientMessage.length() - 4);

        String[] commandTokens = clientCommandMessage.split("\\s+");

        switch (commandTokens[0].toLowerCase(Locale.ROOT)) {
            case "/help":
                return ClientCommandHelp.helpCommand(clientUsername, commandTokens);
            case "/message"://                                                /message personName "Text to be delivered"
                return ClientCommandMessage.messageCommand(clientUsername, commandTokens);
            case "/poll"://                                                         /poll -j pollID  or  /poll -d pollID
                if (commandTokens[1].toLowerCase(Locale.ROOT).equals("-j")) {
                    // Accept pollID
                } else if (commandTokens[1].toLowerCase(Locale.ROOT).equals("-d")) {
                    return "SERVER: You have denied entering the 'pollNumber' poll.";
                }
            default:
                return "SERVER: Invalid Command.";
        }
    }
}
