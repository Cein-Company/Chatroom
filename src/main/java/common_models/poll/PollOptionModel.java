package common_models.poll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PollOptionModel implements Serializable {

    private final String content;
    private final int index;
    private int selectedCount = 0;

    private PollOptionModel(String content, int index) {
        this.content = content;
        this.index = index;
    }

    public static PollOptionModel factory(String content, int index) {
        return new PollOptionModel(content, index);
    }

    public static List<PollOptionModel> generateOptions(String[] contents) {
        List<PollOptionModel> polls = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            polls.add(factory(contents[i], i + 1));
        }
        return polls;
    }

    public void selected() {selectedCount++;}

    @Override
    public String toString() {
        return content;
    }

    public String getContent() {
        return content;
    }

    public int getIndex() {
        return index;
    }

    public int getSelectedCount() {
        return selectedCount;
    }
}
