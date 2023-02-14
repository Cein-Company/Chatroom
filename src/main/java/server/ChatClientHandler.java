package server;

import client.models.ClientMessageModel;
import client.models.ClientModel;
import files.MyMessagesFiles;
import server.commandclient.CommandHandlerClient;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static server.ChatServer.isServerOn;

public class ChatClientHandler implements Runnable {
    private static final ArrayList<ChatClientHandler> clientHandlers = new ArrayList<>();

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

            this.clientModel = (ClientModel) objectInputStream.readObject();
            this.clientUsername = clientModel.getUsername();

            clientHandlers.add(this);

            final String enteredChatMessage = " has entered the chat.";

            ServerMessageModel enteredChatMsg =
                    new ServerMessageModel(ServerMessageMode.FromServerAboutClient, clientModel, enteredChatMessage);

            showMessageHistoryToClient();
            broadcastMessageToAll(enteredChatMsg);
        } catch (IOException e) {
            if (isServerOn())
                closeEverything();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ClientMessageModel clientMessage;

        while (isServerOn() && socket.isConnected()) {
            try {
                if (isServerOn()) {
                    clientMessage = (ClientMessageModel) objectInputStream.readObject();

                    if (clientMessage != null) {
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
            } catch (IOException e) {
                if (isServerOn())
                    closeEverything();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageHandling(ServerMessageModel serverMessage) {
        switch (serverMessage.getMessageMode()) {
            case FromClient, FromServerAboutClient -> broadcastMessageToOthers(serverMessage);
            case FromSerer, ListFromServer -> sendMessageToClient(serverMessage);
            case PMFromClientToServer -> System.out.println(serverMessage.getFullMessage());
            case PMFromClientToClient -> messagingAClient(serverMessage);
            case ServerShutdownMsg -> closeEverything();
        }
    }

    public void broadcastMessageToAll(ServerMessageModel serverMsgModelToSend) {
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
            if (socket != null)
                socket.close();

            if (objectOutputStream != null)
                objectOutputStream.close();

            if (objectInputStream != null)
                objectInputStream.close();
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
