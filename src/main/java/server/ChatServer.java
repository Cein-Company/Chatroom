package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static utils.consts.ConsoleColors.*;

public class ChatServer {
    private static ServerSocket serverSocket;
    static Map<UUID, ChatClientRunnable> clients;

    public static void main(String[] args) {
        final int portNumber = 4444;

        try {
            serverSocket = new ServerSocket(portNumber);
            acceptClients();
        } catch (IOException e) {
            System.err.println(RED_BOLD_BRIGHT + "Could not listen to port." + RESET);
            e.printStackTrace();
        }
    }

    private static void acceptClients() {
        clients = new HashMap<>();
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println(CYAN_BOLD_BRIGHT + "NEW USER CONNECTED" + RESET);
                UUID id = UUID.randomUUID();
                ChatClientRunnable client = new ChatClientRunnable(socket, id);
                Thread thread = new Thread(client);
                thread.start();
                clients.put(id, client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
