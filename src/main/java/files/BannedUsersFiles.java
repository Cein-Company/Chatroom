package files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BannedUsersFiles {
    private static final Path bannedUsersPath = Paths.get("D:\\" + "BannedUsers.txt");

    public static void writeBannedUsers(ArrayList<String> bannedUsers) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(bannedUsersPath.toString()))) {
            out.writeObject(bannedUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readBannedUsers() {
        try {
            new File(bannedUsersPath.toString()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(bannedUsersPath.toString()))) {
            return (ArrayList<String>) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
