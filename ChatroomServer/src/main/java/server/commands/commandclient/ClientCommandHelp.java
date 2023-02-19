package server.commands.commandclient;

import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import static utils.ConsoleDetail.*;

public class ClientCommandHelp {
    protected static final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;

    protected static final String helpCmd = CYAN_BOLD_BRIGHT + "`/help`" + RESET;
    protected static final String helpDescription = WHITE_BOLD_BRIGHT + "To see a list of available commands" + RESET;
    protected static final String messageUserCmd = CYAN_BOLD_BRIGHT + "`/message username 'Your message'`" + RESET;
    protected static final String messageUserDescription = WHITE_BOLD_BRIGHT + "To send a private message to a user" + RESET;
    protected static final String messageAdminCmd = CYAN_BOLD_BRIGHT + "`/message admin 'Your message'`" + RESET;
    protected static final String messageAdminDescription = WHITE_BOLD_BRIGHT + "To send a private message to the server administer" + RESET;
    protected static final String pollCmd = CYAN_BOLD_BRIGHT + """
            `/poll -join uniqueName -v optionIndex`
            
            `/poll -show uniqueName`
            `/poll -show pollID`
            
            `/poll -show-all`
            `/poll -show-all-detail`
            """ + RESET;
    protected static final String pollDescription = WHITE_BOLD_BRIGHT + "To join or see Chatroom Polls" + RESET;
    protected static final String exitCmd = CYAN_BOLD_BRIGHT + "`/exit`" + RESET;
    protected static final String exitDescription = WHITE_BOLD_BRIGHT + "To exit the chatroom" + RESET;


    protected static ServerMessageModel helpCommand(String[] commandTokens) {
        if (commandTokens.length == 1) {
            return getHelpList();
        } else
            return getInvalidHelpCommandMsg();
    }

    private static String helpList() {
        return WHITE_BOLD_BRIGHT + "Here's a list of available commands:" + RESET + "\n\n"
                + helpCmd + indicator + helpDescription + "\n\n"
                + messageUserCmd + indicator + messageUserDescription + "\n"
                + messageAdminCmd + indicator + messageAdminDescription + "\n\n"
                + pollCmd + "\t\t\t" + indicator + pollDescription + "\n\n"
                + exitCmd + indicator + exitDescription + "\n";
    }

    private static ServerMessageModel getHelpList() {
        return new ServerMessageModel(ServerMessageMode.ListFromServer, helpList());
    }

    private static ServerMessageModel getInvalidHelpCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "Please Use the /help command correctly.");
    }
}
