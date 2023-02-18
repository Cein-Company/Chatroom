package files;

import models.clientmodels.ClientModel;
import models.poll.PollModel;
import models.poll.PollStatus;
import utils.ArraysHelper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import static files.Files.polls;

public class MyPollsFile {
    private static final Path pollPath =
            Paths.get(System.getProperty("user.dir"), "ChatroomFiles", "Polls.txt");

    protected static void readPolls() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pollPath.toString()));) {
            polls = (ArrayList<PollModel>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writePolls() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pollPath.toString()))) {
            out.writeObject(polls);
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    public static PollModel getPoll(String identifier) {
        readPolls();
        try {
            return polls.stream()
                    .filter(p -> p.getUniqueName().equals(identifier) || p.getPollID().toString().equals((identifier)))
                    .findFirst()
                    .get();
        } catch (Exception exception) {
            return null;
        }

    }

    public static ArrayList<PollModel> allPolls() {
        readPolls();
        return polls;
    }

    public static void addPoll(PollModel poll) {
        readPolls();
        polls.add(poll);
        writePolls();
    }

    public static void votePoll(ClientModel clientModel, PollModel poll, int optionIndex) {
        poll.vote(clientModel, optionIndex);

        writePolls();
    }

    public static void remove(PollModel poll) {
        readPolls();
        polls.removeIf(e -> e.getPollID() == poll.getPollID() || e.getUniqueName() == poll.getUniqueName());
        writePolls();
    }

    public static void changePollStatus(PollStatus status, PollModel poll) {
        poll.setStatus(status);
        writePolls();
    }

    public static boolean containsUName(String uniqueName) {
        readPolls();

        for (PollModel poll : polls) {
            if (poll.getUniqueName().equals(uniqueName))
                return true;
        }

        return false;
    }
}