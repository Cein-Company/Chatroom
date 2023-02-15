package server.models;

import client.models.ClientMessageModel;
import client.models.ClientModel;

import java.io.Serializable;
import java.util.Date;

import static utils.ConsoleDetail.*;

public class ServerMessageModel implements Serializable {
    private final String message;
    private String coloredMessage;

    private final ServerMessageMode messageMode;

    private ClientModel client;
    private ClientModel clientModelSender;
    private ClientModel clientModelReceiver;

    private String messageTime;
    private String messageTimeColored;

    public ServerMessageModel(ServerMessageMode messageMode, String message) {
        this.messageMode = messageMode;

        if (messageMode.equals(ServerMessageMode.ListFromServer)) {
            this.message = message;
        } else {
            this.message = message;
            this.coloredMessage = RED_BOLD_BRIGHT + this.message + RESET;
            this.messageTime = getCurrentTime();
            this.messageTimeColored = WHITE_BOLD_BRIGHT + this.messageTime + RESET;
        }
    }

    public ServerMessageModel(ServerMessageMode messageMode, ClientMessageModel clientMessageModel) {
        this.messageMode = messageMode;

        this.message = clientMessageModel.getMessage();
        this.coloredMessage = clientMessageModel.getColoredMessage();
        this.clientModelSender = clientMessageModel.getSender();
        this.messageTime = clientMessageModel.getMessageTime();
        this.messageTimeColored = clientMessageModel.getMessageTimeColored();
    }

    public ServerMessageModel(ServerMessageMode messageMode, ClientModel clientModelReceiver, ClientMessageModel clientMessageModel) {
        this.message = clientMessageModel.getMessage();
        this.coloredMessage = clientMessageModel.getColoredMessage();
        this.clientModelSender = clientMessageModel.getSender();
        this.clientModelReceiver = clientModelReceiver;
        this.messageTime = clientMessageModel.getMessageTime();
        this.messageTimeColored = clientMessageModel.getMessageTimeColored();

        this.messageMode = messageMode;
    }

    public ServerMessageModel(ServerMessageMode messageMode, ClientMessageModel clientMessageModel, ClientModel clientModelReceiver, String message) {
        this.messageMode = messageMode;

        this.message = message;
        this.coloredMessage = RED_BOLD_BRIGHT + this.message + RESET;
        this.clientModelSender = clientMessageModel.getSender();
        this.clientModelReceiver = clientModelReceiver;
        this.messageTime = clientMessageModel.getMessageTime();
        this.messageTimeColored = clientMessageModel.getMessageTimeColored();
    }

    public ServerMessageModel(ServerMessageMode messageMode, ClientModel aboutClient, String message) {
        this.message = message;
        this.coloredMessage = RED_BOLD_BRIGHT + this.message + RESET;
        this.client = aboutClient;
        this.messageTime = getCurrentTime();
        this.messageTimeColored = WHITE_BOLD_BRIGHT + messageTime + RESET;

        this.messageMode = messageMode;
    }

    public ServerMessageModel(ServerMessageMode messageMode, ClientMessageModel clientMessageModel, String message) {
        this.message = message;
        this.coloredMessage = RED_BOLD_BRIGHT + this.message + RESET;
        this.clientModelSender = clientMessageModel.getSender();
        this.messageTime = clientMessageModel.getMessageTime();
        this.messageTimeColored = clientMessageModel.getMessageTimeColored();

        this.messageMode = messageMode;
    }

    public String getFullMessage() {
        final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;
        final String serverSender = RED_BOLD_BRIGHT + "SERVER" + RESET;
        final String PrivateAnnouncement = RED_BOLD_BRIGHT + "PM FROM " + RESET;


        switch (messageMode) {
            case FromSerer, ServerShutdownMsg, ServerKickMsg -> {
                return messageTimeColored + indicator + serverSender + colon + coloredMessage;
            }
            case FromServerAboutClient -> {
                return messageTimeColored + indicator + serverSender + colon + client.getColoredUsername() + coloredMessage;
            }
            case FromClient -> {
                return messageTimeColored + indicator + clientModelSender.getColoredUsername() + colon + coloredMessage;
            }
            case ToAdminister -> {
                return messageTimeColored + indicator + coloredMessage;
            }
            case ToAdministerAboutAClient -> {
                return messageTimeColored + indicator + client.getColoredUsername() + coloredMessage;
            }
            case PMFromClientToClient, PMFromClientToServer -> {
                return messageTimeColored + indicator + PrivateAnnouncement + clientModelSender.getColoredUsername() + colon + coloredMessage;
            }
            case PMFromServerToClient -> {
                return messageTimeColored + indicator + PrivateAnnouncement + serverSender + colon + coloredMessage;
            }
            case ListFromServer -> {
                return message;
            }
            default -> {
                return "I heard a roar in the ServerMessageModel class!";
            }
        }
    }

    public String getColorlessMessage() {
        final String indicator = " -> ";
        final String colon = ": ";
        final String serverSender = "SERVER";
        final String PrivateAnnouncement = "PM FROM ";


        switch (messageMode) {
            case FromSerer -> {
                return messageTime + indicator + serverSender + colon + message;
            }
            case FromServerAboutClient -> {
                return messageTime + indicator + serverSender + colon + client.getUsername() + message;
            }
            case FromClient -> {
                return messageTime + indicator + clientModelSender.getUsername() + colon + message;
            }
            case PMFromClientToClient, PMFromClientToServer -> {
                return messageTime + indicator + PrivateAnnouncement + clientModelSender.getUsername() + colon + message;
            }
            case PMFromServerToClient -> {
                return messageTime + indicator + PrivateAnnouncement + serverSender + colon + message;
            }
            default -> {
                return "I heard a roar in the ServerMessageModel class!";
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public String getColoredMessage() {
        return coloredMessage;
    }

    public ServerMessageMode getMessageMode() {
        return messageMode;
    }

    public ClientModel getClientModelSender() {
        return clientModelSender;
    }

    public ClientModel getClientModelReceiver() {
        return clientModelReceiver;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getMessageTimeColored() {
        return messageTimeColored;
    }

    public void setClientModelReceiver(ClientModel clientModelReceiver) {
        this.clientModelReceiver = clientModelReceiver;
    }

    private String getCurrentTime() {
        return dateFormat.format(new Date());
    }
}