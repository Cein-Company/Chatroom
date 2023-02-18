package server.commands.commandserver;

import models.poll.PollStatus;
import files.MyPollsFile;
import models.poll.PollModel;
import models.poll.PollOptionModel;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static utils.ConsoleDetail.*;

public class ServerCommandPoll {
    protected static ServerMessageModel pollCommand(String[] commandTokens) {
        try {
            switch (commandTokens[1].toLowerCase(Locale.ROOT)) {
                case "-create" -> {
                    return createPoll(commandTokens);
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
                case "-end" -> {
                    return endPoll(commandTokens);
                }
                default -> {
                    return getInvalidSecondArg();
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            return getInvalidPollCommand();
        }
    }

    private static ServerMessageModel createPoll(String[] commandTokens) {
        StringBuilder title = new StringBuilder();
        String uniqueName = null;
        List<PollOptionModel> options = new ArrayList<>();

        for (int i = 2; i < commandTokens.length; i++) {
            String arg = commandTokens[i];

            if (arg.charAt(0) == '-') {
                switch (arg.toLowerCase(Locale.ROOT)) {
                    case "-t" -> {
                        if (commandTokens[i + 1].charAt(0) == '-')
                            return getTitleCantBeEmpty();

                        for (int j = i + 1; j < commandTokens.length; j++) {
                            if (commandTokens[j].charAt(0) == '-')
                                break;

                            title.append(commandTokens[j]).append(" ");
                        }
                    }
                    case "-o" -> {
                        if (commandTokens[i + 1].charAt(0) == '-')
                            return getOptionCantBeEmpty();

                        StringBuilder option = new StringBuilder();
                        for (int j = i + 1; j < commandTokens.length; j++) {
                            if (commandTokens[j].charAt(0) == '-')
                                break;

                            option.append(commandTokens[j]).append(" ");
                        }

                        options.add(PollOptionModel.factory(option.toString().trim(), options.size()));
                    }
                    case "-uname" -> {
                        if (commandTokens.length == i + 1)
                            return getUNameCantBeEmpty();

                        uniqueName = commandTokens[i + 1];

                        if (uniqueName.length() >= 22) {
                            return getUNameTooBigMsg();
                        }

                        if (MyPollsFile.containsUName(uniqueName))
                            return getCantUseDuplicateUNameMsg();
                    }
                }
            }
        }

        if (title.isEmpty())
            return getTitleCantBeEmpty();

        if (options.isEmpty())
            return getOptionCantBeEmpty();

        if (options.size() == 1)
            return getOptionCantBeOne();

        if (uniqueName == null)
            return getUNameCantBeEmpty();

        PollModel poll = new PollModel(title.toString(), options, uniqueName);
        MyPollsFile.addPoll(poll);

        return new ServerMessageModel(ServerMessageMode.ListFromServer, poll.show());
    }

    private static ServerMessageModel showPoll(String[] commandTokens) {
        String identifier = commandTokens[2];
        PollModel poll = MyPollsFile.getPoll(identifier);

        if (poll == null)
            return getPollNotFound();

        return new ServerMessageModel(ServerMessageMode.ToAdminister, poll.show());
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

    private static ServerMessageModel endPoll(String[] commandTokens) {
        String identifier = commandTokens[2];
        PollModel poll = MyPollsFile.getPoll(identifier);

        if (poll == null)
            return getPollNotFound();

        MyPollsFile.changePollStatus(PollStatus.End, poll);

        return getPollEndedMsg(poll);
    }

    private static ServerMessageModel getTitleCantBeEmpty() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Poll Title cannot be empty.");
    }

    private static ServerMessageModel getOptionCantBeEmpty() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Poll Options cannot be empty.");
    }

    private static ServerMessageModel getOptionCantBeOne() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "There cannot be only one options in poll.");
    }

    private static ServerMessageModel getUNameCantBeEmpty() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Poll Unique Name cannot be empty.");
    }

    private static ServerMessageModel getUNameTooBigMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister,
                "Poll Unique Name cannot be bigger than 22 letters.");
    }

    private static ServerMessageModel getCantUseDuplicateUNameMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister,
                "This Unique Name is already used for another poll.");
    }

    private static ServerMessageModel getPollNotFound() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No such poll was found.");
    }

    private static ServerMessageModel getNoPollsMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "No polls exists in the chatroom yet.");
    }

    private static ServerMessageModel getPollEndedMsg(PollModel poll) {
        return new ServerMessageModel(ServerMessageMode.ToAdminister,
                poll.getUniqueName() + " poll has ended. " +
                        "The result was: '" + poll.getResult() + "'.");
    }

    private static ServerMessageModel getInvalidSecondArg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Invalid Second Argument.");
    }

    private static ServerMessageModel getInvalidPollCommand() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "Please Use the /poll command correctly.");
    }
}