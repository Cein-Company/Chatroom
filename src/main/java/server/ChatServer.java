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

public class ChatServer {
    private static ServerConfig config;
    private static ServerSocket serverSocket;
    private static boolean serverOn = false;

    private static int MAX_PORTS_RANGE = 65536;
    private static int MIN_PORTS_RANGE = 0;

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
                Socket socket = serverSocket.accept();
                System.out.println(CYAN_BOLD_BRIGHT + "NEW USER CONNECTED!" + RESET);
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
            String scannedCommands;
            Scanner scanner = new Scanner(System.in);

            while (isServerOn() && !serverSocket.isClosed()) {
                if (scanner.hasNext()) {
                    scannedCommands = scanner.nextLine();
                    ServerMessageModel commandRespond = CommandHandlerServer.commandHandler(scannedCommands);
                    System.out.println(commandRespond.getFullMessage());
//                    ServerCli.command(scannedCommands);
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


//    private class ServerCli {
//        private static String logCommand(String[] commands) {
//            String[] log = new String[1];
//            ServerMessageModelFiles.readServerMsgModels().forEach(m -> {
//                if (m != null) {
//                    log[0] += m + "\n";
//                }
//            });
//            if (commands.length > 2) {
//                if (commands[1].equals("-save")) {
//                    try {
//                        String path = commands[2] + "log.txt";
//                        BufferedWriter bfWriter = new BufferedWriter(new FileWriter(new File(path)));
//                        bfWriter.write(log[0]);
//                        bfWriter.close();
//                        return (GREEN + "log Saved in : " + path + RESET);
//                    } catch (Exception ex) {
//                        return (ex.toString());
//                    }
//                }
//            } else
//                return (log[0]);
//            return "";
//        }
//
//        /**
//         * config -arg value
//         * -args {
//         * 1-print
//         * 2-mode
//         * 3-port
//         * 4-restart
//         * 5-ip
//         * 6-show-history
//         * }
//         */
//        private static String configCommand(String[] commands) {
//            if (commands.length == 1)
//                return config.toString();
//            try {
//
//                switch (commands[1].replaceFirst("-", "")) {
//                    case "print" -> {
//                        return config.toString();
//                    }
//                    case "mode" -> {
//                        ServerMode mode = ServerMode.valueOf(commands[2]);
//                        if (mode == null)
//                            return "Incorrect mode";
//                        config.setMode(mode);
//                        return GREEN + "Server mode successfully changed to " + mode + RESET;
//                    }
//                    case "port" -> {
//                        if (commands.length == 2)
//                            return String.valueOf(config.getPort());
//                        int port = Integer.parseInt(commands[2]);
//                        if (port > MAX_PORTS_RANGE || port < MIN_PORTS_RANGE)
//                            return RED + "Port out of range !" + RESET;
//                        config.setPort(port);
//                        restart();
//                        return GREEN + "Server port successfully changed to " + port + RESET;
//                    }
//                    case "restart" -> restart();
//                    case "ip" -> {
//                        if (commands.length == 2)
//                            return String.valueOf(config.getIpAddress());
//                        String ip = commands[2];
//                        String[] Parts = ip.split("\\.");
//                        if (Parts.length != 4)
//                            return RED + "Incorrect ip address!" + RESET;
//                        config.setIpAddress(ip);
//                        restart();
//                        return GREEN + "Server ip address successfully changed to " + ip + RESET;
//                    }
//                    case "show-history" -> {
//
//                    }
//                    default -> {
//                        return RED + "Invalid arg" + RESET;
//                    }
//                }
//
//            } catch (IndexOutOfBoundsException ex) {
//                return "";
//            }
//            return "";
//        }
//
//        public static void command(String scannedCommands) {
//            if (scannedCommands != null && scannedCommands.charAt(0) == '/') {
//                String[] commands = scannedCommands.replaceFirst("/", "").split(" ");
//                if (commands.length == 0)
//                    return;
//                switch (commands[0]) {
//                    // log messages --> save or print
//                    case "log" -> {
//                        System.out.println(logCommand(commands));
//                    }
//                    case "members" -> {
//                        String[] users = new String[1];
//                        users[0] = UsersFiles.readUsers().toString();
//                        users[0] = users[0].replace('{', ' ').replace('}', ' ');
//                        String[] array = users[0].split(",");
//                        users[0] = "";
//                        Arrays.stream(array).forEach(u -> {
//                            String[] data = u.split("=");
//                            users[0] += "username : " + data[0] + "\t" + "password : " + data[1] + "\n";
//                        });
//                        System.out.println(users[0]);
//                    }
//                    case "kick" -> {
//
//                    }
//                    // poll -args ---> poll -create -t "title" -o option1 option2 option3 ... ,
//                    //                 poll -result {poll code}
//                    //                 poll -end {poll code}
//                    case "poll" -> {
//                        switch (commands[1].replaceFirst("-", "")) {
//                            case "create" -> {
//                                String title = "";
//                                List<PollOptionModel> options = null;
//                                for (int i = 0; i < commands.length; i++) {
//                                    String c = commands[i];
//                                    if (c.charAt(0) == '-') {
//                                        if (c.equals("-t")) {
//                                            if (commands[i + 1].charAt(0) == '-')
//                                                continue;
//                                            title = "";
//                                            for (int j = i + 1; j < commands.length; j++) {
//                                                if (commands[j].charAt(0) == '-')
//                                                    break;
//                                                title += commands[j] + " ";
//                                            }
//                                        } else if (c.equals("-o")) {
//                                            options = new ArrayList<>();
//                                            for (int j = i + 1; j < commands.length; j++) {
//                                                if (commands[j].charAt(0) == '-')
//                                                    break;
//                                                options.add(PollOptionModel.factory(commands[j]));
//                                            }
//                                        }
//                                    }
//                                }
//                                if (title == null) {
//                                    System.out.println(RED + "Title can't be empty" + RESET);
//                                    return;
//                                }
//                                if (options == null) {
//                                    System.out.println(RED + "Options can't be empty" + RESET);
//                                    return;
//                                }
//                                PollModel poll = new PollModel(title, options);
//                                System.out.println(poll.show());
//
//                            }
//                            case "result" -> {
//                            }
//                            case "end" -> {
//                            }
//                        }
//
//                    }
//                    case "ban" -> {
//
//                    }
//                    case "config" -> {
//                        System.out.println(configCommand(commands));
//                    }
//                    case "exit" -> closeServerSocket();
//                    case "help" -> System.out.print(helpCommand(commands));
//                    default -> System.out.println("Invalid Command");
//                }
//            }
//        }
//
//    }

}

