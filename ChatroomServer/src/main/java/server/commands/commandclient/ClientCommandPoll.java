package server.commands.commandclient;

import models.clientmodels.ClientModel;
import models.poll.PollModel;
import files.MyPollsFile;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.ArrayList;

import static utils.ConsoleDetail.*;

public class ClientCommandPoll {
    protected static ServerMessageModel pollCommand(ClientModel sender, String[] commandTokens) {
        try {
            switch (commandTokens[1].replaceFirst("-", "").trim()) {
                case "join" -> {
                    String detail = commandTokens[2];
                    MyPollsFile.getPoll(detail);
                    if (commandTokens[3].equals("-v")) {
                        int optionIndex = Integer.parseInt(commandTokens[4]);
                        if (MyPollsFile.votePoll(sender.getClientId(), detail, optionIndex)) {
                            return new ServerMessageModel(ServerMessageMode.FromServer, RED + "Voted !!!" + RESET);
                        } else
                            return new ServerMessageModel(ServerMessageMode.FromServer, RED + "An error occurred during operation" + RESET);
                    }
                }
                case "show" -> {
                    String detail = commandTokens[2];
                    PollModel poll = MyPollsFile.getPoll(detail);
                    if (poll == null)
                        return new ServerMessageModel(ServerMessageMode.FromServer, RED_BRIGHT + "Couldn't find any poll with input information" + RESET);

                    return new ServerMessageModel(ServerMessageMode.FromServer, poll.show());
                }
                case "show-all" -> {
                    ArrayList<PollModel> polls = MyPollsFile.allPolls();
                    if (polls == null || polls.size() == 0)
                        return new ServerMessageModel(ServerMessageMode.FromServer, RED_BRIGHT + "There isn't any poll" + RESET);
                    StringBuilder pollsToString = new StringBuilder();
                    for (PollModel poll : polls) {
                        pollsToString.append(poll.show());
                    }
                    return new ServerMessageModel(ServerMessageMode.FromServer, pollsToString.toString());
                }
                case "show-all-detail" -> {
                    ArrayList<PollModel> polls = MyPollsFile.allPolls();
                    if (polls == null || polls.size() == 0)
                        return new ServerMessageModel(ServerMessageMode.FromServer, RED_BRIGHT + "There isn't any poll" + RESET);
                    StringBuilder details = new StringBuilder("\n");
                    for (PollModel poll : polls) {
                        details.append(GREEN_BOLD + "Unique Name : " + WHITE_BRIGHT).append(poll.getUniqueName()).append("\t\t").append(GREEN_BOLD).append("Poll Id : ").append(WHITE_BRIGHT).append(poll.getPollId()).append("\n");
                    }
                    return new ServerMessageModel(ServerMessageMode.FromServer, details.toString());
                }
            }
        } catch (Exception exception) {
            return new ServerMessageModel(ServerMessageMode.FromServer, RED + "Use command correctly !" + RESET);
        }
        return null;
    }
}
