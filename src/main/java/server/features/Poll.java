package server.features;

import client.ClientModel;

import java.util.*;

public class Poll {

    private int code;
    private String title;
    private List<PollOption> options;
    private Map<ClientModel,Integer> answers;

    public Poll(String title, List<PollOption> options) {
        this.title = title;
        this.options = options;
        code = new Random().nextInt(10000,100000);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public void addOption(PollOption option)
    {
        if(options == null)
            options = new ArrayList<>();
        this.options.add(option);
    }
    public void answer(ClientModel cl,int optionCode)
    {
        if(answers == null)
            answers = new HashMap<>();
        answers.put(cl,optionCode);
    }
}
