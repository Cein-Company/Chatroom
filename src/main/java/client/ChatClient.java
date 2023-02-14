package client;

import files.ActiveUsersFiles;
import utils.consts.ConsoleDetail;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import static utils.consts.ConsoleDetail.*;

public class ChatClient {
    private Socket socket;
    private ClientModel client;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ChatClient(Socket socket, ClientModel client) {
        try {
            this.socket = socket;
            this.client = client;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        final String colon =CYAN_BOLD_BRIGHT + ": " + RESET;

        try {
            writeWithBuffered(client.getColoredUsername());

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                System.out.print(client.getColoredUsername() + colon);

                if (scanner.hasNext()) {
                    String messageToSend = scanner.nextLine();
                    if (messageToSend != null) {
                        if (messageToSend.equals("/exit")) {
                            clientLeaving();
                            break;
                        }

                        if (messageToSend.equals("")) {
                            continue;
                        }
                        System.out.print(String.format("\033[%dA",1)); // Move up
                        System.out.print("\033[2K");
                        messageToSend = getCurrentTime() +" -> " + client.getColoredUsername() + colon + WHITE_BOLD_BRIGHT + messageToSend + RESET;
                        System.out.println(messageToSend);
                        writeWithBuffered(messageToSend);
                    }
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;

        new Thread(() -> {
            String msgFromGroupChat;

            while (socket.isConnected()) {
                try {
                    if (bufferedReader.ready()) {
                        msgFromGroupChat = bufferedReader.readLine();

                        if (msgFromGroupChat != null && msgFromGroupChat.length() != 0) {
                            for (int i = 0; i < client.getUsername().length() + 2; i++)
                                System.out.print("\b");
                            if (msgFromGroupChat.equals("SERVER SHUTDOWN")) {
                                System.out.println(msgFromGroupChat);

                                ChatClientCLI.getActiveUsers().remove(client.getUsername());
                                ActiveUsersFiles.writeActiveUsers(ChatClientCLI.getActiveUsers());

                                closeEverything(socket, bufferedReader, bufferedWriter);

                                break;
                            }
                            System.out.println(msgFromGroupChat);
                            System.out.print(client.getColoredUsername() + colon);
                        }
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    public void clientLeaving() {
        try {
            String leftChatMessage = RED_BOLD_BRIGHT + "SERVER: " + RESET +
                    client.getColoredUsername() + RED_BOLD_BRIGHT + " has left the chatroom." + RESET;

            writeWithBuffered(leftChatMessage);

            System.out.println(RED_BOLD_BRIGHT + "You have left the chatroom. Goodbye." + RESET);

            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWithBuffered(String text) throws IOException {
        bufferedWriter.write(text);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        ChatClientCLI.removeActiveUsers(client.getUsername());

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

    private String getCurrentTime()
    {
        return dateFormat.format(new Date());
    }
}