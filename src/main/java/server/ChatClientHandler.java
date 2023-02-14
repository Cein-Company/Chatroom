package server;

import files.ChatMessagesFiles;
import server.commandclient.ClientCommandHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static client.ChatClientCLI.getUsers;
import static utils.consts.ConsoleDetail.*;

public class ChatClientHandler implements Runnable {
    public static ArrayList<ChatClientHandler> clients = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ChatClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();

            clients.add(this);

            String enteredChatMessage = RED_BOLD_BRIGHT + "SERVER: " + RESET +
                    clientUsername + RED_BOLD_BRIGHT + " has entered the chat." + RESET;

            readMessages();
            saveMessages(enteredChatMessage);

            System.out.println(enteredChatMessage);
            broadcastMessage(enteredChatMessage);
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                
                if (messageFromClient != null && messageFromClient.length() != 0) {
                    if (messageFromClient.charAt(messageFromClient.indexOf(": ") + 13) == '/') {
                        String commandRespond = ClientCommandHandler.commandHandler(messageFromClient);

                        if (commandRespond.contains("SERVER: ")) {
                            sendMessageToClient(commandRespond);
                        } else {
                            String[] arr = commandRespond.split(" ", 2);

                            String target = arr[0];
                            String messageToBeSent = arr[1];

                            if (target.equals("server"))
                                System.out.println(messageToBeSent);
                            else
                                messagingAClient(target, messageToBeSent);
                        }
                    } else {
                        saveMessages(messageFromClient);

                        if (messageFromClient.contains("has left the chatroom"))
                            closeEverything(socket, bufferedReader, bufferedWriter);

                        System.out.println(messageFromClient);
                        broadcastMessage(messageFromClient);
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ChatClientHandler client : clients) {
            try {
                if (!client.equals(this)) {
                    client.getBufferedWriter().write(messageToSend);
                    client.getBufferedWriter().newLine();
                    client.getBufferedWriter().flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void sendMessageToClient(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void messagingAClient(String clientUsername, String messageToSend) {
        for (ChatClientHandler client : clients) {
            try {
                if (client.clientUsername.equals(clientUsername)) {
                    client.getBufferedWriter().write(messageToSend);
                    client.getBufferedWriter().newLine();
                    client.getBufferedWriter().flush();
                    break;
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void removeClientHandler() {
        clients.remove(this);
//        broadcastMessage(RED_BOLD_BRIGHT + "SERVER: " + RESET +
//                clientUsername + RED_BOLD_BRIGHT + " has left the chat." + RESET);
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();

        if (!ChatServer.isServerOn()) {
            try {
                bufferedWriter.write("SERVER SHUTDOWN");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (socket != null)
                socket.close();

            if (bufferedWriter != null)
                bufferedWriter.close();

            if (bufferedReader != null)
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessages(String messageToSave) {
        ArrayList<String> tempMessages = ChatMessagesFiles.readChatMessages();
        if (tempMessages != null) {
            ChatServer.getChatMessages().clear();
            ChatServer.getChatMessages().addAll(tempMessages);
        }

        ChatServer.getChatMessages().add(messageToSave);
        ChatMessagesFiles.writeChatMessages(ChatServer.getChatMessages());
    }

    public void readMessages() {
        ArrayList<String> tempMessages = ChatMessagesFiles.readChatMessages();
        if (tempMessages != null) {
            ChatServer.getChatMessages().clear();
            ChatServer.getChatMessages().addAll(tempMessages);
        }

        for (String oldMessage : ChatServer.getChatMessages()) {
            try {
                bufferedWriter.write(oldMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
