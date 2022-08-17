package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static utils.consts.ConsoleColors.*;

public record ChatServer(ServerSocket serverSocket) {
    public void startServer() {
        System.out.println(RED_BOLD_BRIGHT + "SERVER CONNECTED!\n" +
                "Type 'exit' to close the server." + RESET);
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

    public void closeServerSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForServerExit() {
        new Thread(() -> {
            String scannedMessage;
            Scanner scanner = new Scanner(System.in);

            while (!serverSocket.isClosed()) {
                if (scanner.hasNext()) {
                    scannedMessage = scanner.nextLine();
                    if (scannedMessage.equals("exit"))
                        closeServerSocket();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            ChatServer chatServer = new ChatServer(serverSocket);
            chatServer.startServer();
        } catch (IOException e) {
            System.err.println(RED_BOLD_BRIGHT + "Could not listen to port." + RESET);
            e.printStackTrace();
        }
    }
}
