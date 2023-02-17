package files;

import models.clientmodels.ClientModel;
import utils.ArraysHelper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MyUsersFiles extends Files {
    private static final Path usersPath =
            Paths.get(System.getProperty("user.dir"), "ChatroomFiles", "Users.txt");

    protected static void readUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(usersPath.toString()))) {
            users = (Map<String, ClientModel>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writeUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(usersPath.toString()))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    public static ClientModel getUserByName(String username) {
        ClientModel chosenClient = null;
        for (ClientModel client : users.values()) {
            if (client.getUsername().equals(username)) {
                chosenClient = client;
                break;
            }
        }

        return chosenClient;
    }

    public static void save(ClientModel client) {
        readUsers();
        users.put(client.getUsername(), client);
        writeUsers();
    }

    public static void remove(ClientModel client) {
        readUsers();
        users.remove(client.getUsername());
        writeUsers();
    }

    public static boolean contains(String username) {
        readUsers();
        return users.containsKey(username);
    }

    public static boolean contains(ClientModel client) {
        readUsers();
        return users.containsValue(client);
    }

    public static Map<String, ClientModel> getAllUsersDuplicate() {
        readUsers();
        return new HashMap<>(users);
    }
}
