package server;

import files.*;
import server.commandserver.CommandHandlerServer;
import server.config.ServerConfig;
import server.config.ServerMode;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static server.ChatClientHandler.getClientHandlers;
import static utils.ConsoleDetail.*;

// TODO: Idea: Set up Unit or Integration Testing of the Chatroom Components:
//       Sign-in, Login, New Message, Commands, Exits
// TODO: Separate Server from Client
// TODO: Fix spacing for '>'

public class ChatServer {
    private static ServerConfig config;
    private static ServerSocket serverSocket;
    private static boolean serverOn = false;

    // TODO: What are these?
    private static final int MAX_PORTS_RANGE = 65536;
    private static final int MIN_PORTS_RANGE = 0;

    public static void startServer() {
        serverOn = true;

        ServerMessageModel turnOnMsg =
                new ServerMessageModel(ServerMessageMode.FromSerer, "THE ADMINISTRATOR OPENED THE SERVER.");
        MyMessagesFiles.save(turnOnMsg);

        System.out.println(RED_BOLD_BRIGHT + "SERVER CONNECTED!\n" +
                "Type '/help' to see a list of available commands.\n" +
                "Type '/exit' to close the server." + RESET);
        listenForServerCommands();

        try {
            while (isServerOn() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // established
                ChatClientHandler clientHandler = new ChatClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            if (isServerOn())
                closeServerSocket();
        }
    }

    public static void listenForServerCommands() {
        new Thread(() -> {
            String scannedCommand;
            Scanner scanner = new Scanner(System.in);

            while (isServerOn() && !serverSocket.isClosed()) {
                System.out.print("\n" + CYAN_BOLD_BRIGHT + ">" + RESET);

                if (scanner.hasNext()) {
                    scannedCommand = scanner.nextLine();

                    ServerMessageModel commandRespond = CommandHandlerServer.commandHandler(scannedCommand);

                    if (commandRespond.getMessageMode().equals(ServerMessageMode.PMFromServerToClient)) {
                        for (ChatClientHandler clientHandler : getClientHandlers()) {
                            clientHandler.messagingAClient(commandRespond);
                            break;
                        }
                    } else if (commandRespond.getMessageMode().equals(ServerMessageMode.ToAdminister) ||
                            commandRespond.getMessageMode().equals(ServerMessageMode.ListFromServer)) {
                        System.out.println(commandRespond.getFullMessage());
                    }
                }
            }
        }).start();
    }

    public static void closeServerSocket() {
        serverOn = false;

        ServerMessageModel shutdownMsg =
                new ServerMessageModel(ServerMessageMode.ServerShutdownMsg,
                        "THE ADMINISTRATOR CLOSED THE SERVER.");
        MyMessagesFiles.save(shutdownMsg);

        try {
            ArrayList<ChatClientHandler> tempClientsHandler = new ArrayList<>(getClientHandlers());
            for (ChatClientHandler clientHandler : tempClientsHandler) {
                clientHandler.sendMessageToClient(shutdownMsg);
                clientHandler.closeEverything();
            }

            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isServerOn() {
        return serverOn;
    }

    private static void configServer() {
        config = new ServerConfig(ServerMode.OPEN, true, 4444, "");
        MyServerConfigFile.writeConfig(config);
    }

    // TODO: What happened to this method?
    private static void restart() {
        try {
            closeServerSocket();
            setUpServer();
            startServer();
        } catch (IOException e) {
            System.err.println(RED_BOLD_BRIGHT + "Could not listen to port." + RESET);
            e.printStackTrace();
        }
    }

    private static void setUpServer() throws IOException {
        config = ServerConfig.factory();
        if (config == null)
            configServer();
        serverSocket = new ServerSocket(config.getPort());
    }

    public static void main(String[] args) {
        Files.readFiles();

        try {
            setUpServer();
            startServer();
        } catch (IOException e) {
            System.err.println(RED_BOLD_BRIGHT + "Could not listen to port." + RESET);
            e.printStackTrace();
        }
    }
}

