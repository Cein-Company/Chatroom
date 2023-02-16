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
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import static server.ChatServer.isServerOn;
import static utils.ConsoleDetail.CYAN_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

// TODO: Client name color changes on each login
public class ChatClientHandler implements Runnable {
    private static final ArrayList<ChatClientHandler> clientHandlers = new ArrayList<>();
    private static final String SIGN_UP = "sign_up";
    private static final String LOGIN = "login";

    private Socket socket;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private String clientUsername;
    private ClientModel clientModel;

    public ChatClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Object obj = objectInputStream.readObject();
            if (obj != null && obj instanceof ClientModel) {
                this.clientModel = (ClientModel) obj;
                this.clientUsername = clientModel.getUsername();

                final String enteredChatMessage = " has entered the chat.";

                ServerMessageModel enteredChatMsg =
                        new ServerMessageModel(ServerMessageMode.FromServerAboutClient, clientModel, enteredChatMessage);

                showMessageHistoryToClient();
                broadcastMessageToAll(enteredChatMsg);
            }

            clientHandlers.add(this);
        } catch (IOException e) {
            if (isServerOn())
                closeEverything();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ClientMessageModel clientMessage = null;

        while (isServerOn() && socket.isConnected()) {
            try {
                if (isServerOn()) {
                    Object readObject = objectInputStream.readObject();
                    if (readObject instanceof ClientMessageModel<?>)
                        clientMessage = (ClientMessageModel) readObject;
                    if (clientMessage != null) {
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
                        } else if (clientMessage.getMode() == ClientMessageMode.SIGN_INTERACT) {
                            try {
                                messageHandling(entranceHandling(clientMessage));
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    if (clientMessage.getMessage().equals(SIGN_UP))
                        EntranceHandler.register(clientModel);
                    else if (clientMessage.getMessage().equals(LOGIN)) {
                        EntranceHandler.login(clientModel);
                        clientModel = MyUsersFiles.getUserByName(clientModel.getUsername());
                    }
                    response.put("condition", true);
                    JSONObject clientJO = new JSONObject();
                    clientJO.put("username", clientModel.getUsername());
                    clientJO.put("password", clientModel.getPassword());
                    if (clientMessage.getMessage().equals(SIGN_UP))
                        clientJO.put("id", UUID.randomUUID().toString());
                    else
                        clientJO.put("id", clientModel.getClientId().toString());
                    response.put("content", CYAN_BOLD_BRIGHT +
                            "Login successful. You can start chatting now.\n" + RESET);
                    response.put("client", clientJO);
                    MyActiveUsersFiles.save(clientModel.getUsername());
                } catch (Exception e) {
                    response.put("condition", false);
                    response.put("content", e.toString());
                }
            } catch (JSONException exception) {
                response.put("content", exception.toString());
                response.put("condition", false);
            }

            serverMessageModel = new ServerMessageModel(ServerMessageMode.SignInteract, response.toString());
        }

        return serverMessageModel;
    }

    public void messageHandling(ServerMessageModel serverMessage) {
        System.out.println("comes to messageHandling");
        System.out.println(serverMessage.getFullMessage());
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
        System.out.println("comes to broadcastMessageToAll");
        System.out.println(serverMsgModelToSend.getFullMessage());
        MyMessagesFiles.save(serverMsgModelToSend);

        for (ChatClientHandler client : clientHandlers) {
            try {
                client.getObjectOutputStream().writeObject(serverMsgModelToSend);
                client.getObjectOutputStream().flush();
            } catch (IOException e) {
                if (isServerOn())
                    closeEverything();
                break;
            }
        }
    }

    public void broadcastMessageToOthers(ServerMessageModel serverMsgModelToSend) {
        System.out.println("comes to broadcastMessageToOthers");
        System.out.println(serverMsgModelToSend.getFullMessage());
        MyMessagesFiles.save(serverMsgModelToSend);

        for (ChatClientHandler client : clientHandlers) {
            try {
                if (!client.equals(this)) {
                    client.getObjectOutputStream().writeObject(serverMsgModelToSend);
                    client.getObjectOutputStream().flush();
                }
            } catch (IOException e) {
                if (isServerOn())
                    closeEverything();
                break;
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
        for (ChatClientHandler client : clientHandlers) {
            try {
                if (client.getClientUsername().equals(serverMsgModelToSend.getClientModelReceiver().getUsername())) {
                    client.getObjectOutputStream().writeObject(serverMsgModelToSend);
                    client.getObjectOutputStream().flush();
                    break;
                }
            } catch (IOException e) {
                if (isServerOn())
                    closeEverything();
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
                    new ServerMessageModel(ServerMessageMode.ServerShutdownMsg, "SERVER WAS SHUTDOWN BY THE ADMINISTRATOR.");

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

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
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
