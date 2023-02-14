package server;

import files.ServerConfigFile;
import server.config.ServerConfig;
import server.config.ServerMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static server.ChatClientHandler.clients;
import static utils.consts.ConsoleDetail.*;

public class ChatServer {

    private static final ArrayList<String> chatMessages = new ArrayList<>();
    private static ServerConfig config;
    private static ServerSocket serverSocket;
    private static boolean serverOn = false;

    public static void startServer() {
        serverOn = true;

        System.out.println(RED_BOLD_BRIGHT + "SERVER CONNECTED!\n" +
                "Type '/exit' to close the server." + RESET);
        listenForServerExit();

        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println(CYAN_BOLD_BRIGHT + "NEW USER CONNECTED!" + RESET);
                ChatClientHandler client = new ChatClientHandler(socket);
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public static void listenForServerExit() {
        new Thread(() -> {
            String scannedMessage;
            Scanner scanner = new Scanner(System.in);

            while (!serverSocket.isClosed()) {
                if (scanner.hasNext()) {
                    scannedMessage = scanner.nextLine();

                    if (scannedMessage != null && scannedMessage.equals("/exit"))
                        closeServerSocket();
                }
            }
        }).start();
    }

    public static void closeServerSocket() {
        serverOn = false;

        try {
            ArrayList<ChatClientHandler> tempClients = new ArrayList<>(clients);
            for(ChatClientHandler client : tempClients)
                client.closeEverything(client.getSocket(), client.getBufferedReader(), client.getBufferedWriter());

            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isServerOn() {
        return serverOn;
    }

    public static ArrayList<String> getChatMessages() {
        return chatMessages;
    }

    private static void configServer() {
        config = new ServerConfig(ServerMode.OPEN, true, 4444, "");
        ServerConfigFile.writeConfig(config);
    }

    public static void main(String[] args) {
        try {
            config = ServerConfig.factory();
            if(config == null)
                configServer();

            serverSocket = new ServerSocket(config.getPort());
            startServer();
        } catch (IOException e) {
            System.err.println(RED_BOLD_BRIGHT + "Could not listen to port." + RESET);
            e.printStackTrace();
        }
    }
}
