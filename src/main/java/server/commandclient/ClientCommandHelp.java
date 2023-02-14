package server.commandclient;

import static client.ChatClientCLI.getUsers;
import static utils.consts.ConsoleDetail.*;

public class ClientCommandHelp {
    private static final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;

    private static final String help = CYAN_BOLD_BRIGHT + "/help" + RESET;
    private static final String helpDescription = WHITE_BOLD_BRIGHT + "To see a list of available commands" + RESET;
    private static final String messageUser = CYAN_BOLD_BRIGHT + "/message username 'Your message'" + RESET;
    private static final String messageUserDescription = WHITE_BOLD_BRIGHT + "To send a private message to a user" + RESET;
    private static final String messageServer = CYAN_BOLD_BRIGHT + "/message server 'Your message'" + RESET;
    private static final String messageServerDescription = WHITE_BOLD_BRIGHT + "To send a private message to the server administer" + RESET;
    private static final String pollJoin = CYAN_BOLD_BRIGHT + "/poll -j pollNumber" + RESET;
    private static final String pollJoinDescription = WHITE_BOLD_BRIGHT + "To accept joining a poll" + RESET;
    private static final String pollDeny = CYAN_BOLD_BRIGHT + "/poll -d pollNumber" + RESET;
    private static final String pollDenyDescription = WHITE_BOLD_BRIGHT + "To deny joining a poll" + RESET;


    protected static String helpCommand(String clientUsername) {
        String target = getUsers().get(clientUsername).getColoredUsername();

        String messageToBeSent = WHITE_BOLD_BRIGHT + "Here's a list of available commands:" + RESET + "\n"
                + help + indicator + helpDescription + "\n"
                + messageUser + indicator + messageUserDescription + "\n"
                + messageServer + indicator + messageServerDescription + "\n"
                + pollJoin + indicator + pollJoinDescription + "\n"
                + pollDeny + indicator + pollDenyDescription + "\n";

        return target + " " + messageToBeSent;
    }
}
