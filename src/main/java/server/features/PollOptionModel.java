package server.features;

import java.util.ArrayList;
import java.util.List;

public class PollOptionModel {

    private String content;

    private PollOptionModel(String content) {
        this.content = content;
    }

    public static PollOptionModel factory(String content)
    {
        return new PollOptionModel(content);
    }

    public static List<PollOptionModel> generateOptions(String[] contents) {
        List<PollOptionModel> polls = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            polls.add(factory(contents[i]));
        }
        return polls;
    }

    @Override
    public String toString() {
        return content;
    }
}
