package server.commandclient;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static client.ChatClientCLI.getActiveUsers;
import static client.ChatClientCLI.getUsers;
import static utils.consts.ConsoleDetail.*;

public class ClientCommandMessage {
    protected static String messageCommand(String clientUsername, String[] commandTokens) {
        if (getActiveUsers().contains(commandTokens[1]) || commandTokens[1].toLowerCase(Locale.ROOT).equals("server")) {
            String target = commandTokens[1].toLowerCase(Locale.ROOT).equals("server") ?
                    commandTokens[1].toLowerCase(Locale.ROOT) : getUsers().get(commandTokens[1]).getColoredUsername();
            String messageTime = WHITE_BOLD_BRIGHT + getCurrentTime() + RESET;
            String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;
            String announcement = RED_BOLD_BRIGHT + "PM FROM " + RESET;
            String senderColoredUsername = getUsers().get(clientUsername).getColoredUsername();
            String colon = CYAN_BOLD_BRIGHT + ": " + RESET;
            String message = WHITE_BOLD_BRIGHT + join(commandTokens, commandTokens[2]) + RESET;

            String toBeSentMessage = messageTime + indicator + announcement + senderColoredUsername + colon + message;

            return target + " " + toBeSentMessage;
        } else if (getUsers().containsKey(commandTokens[1])) {
            String target = getUsers().get(commandTokens[1]).getColoredUsername();

            return RED_BOLD_BRIGHT + "SERVER: " + RESET + target + RED_BOLD_BRIGHT + " is not online at the moment." + RESET;
        } else {
            return RED_BOLD_BRIGHT + "SERVER: No such user was found in the server." + RESET;
        }
    }

    private static String join(String[] tokens, String from) {
        return String.join(
                " ",
                Arrays.copyOfRange(
                        tokens,
                        Arrays.asList(tokens).indexOf(from),
                        tokens.length));
    }

    private static String getCurrentTime() {
        return dateFormat.format(new Date());
    }
}
