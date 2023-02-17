package server;

import client.models.ClientMessageMode;
import client.models.ClientMessageModel;
import client.models.ClientModel;
import files.MyActiveUsersFiles;
import files.MyMessagesFiles;
import files.MyUsersFiles;
import org.json.JSONException;
import org.json.JSONObject;
import server.commandclient.CommandHandlerClient;
import server.entrance.EntranceHandler;
import server.models.servermessage.ServerMessageMode;
import server.models.servermessage.ServerMessageModel;
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

                    if (obj instanceof ClientMessageModel<?>) {
                        clientMessage = (ClientMessageModel) obj;

                        if (clientMessage.getMode().equals(ClientMessageMode.INITIAL_CONNECTION)) {
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

                        if (clientMessage.getMode() == ClientMessageMode.MESSAGE) {
                            if (clientMessage.isCommand()) {
                                ServerMessageModel commandRespond =
                                        CommandHandlerClient.commandHandler(this, clientMessage);
                                messageHandling(commandRespond);
                            } else {
                                ServerMessageModel serverMessageModel =
                                        new ServerMessageModel(ServerMessageMode.FromClient, clientMessage);

                                messageHandling(serverMessageModel);
                            }
                        }
                    }
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

        if (clientMessage.getData() != null &&
                clientMessage.getData() instanceof ClientModel) {
            ClientModel clientModel = (ClientModel) clientMessage.getData();
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

    public void messageHandling(ServerMessageModel serverMessage) {
        switch (serverMessage.getMessageMode()) {
            case FromClient, FromServerAboutClient -> broadcastMessageToOthers(serverMessage);
            case FromSerer, ListFromServer, SignInteract -> sendMessageToClient(serverMessage);
            case PMFromClientToServer -> System.out.println(serverMessage.getFullMessage());
            case PMFromClientToClient -> messagingAClient(serverMessage);
            case ServerShutdownMsg -> closeEverything();
            default -> {
                System.out.println("I heard a roar in ChatClientHandler Class!");
                System.out.println("-> " + serverMessage + " -> " + serverMessage.getMessageMode());
            }
        }

        System.out.print("\n" + CYAN_BOLD_BRIGHT + ">" + RESET);
    }

    public void broadcastMessageToAll(ServerMessageModel serverMsgModelToSend) {
        System.out.println(serverMsgModelToSend.getFullMessage());
        MyMessagesFiles.save(serverMsgModelToSend);

        for (ChatClientHandler clientHandler : clientHandlers)
            clientHandler.sendMessageToClient(serverMsgModelToSend);
    }

    public void broadcastMessageToOthers(ServerMessageModel serverMsgModelToSend) {
        System.out.println(serverMsgModelToSend.getFullMessage());
        MyMessagesFiles.save(serverMsgModelToSend);

        for (ChatClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.equals(this)) {
                clientHandler.sendMessageToClient(serverMsgModelToSend);
            }
        }
    }

    public void sendMessageToClient(ServerMessageModel serverMsgModelToSend) {
        try {
            objectOutputStream.writeObject(serverMsgModelToSend);
            objectOutputStream.flush();
        } catch (IOException e) {
            if (isServerOn())
                closeEverything();
        }
    }

    public void messagingAClient(ServerMessageModel serverMsgModelToSend) {
        for (ChatClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getClientUsername().equals(serverMsgModelToSend.getClientModelReceiver().getUsername())) {
                clientHandler.sendMessageToClient(serverMsgModelToSend);
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
        clientHandlers.remove(this);

        if (!isServerOn()) {
            ServerMessageModel shutdownMessage =
                    new ServerMessageModel(ServerMessageMode.ServerShutdownMsg,
                            "SERVER WAS SHUTDOWN BY THE ADMINISTRATOR.");

            try {
                objectOutputStream.writeObject(shutdownMessage);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
}
