package server.commandserver;

import files.MyMessagesFiles;
import server.models.servermessage.ServerMessageMode;
import server.models.servermessage.ServerMessageModel;
import utils.ArraysHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Locale;

public class ServerCommandLog {
    protected static ServerMessageModel logCommand(String[] commandTokens) {
        StringBuilder logColored = new StringBuilder();
        StringBuilder logColorless = new StringBuilder();

        if (!MyMessagesFiles.getAllMessagesDuplicate().isEmpty()) {
            for (ServerMessageModel messageModel : MyMessagesFiles.getAllMessagesDuplicate())
                logColored.append("\n").append(messageModel.getFullMessage());
            for (ServerMessageModel messageModel : MyMessagesFiles.getAllMessagesDuplicate())
                logColorless.append("\n").append(messageModel.getColorlessMessage());
        } else
            return geEmptyHistoryMsg();

        if (commandTokens.length == 3 && commandTokens[1].toLowerCase(Locale.ROOT).equals("-s")) {
            try {
                String path = commandTokens[2].endsWith("/") ?
                        commandTokens[2] + "log.txt" : commandTokens[2] + "/log.txt";

                if (new File(commandTokens[2]).exists()) {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
                    bufferedWriter.write(logColorless.toString());
                    bufferedWriter.close();

                    return getSavedLogMsg(path);
                } else
                    return getNoPathMsg(path);
            } catch (Exception exception) {
                return getExceptionMsg(exception);
            }
        } else if (commandTokens.length == 2 && commandTokens[1].toLowerCase(Locale.ROOT).equals("-c")) {
            MyMessagesFiles.clear();

            return getLogCleared();
        } else if (commandTokens.length == 1)
            return getLogMsg(logColored);
        else
            return getInvalidLogCommandMsg();
    }

    private static ServerMessageModel getSavedLogMsg(String path) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Log file saved in: " + path);
    }

    private static ServerMessageModel getNoPathMsg(String path) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "The path '" + path + "' does not exist.");
    }

    private static ServerMessageModel geEmptyHistoryMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Message History is empty.");
    }

    private static ServerMessageModel getLogCleared() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Message History was cleared.");
    }

    private static ServerMessageModel getLogMsg(StringBuilder log) {
        return new ServerMessageModel(ServerMessageMode.ListFromServer, log.toString());
    }

    private static ServerMessageModel getExceptionMsg(Exception e) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
    }

    private static ServerMessageModel getInvalidLogCommandMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /log command correctly.");
    }
}
