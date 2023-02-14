package files;

import client.models.ClientModel;
import server.models.ServerMessageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Files {
    protected static final String path = "D:\\ChatroomFiles";

    protected static ArrayList<String> activeUsers = new ArrayList<>();
    protected static Map<String, ClientModel> users = new HashMap<>();
    protected static ArrayList<ServerMessageModel> messages = new ArrayList<>();

    public static void readFiles() {
        new File(path).mkdir();

        MyUsersFiles.readUsers();
        MyMessagesFiles.readMessages();
        MyActiveUsersFiles.readActiveUsers();
    }


}
