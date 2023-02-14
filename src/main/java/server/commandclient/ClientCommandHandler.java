package server.commandclient;

public class ClientCommandHandler {
    public static String commandHandler(String clientMessage) {
        String clientColoredUsername = clientMessage.substring(0, clientMessage.indexOf(":"));
        String clientCommandMessage = clientMessage.substring(clientMessage.indexOf(":") + 2);

        String[] commandTokens = clientCommandMessage.split("\\s+");

        switch (commandTokens[0]) {
            case "/poll":                                                   // -> /poll -j pollID  or  /poll -d pollID
                if (commandTokens[1].equals("-j")) {
                    // Accept pollID
                } else if (commandTokens[1].equals("-d")) {
                    return "SERVER: You have denied entering the pollNumber poll.";
                }
            case "/help":
                // Help
            default:
                return "SERVER: Invalid Command.";
        }
    }
}
