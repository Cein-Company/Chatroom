package files;

import common_models.poll.PollModel;
import common_models.poll.PollStatus;
import utils.ArraysHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

import static files.Files.path;
import static files.Files.polls;

public class MyPollsFile {

    private static final String pollPath = path + "\\Polls.txt";

    protected static void readPolls() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pollPath));) {
            polls = (ArrayList<PollModel>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writePolls() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pollPath))) {
            out.writeObject(polls);
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    public static PollModel getPoll(String detail) {
       readPolls();
       try {
           return polls.stream()
                   .filter(p->p.getUniqueName().equals(detail) || p.getPollId().toString().equals((detail)))
                   .findFirst()
                   .get();
       }catch (Exception exception){
           return null;
       }

    }

    public static ArrayList<PollModel> allPolls() {
        readPolls();
        return  polls;
    }

    public static void addPoll(PollModel poll) {
        readPolls();
        polls.add(poll);
        writePolls();
    }

    public static boolean votePoll(UUID clientId,String detail , int index)
    {
        PollModel poll = getPoll(detail);
        if (poll == null)
            return false;
        poll.vote(clientId,index);
        writePolls();
        return true;
    }

    public static void remove(PollModel poll) {
        readPolls();
        polls.removeIf(e -> e.getPollId() == poll.getPollId() || e.getUniqueName() == poll.getUniqueName());
        writePolls();
    }

    public static boolean changePollStatus(PollStatus status,String detail) {
        PollModel poll = getPoll(detail);
        if(poll == null)
            return false ;

        poll.setStatus(status);
        writePolls();
        return true;
    }

}