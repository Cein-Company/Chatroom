package server.commandserver;

import server.ChatServer;

import java.util.Arrays;

import static server.ChatServer.SERVER_COMMANDS;

public class ServerCommandHandler {
    public static String commandHandler(String serverMessage) {
        String[] commandTokens = serverMessage.split("\\s+");

        switch (commandTokens[0]) {
            case "/help":                                                                                  //     /help
                // Help
            case "/commands":
                return (Arrays.toString(SERVER_COMMANDS));
            case "/log":                                                                                    //     /log
                return (logCommand(commandTokens));
            case "/members":                                                                            //     /members
                return (membersCommand());
            case "/kick":                                                                       //     /kick clientName
                // Kick
            case "/ban":                                                                        //     /band clientName
                // Ban
            case "/config":
                return (configCommand(commandTokens));
            case "/poll":                          //     /poll -q 'question sentence' -o 'option one, option two, ...'
                // Poll
            case "/exit":
                ChatServer.closeServerSocket();
            default:
                return "Invalid Command";
        }
    }

    private static String logCommand(String[] commandTokens) {

        return null;
    }

    private static String configCommand(String[] commandTokens) {

        return null;
    }

    private static String membersCommand() {

        return null;
    }
}
