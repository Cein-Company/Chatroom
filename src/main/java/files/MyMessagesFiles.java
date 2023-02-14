package files;

import server.models.ServerMessageModel;
import utils.ArraysHelper;

import java.io.*;
import java.util.ArrayList;

public class MyMessagesFiles extends Files {
    private static final String messagesPath = path + "\\Messages.txt";

    protected static void readMessages() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(messagesPath));) {
            messages = (ArrayList<ServerMessageModel>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writeMessages() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(messagesPath))) {
            out.writeObject(messages);
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    public static void save(ServerMessageModel message) {
        readMessages();
        messages.add(message);
        writeMessages();
    }

    public static void clear() {
        readMessages();
        messages.clear();
        writeMessages();
    }

    public static ArrayList<ServerMessageModel> getAllMessagesDuplicate() {
        readMessages();
        return new ArrayList<>(messages);
    }
}
