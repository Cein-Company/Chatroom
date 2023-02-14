package client.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static utils.ConsoleDetail.*;

public class ClientMessageModel<T> implements Serializable {
//    private static final Map<UUID, ClientMessageModel> clientMessageMap = new HashMap<>();

    private final String message;
    private final String coloredMessage;
    private final ClientModel sender;
    private String receiver;
    private final String messageTime;
    private final String messageTimeColored;
    T data;
    private final boolean isCommand;
    private final boolean isExitCommand;
    private ClientMessageMode mode;
    public ClientMessageModel(ClientModel sender, String message) {
        this.message = message;
        this.coloredMessage = WHITE_BOLD_BRIGHT + this.message + RESET;
        this.sender = sender;
        this.messageTime = getCurrentTime();
        this.messageTimeColored = WHITE_BOLD_BRIGHT + this.messageTime + RESET;
        this.isCommand = this.message.startsWith("/");
        this.isExitCommand = this.message.equals("/exit");
        mode = ClientMessageMode.MESSAGE;
    }
    public ClientMessageModel(ClientModel sender, String message,T data) {
        this.message = message;
        this.coloredMessage = WHITE_BOLD_BRIGHT + this.message + RESET;
        this.sender = sender;
        this.messageTime = getCurrentTime();
        this.messageTimeColored = WHITE_BOLD_BRIGHT + this.messageTime + RESET;
        this.isCommand = this.message.startsWith("/");
        this.isExitCommand = this.message.equals("/exit");
        this.data = data;
        mode = ClientMessageMode.SIGN_INTERACT;
    }

    public String getFullMessage() {
        final String indicator = BLUE_BOLD_BRIGHT + " -> " + RESET;
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;

        return messageTime + indicator + sender.getColoredUsername() + colon + coloredMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getColoredMessage() {
        return coloredMessage;
    }

    public ClientModel getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getMessageTimeColored() {
        return messageTimeColored;
    }

    public boolean isCommand() {
        return isCommand;
    }

    public boolean isExitCommand() {
        return isExitCommand;
    }

    private String getCurrentTime() {
        return dateFormat.format(new Date());
    }

    public ClientMessageMode getMode() {
        return mode;
    }
}
