package server.commands.commandserver;

import files.MyMessagesFiles;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;
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
                logColored.append(messageModel.getFullMessage()).append("\n");
            for (ServerMessageModel messageModel : MyMessagesFiles.getAllMessagesDuplicate())
                logColorless.append(messageModel.getColorlessMessage()).append("\n");
        }

        if (commandTokens.length == 3 && commandTokens[1].toLowerCase(Locale.ROOT).equals("-s")) {
            if (MyMessagesFiles.getAllMessagesDuplicate().isEmpty())
                return getEmptyHistoryMsg();

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
            if (MyMessagesFiles.getAllMessagesDuplicate().isEmpty())
                return getEmptyHistoryMsg();

            MyMessagesFiles.clear();

            return getLogCleared();
        } else if (commandTokens.length == 1) {
            if (MyMessagesFiles.getAllMessagesDuplicate().isEmpty())
                return getEmptyHistoryMsg();

            return getLogMsg(logColored);
        }
        else
            return getInvalidLogCommandMsg();
    }

    private static ServerMessageModel getSavedLogMsg(String path) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Log file saved in: " + path);
    }

    private static ServerMessageModel getNoPathMsg(String path) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "The path '" + path + "' does not exist.");
    }

    private static ServerMessageModel getEmptyHistoryMsg() {
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
