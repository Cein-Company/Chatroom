package server.commandclient;

import static client.ChatClientCLI.getUsers;
import static client.ChatClientCLI.getUsersFromFile;
import static utils.ConsoleDetail.*;

public class ClientCommandHelp {
    protected static final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;

    protected static final String helpCmd = CYAN_BOLD_BRIGHT + "`/help`" + RESET;
    protected static final String helpDescription = WHITE_BOLD_BRIGHT + "To see a list of available commands" + RESET;
    protected static final String messageUserCmd = CYAN_BOLD_BRIGHT + "`/message username 'Your message'`" + RESET;
    protected static final String messageUserDescription = WHITE_BOLD_BRIGHT + "To send a private message to a user" + RESET;
    protected static final String messageServerCmd = CYAN_BOLD_BRIGHT + "`/message server 'Your message'`" + RESET;
    protected static final String messageServerDescription = WHITE_BOLD_BRIGHT + "To send a private message to the server administer" + RESET;
    protected static final String pollJoinCmd = CYAN_BOLD_BRIGHT + "`/poll -j pollNumber`" + RESET;
    protected static final String pollJoinDescription = WHITE_BOLD_BRIGHT + "To accept joining a poll" + RESET;
    protected static final String pollDenyCmd = CYAN_BOLD_BRIGHT + "`/poll -d pollNumber`" + RESET;
    protected static final String pollDenyDescription = WHITE_BOLD_BRIGHT + "To deny joining a poll" + RESET;


    protected static String helpCommand(String clientUsername, String[] commandTokens) {
        if (commandTokens.length == 1) {
            String target = getUsersFromFile().get(clientUsername).getColoredUsername();

            String messageToBeSent = WHITE_BOLD_BRIGHT + "Here's a list of available commands:" + RESET + "\n"
                    + helpCmd + indicator + helpDescription + "\n"
                    + messageUserCmd + indicator + messageUserDescription + "\n"
                    + messageServerCmd + indicator + messageServerDescription + "\n"
                    + pollJoinCmd + indicator + pollJoinDescription + "\n"
                    + pollDenyCmd + indicator + pollDenyDescription + "\n";

            return target + " " + messageToBeSent;
        } else
            return RED_BOLD_BRIGHT + "SERVER: Please Use the /help command correctly.\n" + RESET + indicator + helpCmd;
    }
}
