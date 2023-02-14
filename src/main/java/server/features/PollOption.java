package server.features;

public class PollOption {

    private String content;

    private PollOption(String content) {
        this.content = content;
    }

    public static PollOption factory(String content)
    {
        return new PollOption(content);
    }

}
