package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static utils.consts.ConsoleColors.*;

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

            broadcastMessage(RED_BOLD_BRIGHT + "SERVER: " + RESET +
                    clientUsername + RED_BOLD_BRIGHT + " has entered the chat." + RESET);
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

                    if (messageFromClient.contains("has left the chatroom"))
                        closeEverything(socket, bufferedReader, bufferedWriter);

                    System.out.println(messageFromClient);
                    broadcastMessage(messageFromClient);
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

    public void removeClientHandler() {
        clients.remove(this);
//        broadcastMessage(RED_BOLD_BRIGHT + "SERVER: " + RESET +
//                clientUsername + RED_BOLD_BRIGHT + " has left the chat." + RESET);
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();

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

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
