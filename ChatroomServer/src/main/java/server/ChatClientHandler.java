package server;

import models.clientmodels.ClientMessageMode;
import models.clientmodels.ClientMessageModel;
import models.clientmodels.ClientModel;
import files.MyActiveUsersFiles;
import files.MyMessagesFiles;
import files.MyUsersFiles;
import org.json.JSONException;
import org.json.JSONObject;
import server.commands.commandclient.CommandHandlerClient;
import server.entrance.EntranceHandler;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;
import utils.ConsoleDetail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import static server.ChatServer.isServerOn;
import static utils.ConsoleDetail.*;

public class ChatClientHandler implements Runnable {
    private static final ArrayList<ChatClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private ClientModel clientModel;
    private String clientUsername;

    private boolean symbolShown = true;

    public ChatClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            clientHandlers.add(this);
        } catch (IOException e) {
            if (isServerOn())
                closeEverything();
        }
    }

    @Override
    public void run() {
        ClientMessageModel clientMessage;

        while (isServerOn() && socket.isConnected()) {
            try {
                if (isServerOn()) {
                    Object obj = objectInputStream.readObject();

                    if (obj instanceof ClientModel) {
                        this.clientModel = (ClientModel) obj;
                        this.clientUsername = clientModel.getUsername();
                        MyActiveUsersFiles.save(clientModel.getUsername());

                        final String enteredChatMessage = " has entered the chat.";

                        ServerMessageModel enteredChatMsg =
                                new ServerMessageModel(ServerMessageMode.FromServerAboutClient, clientModel, enteredChatMessage);

                        showMessageHistoryToClient();
                        broadcastMessageToAll(enteredChatMsg);
                    }

                    if (obj instanceof ClientMessageModel) {
                        clientMessage = (ClientMessageModel) obj;

                        if (clientMessage.getMode().equals(ClientMessageMode.INITIAL_CONNECTION)) {
                            clearInputSymbol();

                            System.out.println(CYAN_BOLD_BRIGHT + "NEW CONNECTION ESTABLISHED!" + RESET);
                        }

                        if (clientMessage.getMode().equals(ClientMessageMode.SIGNING_IN)
                                || clientMessage.getMode().equals(ClientMessageMode.LOGIN_IN)) {
                            try {
                                messageHandling(entranceHandling(clientMessage));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (clientMessage.getMode().equals(ClientMessageMode.MESSAGE)) {
                            if (clientMessage.isCommand()) {
                                ServerMessageModel commandRespond =
                                        CommandHandlerClient.commandHandler(this, clientMessage);
                                messageHandling(commandRespond);
                            } else {
                                ServerMessageModel clientMessageToBeSent =
                                        new ServerMessageModel(ServerMessageMode.FromClient, clientMessage);

                                messageHandling(clientMessageToBeSent);
                            }
                        }
                    }

                    showInputSymbol();
                }
            } catch (IOException e) {
                if (isServerOn())
                    closeEverything();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private ServerMessageModel entranceHandling(ClientMessageModel clientMessage) throws JSONException {
        JSONObject response = new JSONObject();
        ServerMessageModel serverMessageModel = null;

        if (clientMessage.getRequestingClient() != null) {
            ClientModel clientModel = clientMessage.getRequestingClient();

            try {
                try {
                    if (clientMessage.getMode().equals(ClientMessageMode.SIGNING_IN))
                        EntranceHandler.register(clientModel);
                    else if (clientMessage.getMode().equals(ClientMessageMode.LOGIN_IN)) {
                        EntranceHandler.login(clientModel);
                        clientModel = MyUsersFiles.getUserByName(clientModel.getUsername());
                    }
                    response.put("condition", true);

                    JSONObject clientJO = new JSONObject();
                    clientJO.put("username", clientModel.getUsername());
                    clientJO.put("password", clientModel.getPassword());
                    if (clientMessage.getMode().equals(ClientMessageMode.SIGNING_IN)) {
                        clientJO.put("id", UUID.randomUUID().toString());
                        clientJO.put("CLIENT_COLOR", ConsoleDetail.getRandomBBColor());
                    } else {
                        clientJO.put("id", clientModel.getClientId().toString());
                        clientJO.put("CLIENT_COLOR", clientModel.getCLIENT_COLOR());
                    }

                    response.put("content", CYAN_BOLD_BRIGHT +
                            "Login successful. You can start chatting now.\n" + RESET);
                    response.put("client", clientJO);
                } catch (Exception e) {
                    response.put("condition", false);
                    response.put("content", e.getMessage());
                }
            } catch (JSONException jsonEx) {
                response.put("condition", false);
                response.put("content", jsonEx.getMessage());
            }

            serverMessageModel = new ServerMessageModel(ServerMessageMode.SignInteract, response.toString());
        }

        return serverMessageModel;
    }

    private void messageHandling(ServerMessageModel message) {
        switch (message.getMessageMode()) {
            case FromClient -> broadcastMessageToAll(message);
            case FromServerAboutClient -> broadcastMessageToOthers(message);
            case FromServer, ListFromServer, SignInteract,
                    FromServerToClient, PMFromServerToClient -> sendMessageToClient(message);
            case PMFromClientToServer -> {
                clearInputSymbol();
                System.out.println(message.getFullMessage());
            }
            case PMFromClientToClient -> messagingAClient(message);
            default -> {
                System.out.println("I heard a roar in ChatClientHandler Class!");
                System.out.println("-> " + message + " -> " + message.getMessageMode());
            }
        }
    }

    public void broadcastMessageToAll(ServerMessageModel messageToSend) {
        clearInputSymbol();
        System.out.println(messageToSend.getFullMessage());
        MyMessagesFiles.save(messageToSend);

        for (ChatClientHandler clientHandler : clientHandlers)
            clientHandler.sendMessageToClient(messageToSend);
    }

    public void broadcastMessageToOthers(ServerMessageModel messageToSend) {
        clearInputSymbol();
        System.out.println(messageToSend.getFullMessage());
        MyMessagesFiles.save(messageToSend);

        for (ChatClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.equals(this)) {
                clientHandler.sendMessageToClient(messageToSend);
            }
        }
    }

    public void sendMessageToClient(ServerMessageModel messageToSend) {
        try {
            objectOutputStream.writeObject(messageToSend);
            objectOutputStream.flush();
        } catch (IOException e) {
            if (isServerOn())
                closeEverything();
        }
    }

    public void messagingAClient(ServerMessageModel messageToSend) {
        for (ChatClientHandler clientHandler : clientHandlers) {
            if (clientHandler.clientUsername.equals(messageToSend.getClientModelReceiver().getUsername())) {
                clientHandler.sendMessageToClient(messageToSend);
                break;
            }
        }
    }

    private void showMessageHistoryToClient() {
        StringBuilder tempMessages = new StringBuilder();
        for (ServerMessageModel message : MyMessagesFiles.getAllMessagesDuplicate())
            tempMessages.append(message.getFullMessage()).append("\n");

        sendMessageToClient(new ServerMessageModel(ServerMessageMode.ListFromServer, tempMessages.toString()));
    }

    public void closeEverything() {
        MyActiveUsersFiles.remove(this.clientUsername);
        clientHandlers.remove(this);

        try {
            if (objectOutputStream != null)
                objectOutputStream.close();

            if (objectInputStream != null)
                objectInputStream.close();

            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public static ArrayList<ChatClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    private void clearInputSymbol() {
        // To clear '>'
        if (symbolShown) {
            for (int i = 0; i < 12; i++)
                System.out.print("\b");

            symbolShown = false;
        }
    }

    private void showInputSymbol() {
        // To show '>'
        if (!symbolShown) {
            System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

            symbolShown = true;
        }
    }
}
