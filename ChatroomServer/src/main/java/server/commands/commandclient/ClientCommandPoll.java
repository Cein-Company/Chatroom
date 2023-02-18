package server.commands.commandclient;

import models.clientmodels.ClientModel;
import models.poll.PollModel;
import files.MyPollsFile;
import models.poll.PollStatus;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.ArrayList;
import java.util.Locale;

import static utils.ConsoleDetail.*;

public class ClientCommandPoll {
    protected static ServerMessageModel pollCommand(ClientModel sender, String[] commandTokens) {
        try {
            switch (commandTokens[1].toLowerCase(Locale.ROOT)) {
                case "-join" -> {
                    return joinPoll(sender, commandTokens);
                }
                case "-show" -> {
                    return showPoll(commandTokens);
                }
                case "-show-all" -> {
                    return showAllPolls();
                }
                case "-show-all-detail" -> {
                    return showAllPollsDetails();
                }
                default -> {
                    return getInvalidSecondArg();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return getInvalidPollCommand();
        }
    }

    private static ServerMessageModel joinPoll(ClientModel sender, String[] commandTokens) {
        String identifier = commandTokens[2];
        PollModel poll = MyPollsFile.getPoll(identifier);

        if (poll == null)
            return getPollNotFound();

        if (commandTokens[3].toLowerCase(Locale.ROOT).equals("-v")) {
            int optionIndex = Integer.parseInt(commandTokens[4]) - 1;
            
            if (poll.getStatus().equals(PollStatus.End))
                return getPollEndedMsg(poll);

            if (optionIndex > poll.getOptions().size() || optionIndex < 0)
                return getSelectCorrectOptionMsg();

            if (poll.getVotes() != null && poll.getVotes().containsKey(sender.getClientId()))
                return getCantVoteTwiceMsg();

            MyPollsFile.votePoll(sender, poll, optionIndex);

            return getYouVotedMsg(poll, optionIndex);
        } else {
            System.out.println(commandTokens[3]);
            return getInvalidPollCommand();
        }
    }

    private static ServerMessageModel showPoll(String[] commandTokens) {
        String identifier = commandTokens[2];
        PollModel poll = MyPollsFile.getPoll(identifier);

        if (poll == null)
            return getPollNotFound();

        return new ServerMessageModel(ServerMessageMode.ListFromServer, poll.show());
    }

    private static ServerMessageModel showAllPolls() {
        ArrayList<PollModel> polls = MyPollsFile.allPolls();

        if (polls == null || polls.size() == 0)
            return getNoPollsMsg();

        StringBuilder pollsToString = new StringBuilder();
        for (PollModel poll : polls)
            pollsToString.append(poll.show());

        return new ServerMessageModel(ServerMessageMode.ListFromServer, pollsToString.toString());
    }

    private static ServerMessageModel showAllPollsDetails() {
        ArrayList<PollModel> polls = MyPollsFile.allPolls();

        if (polls == null || polls.size() == 0)
            return getNoPollsMsg();

        StringBuilder details = new StringBuilder("\n");
        for (PollModel poll : polls) {
            details
                    .append(GREEN_BOLD_BRIGHT + "Unique Name : ")
                    .append(WHITE_BOLD_BRIGHT).append(poll.getUniqueName()).append("\n\t")
                    .append(BLUE_BOLD_BRIGHT).append("Poll ID : ")
                    .append(WHITE_BOLD_BRIGHT).append(poll.getPollID()).append("\n");
        }

        return new ServerMessageModel(ServerMessageMode.ListFromServer, details.toString());
    }

    private static ServerMessageModel getYouVotedMsg(PollModel poll, int optionIndex) {
        return new ServerMessageModel(ServerMessageMode.FromServer, "You voted for '" + poll.getOptions().get(optionIndex) + "'.");
    }

    private static ServerMessageModel getCantVoteTwiceMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "You already voted in this poll.");
    }

    private static ServerMessageModel getSelectCorrectOptionMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "Please select a correct option.");
    }

    private static ServerMessageModel getPollNotFound() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "No such poll was found.");
    }

    private static ServerMessageModel getNoPollsMsg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "No polls exists in the chatroom yet.");
    }

    private static ServerMessageModel getPollEndedMsg(PollModel poll) {
        return new ServerMessageModel(ServerMessageMode.FromServer,
                poll.getUniqueName() + " poll has ended. " +
                        "The result was: '" + poll.getResult() + "'.");
    }

    private static ServerMessageModel getInvalidSecondArg() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "Invalid Second Argument.");
    }

    private static ServerMessageModel getInvalidPollCommand() {
        return new ServerMessageModel(ServerMessageMode.FromServer, "Please Use the /poll command correctly.");
    }
}
