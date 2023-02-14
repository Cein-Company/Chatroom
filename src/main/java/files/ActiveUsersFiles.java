package files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class ActiveUsersFiles {
    private static final Path activeUsersPath = Paths.get( "ActiveUsers.txt");

    public static void writeActiveUsers(ArrayList<String> activeUsers) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(activeUsersPath.toString()))) {
            out.writeObject(activeUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readActiveUsers() {
        try {
            new File(activeUsersPath.toString()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(activeUsersPath.toString()))) {
            return (ArrayList<String>) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
