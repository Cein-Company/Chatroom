package server;

import files.*;
import server.commands.commandserver.CommandHandlerServer;
import server.config.ServerConfig;
import server.config.ServerMode;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static server.ChatClientHandler.getClientHandlers;
import static utils.ConsoleDetail.*;

// TODO: Idea: Set up Unit or Integration Testing of the Chatroom Components:
//       Sign-in, Login, New Message, Commands, Exits

// TODO: Message Object system is confusing and ugly code.
//  Needs to be rebuilt.
public class ChatServer {
    private static ServerConfig config;
    private static ServerSocket serverSocket;
    private static boolean serverOn = false;

    // TODO: What are these?
    private static final int MAX_PORTS_RANGE = 65536;
    private static final int MIN_PORTS_RANGE = 0;

    public static boolean isServerOn() {
        return serverOn;
    }

    private static void startServer() {
        serverOn = true;

        ServerMessageModel turnOnMsg =
                new ServerMessageModel(ServerMessageMode.FromServer, "THE ADMINISTRATOR OPENED THE SERVER.");
        MyMessagesFiles.save(turnOnMsg);

        System.out.println(RED_BOLD_BRIGHT + """
                SERVER CONNECTED!
                Type '/help' to see a list of available commands.
                Type '/shutdown' to close the server.""" + RESET);

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

    private static void listenForServerCommands() {
        new Thread(() -> {
            String scannedCommand;
            Scanner scanner = new Scanner(System.in);

            while (isServerOn() && !serverSocket.isClosed()) {
                System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

                if (scanner.hasNext()) {
                    scannedCommand = scanner.nextLine().trim();

                    ServerMessageModel commandRespond = CommandHandlerServer.commandHandler(scannedCommand);

                    System.out.println(commandRespond.getFullMessage());

                    if (commandRespond.getMessageMode().equals(ServerMessageMode.PMFromServerToClient)) {
                        for (ChatClientHandler clientHandler : getClientHandlers()) {
                            if (clientHandler.getClientUsername().equals(commandRespond.getClientModelReceiver().getUsername())) {
                                clientHandler.sendMessageToClient(commandRespond);
                                break;
                            }
                        }
                    } else if (commandRespond.getMessageMode().equals(ServerMessageMode.ServerShutdownMsg)) {
                        ChatServer.closeServerSocket();
                    }
                }
            }
        }).start();
    }

    private static void closeServerSocket() {
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

