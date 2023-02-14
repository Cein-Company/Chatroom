package server.commandserver;

import static utils.consts.ConsoleDetail.*;

public class ServerCommandHelp {
    private static final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;
    private static final String help = CYAN_BOLD_BRIGHT + "/help" + RESET;
    private static final String helpDescription = WHITE_BOLD_BRIGHT + "To see a list of available commands" + RESET;


    public static String helpCommand() {
        return "\n" + WHITE_BOLD_BRIGHT + "Here's a list of available commands:" + RESET + "\n\n"
                + help + indicator + helpDescription + "\n\n";
    }
}
