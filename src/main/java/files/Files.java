package files;

import client.models.ClientModel;
import server.models.poll.PollModel;
import server.models.servermessage.ServerMessageModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Files {
    private static final Path path =
            Paths.get(System.getProperty("user.dir"), "ChatroomFiles");

    protected static ArrayList<String> activeUsers = new ArrayList<>();
    protected static Map<String, ClientModel> users = new HashMap<>();
    protected static ArrayList<ServerMessageModel> messages = new ArrayList<>();
    protected static ArrayList<PollModel> polls = new ArrayList<>();

    public static void readFiles() {
        new File(path.toString()).mkdir();

        MyUsersFiles.readUsers();
        MyMessagesFiles.readMessages();
        MyActiveUsersFiles.clear();
    }
}
