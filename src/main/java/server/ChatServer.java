package server;

import files.Files;
import files.MyMessagesFiles;
import files.MyServerConfigFile;
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

// TODO: Idea: Set up Unit or Integration Testing of the Chatroom Components
// Sign-in, Login, New Message, Commands, Exits
// TODO: Some Exceptions still include "java.lang.Exception"

public class ChatServer {
    private static ServerConfig config;
    private static ServerSocket serverSocket;
    private static boolean serverOn = false;

    private static final int MAX_PORTS_RANGE = 65536;
    private static final int MIN_PORTS_RANGE = 0;

    public static void startServer() {
        serverOn = true;

        ServerMessageModel turnOnMsg = new ServerMessageModel(ServerMessageMode.FromSerer, "THE ADMINISTRATOR OPENED THE SERVER.");
        MyMessagesFiles.save(turnOnMsg);

        System.out.println(RED_BOLD_BRIGHT + "SERVER CONNECTED!\n" +
                "Type '/help' to see a list of available commands.\n" +
                "Type '/exit' to close the server." + RESET);
        listenForServerCommands();

        try {
            while (isServerOn() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();//established
                System.out.println(CYAN_BOLD_BRIGHT + "NEW CONNECTION ESTABLISHED !" + RESET);
                ChatClientHandler client = new ChatClientHandler(socket);
                Thread thread = new Thread(client);
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
                System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

                if (scanner.hasNext()) {
                    scannedCommand = scanner.nextLine();

                    ServerMessageModel commandRespond = CommandHandlerServer.commandHandler(scannedCommand);

                    if (commandRespond.getMessageMode().equals(ServerMessageMode.PMFromServerToClient)) {
                        for (ChatClientHandler client : getClientHandlers()) {
                            client.messagingAClient(commandRespond);
                            break;
                        }
                    } else if (commandRespond.getMessageMode().equals(ServerMessageMode.ToAdminister) ||
                            commandRespond.getMessageMode().equals(ServerMessageMode.ListFromServer)) {
                        System.out.println(commandRespond.getFullMessage());
                        //                    ServerCli.command(scannedCommands);
                    }
                }
            }
        }).start();
    }

    public static void closeServerSocket() {
        serverOn = false;

        ServerMessageModel shutdownMsg =
                new ServerMessageModel(ServerMessageMode.ServerShutdownMsg, "THE ADMINISTRATOR CLOSED THE SERVER.");
        MyMessagesFiles.save(shutdownMsg);

        try {
            ArrayList<ChatClientHandler> tempClients = new ArrayList<>(getClientHandlers());
            for (ChatClientHandler client : tempClients) {
                client.sendMessageToClient(shutdownMsg);
                client.closeEverything();
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

