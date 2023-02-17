package server.models.poll;

import java.io.Serializable;
import java.util.*;

import static utils.ConsoleDetail.*;

// TODO: Move server.models.poll to models package

public class PollModel implements Serializable {

    private final UUID pollId;
    private final String uniqueName;
    private final String title;
    private final List<PollOptionModel> options;
    private Map<UUID, Integer> votes;
    private PollStatus status = PollStatus.OnGoing;

    public PollModel(String title, List<PollOptionModel> options, String uniqueName) {
        this.title = title;
        this.options = options;
        this.uniqueName = uniqueName;
        pollId = UUID.randomUUID();
    }

    public String getTitle() {
        return title;
    }

    public List<PollOptionModel> getOptions() {
        return options;
    }

    public boolean vote(UUID cl, int optionIndex) {
        if (votes == null)
            votes = new HashMap<>();
        if (optionIndex > options.size() || optionIndex < 0)
            return false;
        options.stream().filter(p -> p.getIndex() == optionIndex).forEach(p -> p.selected());
        votes.put(cl, optionIndex);
        return true;

    }

    public String show() {
        String poll = "\n" + WHITE_BRIGHT + "------------------------------------------------------------------------------------\n" + GREEN_BOLD + title + "\n" + RED_BOLD_BRIGHT;
        for (int i = 0; i < options.size(); i++) {
            poll += "\t" + (i + 1) + "- " + options.get(i).toString() + "\t\t" + getOptionPercent(options.get(i));
            poll += "\n\t-----------------------\n";

        }
        poll += GREEN_BOLD + "Unique Name : " + WHITE_BRIGHT + uniqueName + "\t\t" + GREEN_BOLD + "Poll Id : " + WHITE_BRIGHT + pollId + "\n";

        poll += GREEN_BOLD + "Total Votes : " + WHITE_BRIGHT + getTotalVotes() + "\t\t" + GREEN_BOLD + "Selected Options : " + WHITE_BRIGHT + "This!!!" + "\n";
        poll += "\n-------------------------------------------------------------------------------------\n";
        return poll;
    }

    private String getOptionPercent(PollOptionModel option) {
        if (votes == null
                || votes.size() == 0)
            return "0%";
        float percent = (((float) option.getSelectedCount()) / ((float) votes.size())) * 100;
        return String.valueOf(percent) + "%";
    }

    private String getTotalVotes() {
        if (votes == null)
            return String.valueOf(0);
        return String.valueOf(votes.size());
    }

    @Override
    public String toString() {
        return show();
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public UUID getPollId() {
        return pollId;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }
}
