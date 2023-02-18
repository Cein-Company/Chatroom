package models.poll;

import models.clientmodels.ClientModel;

import java.io.Serializable;
import java.util.*;

import static utils.ConsoleDetail.*;

public class PollModel implements Serializable {
    private final UUID pollID;
    private final String uniqueName;
    private final String title;
    private final String coloredTitle;
    private final List<PollOptionModel> options;
    private Map<UUID, Integer> votes;
    private PollStatus status = PollStatus.OnGoing;

    public PollModel(String title, List<PollOptionModel> options, String uniqueName) {
        this.title = title;
        this.coloredTitle = GREEN_BOLD_BRIGHT + title + RESET;
        this.options = options;
        this.uniqueName = uniqueName;
        this.pollID = UUID.randomUUID();
    }

    public String getTitle() {
        return title;
    }

    public List<PollOptionModel> getOptions() {
        return options;
    }

    public void vote(ClientModel clientModel, int optionIndex) {
        if (votes == null)
            votes = new HashMap<>();

        options.stream().filter(p -> p.getIndex() == optionIndex).forEach(PollOptionModel::selected);
        votes.put(clientModel.getClientId(), optionIndex);
    }

    public String show() {
        String line =
                WHITE_BOLD_BRIGHT + "----------------------------------------------------------------------------------" + RESET;
        String smallLine = RED_BOLD_BRIGHT + "-------------------------------------" + RESET;

        StringBuilder poll = new StringBuilder();

        poll.append(line).append("\n")
                .append(BLUE_BOLD_BRIGHT + "Poll: " + RESET)
                .append(coloredTitle).append("\n");

        poll.append("\t").append(smallLine).append("\n");

        for (int i = 0; i < options.size(); i++) {
            poll.append("\t").append(PURPLE_BOLD_BRIGHT).append(getOptionPercent(options.get(i))).append(RESET)
                    .append("   ").append(RED_BOLD_BRIGHT).append(i + 1).append(". ")
                    .append(options.get(i).toString()).append(RESET).append("\n");

            poll.append("\t").append(smallLine).append("\n");
        }

        poll.append("\n")
                .append(GREEN_BOLD_BRIGHT + "Unique Name : " + RESET)
                .append(BLUE_BOLD_BRIGHT).append(uniqueName).append(RESET).append(getWhiteSpaceForPollID())
                .append(GREEN_BOLD_BRIGHT + "Poll ID : " + RESET)
                .append(WHITE_BOLD_BRIGHT).append(pollID).append(RESET).append("\n");


        poll.append(GREEN_BOLD_BRIGHT + "Total Votes : " + RESET)
                .append(WHITE_BOLD_BRIGHT).append(getTotalVotes()).append(RESET).append(getWhiteSpaceForResult())
                .append("\t").append(GREEN_BOLD_BRIGHT + "Result : ")
                .append(BLUE_BOLD_BRIGHT).append(getResult()).append(RESET).append("\n");

        poll.append(line);

        return poll.toString();
    }

    private String getWhiteSpaceForPollID() {
        int spaces;

        if (uniqueName.length() < 22)
            spaces = 22 - uniqueName.length();
        else
            spaces = 5;

        return String.format("%1$" + spaces + "s", "");
    }

    private String getWhiteSpaceForResult() {
        int spaces;

        if (Integer.parseInt(getTotalVotes()) < 10)
            spaces = 20;
        else
            spaces = 18;

        return String.format("%1$" + spaces + "s", "");
    }

    private String getOptionPercent(PollOptionModel option) {
        if (votes == null
                || votes.size() == 0)
            return "0%";

        float percent = (((float) option.getSelectedCount()) / ((float) votes.size())) * 100;

        return Math.round(percent * Math.pow(10, 2)) / Math.pow(10, 2) + "%";
    }

    private String getTotalVotes() {
        if (votes == null)
            return String.valueOf(0);

        return String.valueOf(votes.size());
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public UUID getPollID() {
        return pollID;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

    public String getResult() {
        String result = "NONE";
        PollOptionModel resultOption = options.get(0);

        if (votes != null && votes.size() != 0) {
            for (PollOptionModel option : options) {
                if (option.getSelectedCount() > resultOption.getSelectedCount()) {
                    resultOption = option;
                    result = resultOption.getContent();
                } else if (option.getSelectedCount() == resultOption.getSelectedCount()) {
                    result = "TIE";
                }
            }
        }

        return result;
    }

    public Map<UUID, Integer> getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        return show();
    }
}
