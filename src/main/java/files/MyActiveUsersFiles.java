package files;

import client.models.ClientModel;
import utils.ArraysHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyActiveUsersFiles extends Files {
    private static final String activeUsersPath = path + "\\ActiveUsers.txt";

    protected static void readActiveUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(activeUsersPath));) {
            activeUsers = (ArrayList<String>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writeActiveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(activeUsersPath))) {
            out.writeObject(activeUsers);
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    public static void save(String onlineClient) {
        readActiveUsers();
        activeUsers.add(onlineClient);
        writeActiveUsers();
    }

    public static void remove(String onlineClient) {
        readActiveUsers();
        activeUsers.remove(onlineClient);
        writeActiveUsers();
    }

    public static boolean contains(String username) {
        readActiveUsers();
        return activeUsers.contains(username);
    }

    public static boolean contains(ClientModel client) {
        readActiveUsers();
        return activeUsers.contains(client.getUsername());
    }

    public static ArrayList<String> getAllActiveUsersDuplicate() {
        readActiveUsers();
        return new ArrayList<>(activeUsers);
    }
}
