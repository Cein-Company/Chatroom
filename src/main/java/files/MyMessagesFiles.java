package files;

import models.servermessage.ServerMessageModel;
import utils.ArraysHelper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MyMessagesFiles extends Files {
    private static final Path messagesPath =
            Paths.get(System.getProperty("user.dir"), "ChatroomFiles", "Messages.txt");

    protected static void readMessages() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(messagesPath.toString()));) {
            messages = (ArrayList<ServerMessageModel>) in.readObject();
        } catch (FileNotFoundException | EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage() + "\n" + ArraysHelper.toString(e.getStackTrace()));
        }
    }

    protected static void writeMessages() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(messagesPath.toString()))) {
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
