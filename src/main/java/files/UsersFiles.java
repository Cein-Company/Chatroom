package files;

import client.ClientModel;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class UsersFiles {
    private static final Path usersPath = Paths.get("Users.txt");

    public static void writeUsers(Map<String, ClientModel> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(usersPath.toString()))) {
            out.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ClientModel> readUsers() {
        try {
            new File(usersPath.toString()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(usersPath.toString()))) {
            return (Map<String, ClientModel>) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
