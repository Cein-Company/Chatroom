package files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChatMessagesFiles {
    private static final Path chatMessagesPath = Paths.get("ChatMessages.txt");

    public static void writeChatMessages(ArrayList<String> chatMessages) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(chatMessagesPath.toString()))) {
            out.writeObject(chatMessages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readChatMessages() {
        try {
            new File(chatMessagesPath.toString()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(chatMessagesPath.toString()))) {
            return (ArrayList<String>) in.readObject();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
