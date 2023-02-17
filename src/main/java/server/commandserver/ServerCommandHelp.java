package server.commandserver;

import server.models.servermessage.ServerMessageMode;
import server.models.servermessage.ServerMessageModel;

import static utils.ConsoleDetail.*;


// TODO: Add Poll Command Help
public class ServerCommandHelp {
    protected static final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;
    protected static final String helpCmd = CYAN_BOLD_BRIGHT + "`/help`" + RESET;
    protected static final String helpDescription = WHITE_BOLD_BRIGHT + "To see a list of available commands" + RESET;
    protected static final String logCmd = CYAN_BOLD_BRIGHT + "`/log`" + RESET;
    protected static final String logDescription = WHITE_BOLD_BRIGHT + "To see a list of all messages" + RESET;
    protected static final String logSaveCmd = CYAN_BOLD_BRIGHT + "`/log -s address`" + RESET;
    protected static final String logSaveDescription = WHITE_BOLD_BRIGHT + "To save all messages in a specific address inside the system" + RESET;
    protected static final String logClearCmd = CYAN_BOLD_BRIGHT + "`/log -c`" + RESET;
    protected static final String logClearDescription = WHITE_BOLD_BRIGHT + "To clear the message history" + RESET;
    protected static final String membersCmd = CYAN_BOLD_BRIGHT + "`/members`" + RESET;
    protected static final String membersDescription = WHITE_BOLD_BRIGHT + "To see a list of all chatroom members" + RESET;
    protected static final String onlineMembersCmd = CYAN_BOLD_BRIGHT + "`/members -o`" + RESET;
    protected static final String onlineMembersDescription = WHITE_BOLD_BRIGHT + "To see a list of all online chatroom members" + RESET;
    protected static final String kickCmd = CYAN_BOLD_BRIGHT + "`/kick username`" + RESET;
    protected static final String kickDescription = WHITE_BOLD_BRIGHT + "To kick a user from the chatroom temporarily" + RESET;
    protected static final String banCmd = CYAN_BOLD_BRIGHT + "`/ban username`" + RESET;
    protected static final String banDescription = WHITE_BOLD_BRIGHT + "To ban a user from entering the chatroom forever" + RESET;
    protected static final String unBanCmd = CYAN_BOLD_BRIGHT + "`/ban username -u`" + RESET;
    protected static final String unBanDescription = WHITE_BOLD_BRIGHT + "To unban a banned user" + RESET;
    protected static final String shutdownCmd = CYAN_BOLD_BRIGHT + "`/shutdown`" + RESET;
    protected static final String shutdownDescription = WHITE_BOLD_BRIGHT + "To close and shutdown the server" + RESET;

    public static ServerMessageModel helpCommand(String[] commandTokens) {
        if (commandTokens.length == 1) {
            return getHelpList();
        } else
            return getInvalidHelpCommandMsg();
    }

    private static String helpList() {
        return "\n" + WHITE_BOLD_BRIGHT + "Here's a list of available commands:" + RESET + "\n\n"
                + helpCmd + indicator + helpDescription + "\n"
                + logCmd + indicator + logDescription + "\n"
                + logSaveCmd + indicator + logSaveDescription + "\n"
                + logClearCmd + indicator + logClearDescription + "\n"
                + membersCmd + indicator + membersDescription + "\n"
                + onlineMembersCmd + indicator + onlineMembersDescription + "\n"
                + kickCmd + indicator + kickDescription + "\n"
                + banCmd + indicator + banDescription + "\n"
                + unBanCmd + indicator + unBanDescription + "\n"
                + shutdownCmd + indicator + shutdownDescription;
    }

    private static ServerMessageModel getHelpList() {
        return new ServerMessageModel(ServerMessageMode.ListFromServer, helpList());
    }

    private static ServerMessageModel getInvalidHelpCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /help command correctly.");
    }
}
