package server.models;

import client.models.ClientModel;

import java.util.*;

public class PollModel {

    private final int code;
    private String title;
    private List<PollOptionModel> options;
    private Map<ClientModel, Integer> answers;

    public PollModel(String title, List<PollOptionModel> options) {
        this.title = title;
        this.options = options;
        code = new Random().nextInt(10000, 1000000);
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PollOptionModel> getOptions() {
        return options;
    }

    public void setOptions(List<PollOptionModel> options) {
        this.options = options;
    }

    public void addOption(PollOptionModel option) {
        if (options == null)
            options = new ArrayList<>();
        this.options.add(option);
    }

    public void answer(ClientModel cl, int optionCode) {
        if (answers == null)
            answers = new HashMap<>();
        answers.put(cl, optionCode);
    }

    public String show() {
        String poll = "--------------------------------------------------\n" + title + "\n";
        for (int i = 0; i < options.size(); i++) {
            poll += (i + 1) + "- " + options.get(i).toString() + "\n";
        }
        poll += "--------------------------------------------------";
        return poll;

    }

    @Override
    public String toString() {
        return show();
    }
}
