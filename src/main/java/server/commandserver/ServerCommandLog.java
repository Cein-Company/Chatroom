package server.commandserver;

import files.ChatMessagesFiles;
import utils.ArraysHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Locale;

import static server.commandserver.ServerCommandHelp.*;
import static utils.ConsoleDetail.*;

public class ServerCommandLog {
    protected static String logCommand(String[] commandTokens) {
        String[] log = new String[1];
        ChatMessagesFiles.readChatMessages().forEach(m -> {
            if (m != null)
                log[0] += m + "\n";
        });

        if (commandTokens.length == 3 && commandTokens[1].toLowerCase(Locale.ROOT).equals("-s")) {
            try {
                String path = commandTokens[2] + "log.txt";
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
                bufferedWriter.write(log[0]);
                bufferedWriter.close();
                return (GREEN + "Log Saved in: " + path + RESET);
            } catch (Exception e) {
                return e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace());
            }
        } else if (commandTokens.length == 1){
            return log[0];
        } else {
            return RED_BOLD_BRIGHT + "Please Use the /log command correctly." + RESET + indicator + logCmd;
        }
    }
}
