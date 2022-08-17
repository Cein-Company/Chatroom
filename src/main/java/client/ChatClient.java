package client;

import files.UserFiles;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import static client.ChatClientCLI.*;

public class ChatClient {
    public static void main(String[] args) {
        Map<String, String> temp = UserFiles.readUsers();
        if (temp != null)
            getUsers().putAll(temp);

        final int portNumber = 4444;

        try {
            Socket socket = new Socket(InetAddress.getLoopbackAddress(), portNumber);
            startMenu(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runChat(Socket socket, String username) {
        ChatServerRunnable server = new ChatServerRunnable(socket, username);
        Thread thread = new Thread(server);
        thread.start();
    }
}
