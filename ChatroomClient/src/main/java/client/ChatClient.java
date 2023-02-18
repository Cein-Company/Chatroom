package client;

import models.clientmodels.ClientMessageModel;
import models.clientmodels.ClientModel;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static utils.ConsoleDetail.*;

public class ChatClient {
    private static final ArrayList<ChatClient> chatClients = new ArrayList<>();

    private Socket socket;
    private ClientModel clientModel;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean isServerOn;
    private boolean isKicked;

    public ChatClient(Socket socket, ClientModel clientModel) {
        try {
            this.socket = socket;
            this.clientModel = clientModel;

            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());

            this.isServerOn = true;
            this.isKicked = false;

            chatClients.add(this);

            objectOutputStream.writeObject(clientModel);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage() {
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;

        try {
            Scanner scanner = new Scanner(System.in);
            while (isServerOn && socket.isConnected()) {
                if (!isServerOn)
                    break;

                System.out.print(clientModel.getColoredUsername() + colon);

                if (isServerOn && scanner.hasNext()) {
                    String messageToSend = scanner.nextLine().trim();

                    // FIXME: Empty messages don't work correctly
                    if (messageToSend.equals(""))
                        continue;

                    ClientMessageModel message = new ClientMessageModel(clientModel, messageToSend);

                    writeWithObjectOutput(message);

                    if (message.getMessage().toLowerCase(Locale.ROOT).equals("/exit")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            if (isServerOn)
                closeEverything();
        }
    }

    public void listenForMessage() {
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;

        new Thread(() -> {
            ServerMessageModel messageFromChat;

            while (isServerOn && socket.isConnected()) {
                try {
                    if (isServerOn) {
                        messageFromChat = (ServerMessageModel) objectInputStream.readObject();

                        if (messageFromChat != null) {
                            for (int i = 0; i < clientModel.getUsername().length() + 2; i++)
                                System.out.print("\b");

                            System.out.println(messageFromChat.getFullMessage());

                            if (messageFromChat.getMessageMode().equals(ServerMessageMode.ServerShutdownMsg)) {
                                isServerOn = false;

                                closeEverything();
                                break;
                            }

                            if (messageFromChat.getMessageMode().equals(ServerMessageMode.ServerKickMsg)) {
                                isKicked = true;

                                closeEverything();
                                break;
                            }

                            if (messageFromChat.getMessageMode().equals(ServerMessageMode.GoodbyeFromServer)) {
                                clientLeaving();
                                break;
                            }

                            System.out.print(clientModel.getColoredUsername() + colon);
                        }
                    }
                } catch (IOException e) {
                    if (isServerOn)
                        closeEverything();
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void clientLeaving() throws IOException {
        closeEverything();

        System.out.println("""
                \033[1;97m
                1. Return to main menu
                2. Exit
                \033[0m""");

        label:
        while (true) {
            System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

            String choice = new Scanner(System.in).nextLine().trim();

            switch (choice) {
                case "1":
                    ChatClientCLI.makeInitialConnection();
                    return;
                case "2":
                    System.out.print(RED_BOLD_BRIGHT + "\nGoodbye." + RESET);
                    break label;
                case "":
                    continue;
                default:
                    System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
                    break;
            }
        }
    }

    private void writeWithObjectOutput(ClientMessageModel clientMessage) throws IOException {
        objectOutputStream.writeObject(clientMessage);
        objectOutputStream.flush();
    }

    private void closeEverything() {
        chatClients.remove(this);

        try {
            if (socket != null)
                socket.close();
            if (objectOutputStream != null)
                objectOutputStream.close();

            if (objectInputStream != null)
                objectInputStream.close();

            if (!isServerOn || isKicked)
                System.exit(0);
        } catch (IOException e) {
            System.out.print(RED_BOLD_BRIGHT + """
                   
                    CONNECTION TO SERVER WAS UNEXPECTEDLY ABORTED.
                    EXITING THE APP NOW...
                    """ + RESET);
        }
    }
}