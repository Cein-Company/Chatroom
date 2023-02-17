package client;

import client.models.ClientMessageMode;
import client.models.ClientMessageModel;
import client.models.ClientModel;
import org.json.JSONException;
import org.json.JSONObject;
import server.models.ServerMessageMode;
import server.models.ServerMessageModel;
import utils.InteractiveInterface;
import utils.JsonRequestResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

import static utils.ConsoleDetail.*;

public class SignInteractHandler {
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private JsonRequestResponse listener;
    private boolean isServerOn;

    // TODO: Why is isKicked here?
    private boolean isKicked;

    private boolean initialConnectionResponse;

    public SignInteractHandler() {
        setUpSocket();
    }

    public void listenForMessage() {
        new Thread(() -> {
            ServerMessageModel response;

            while (isServerOn && socket.isConnected()) {
                try {
                    if (isServerOn) {
                        response = (ServerMessageModel) inputStream.readObject();

                        if (response != null) {
                            if (response.getMessageMode().equals(ServerMessageMode.ServerShutdownMsg)) {
                                isServerOn = false;
                                System.out.println(response.getFullMessage());
                                closeEverything();
                                break;
                            }

                            if (response.getMessageMode().equals(ServerMessageMode.ServerKickMsg)) {
                                isKicked = true;
                                System.out.println(response.getFullMessage());
                                closeEverything();
                                break;
                            }

                            checkResponse(response);
                        }
                    }
                } catch (IOException e) {
                    if (isServerOn)
                        closeEverything();
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    /**
     * @param response -> Server response to client request . the response is a JSON object containing details LIKE content, error, and condition,...
     *                 <br><b>requests </b><br>
     *                 {<br>
     *                 1- sing_up<br>
     *                 2- login<br>
     *                 }
     */
    private void checkResponse(ServerMessageModel response) {
        if (response.getMessageMode().equals(ServerMessageMode.SignInteract)) {
            try {
                JSONObject responseJso = new JSONObject(response.getMessage());
                listener.result(responseJso);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeEverything() {
        try {
            isServerOn = false;

            if (socket != null)
                socket.close();

            if (outputStream != null)
                outputStream.close();

            if (inputStream != null)
                inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpSocket() {
        outer:
        while (true) {
            try {
                this.socket = new Socket(InetAddress.getLoopbackAddress(), 4444);
                this.outputStream = new ObjectOutputStream(socket.getOutputStream());
                this.inputStream = new ObjectInputStream(socket.getInputStream());

                ClientMessageModel initialConnectionMsg = new ClientMessageModel(ClientMessageMode.INITIAL_CONNECTION);
                outputStream.writeObject(initialConnectionMsg);
                outputStream.flush();

                isServerOn = true;
                initialConnectionResponse = true;

                listenForMessage();
                // closeEverything();
                break;
            } catch (IOException e) {
                System.out.println(RED_BOLD_BRIGHT + "AN ERROR OCCURRED DURING CONNECTING TO SERVER" + RESET);
                inner:
                while (true) {
                    System.out.println(CYAN_BOLD_BRIGHT + "Try Again? (Y/N)" + RESET);
                    System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

                    switch (new Scanner(System.in).nextLine().trim().toLowerCase()) {
                        case "y" -> {
                            break inner;
                        }
                        case "n" -> {
                            isServerOn = false;
                            break outer;
                        }
                        default -> System.out.println(RED_BOLD_BRIGHT + "Invalid Command." + RESET);
                    }
                }
            }
        }
    }

    public void signUp(InteractiveInterface<ClientModel> result, ClientModel newClient) {
        // /signup username password
        ClientMessageModel request = new ClientMessageModel<ClientModel>(ClientMessageMode.SIGNING_IN, newClient);

        // TODO: Why setUpSocket when server is off?
        if (initialConnectionResponse && socket.isConnected()) {
            sendRequest(request);
        } else {
            setUpSocket();
        }

        listener = response -> {
            // condition : SUCCESSFUL, ERROR, TAKEN,
            try {
                boolean condition = response.getBoolean("condition");
                String error = response.getString("content");
                ClientModel client = null;

                if (condition) {
                    JSONObject clientModel = response.getJSONObject("client");
                    client = new ClientModel(clientModel.getString("username"),
                            clientModel.getString("password"),
                            UUID.fromString(clientModel.getString("id")));
                }

                result.result(condition, error, client);
                if (isServerOn)
                    closeEverything();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    public void login(InteractiveInterface<ClientModel> result, String username, String password) {
        ClientMessageModel request = new ClientMessageModel<ClientModel>(ClientMessageMode.LOGIN_IN, ClientModel.factory(username, password));

        if (initialConnectionResponse && socket.isConnected())
            sendRequest(request);
        else
            setUpSocket();

        listener = response -> {
            // condition : SUCCESS, ERROR, TAKEN,
            try {
                boolean condition = response.getBoolean("condition");
                String content = response.getString("content");
                ClientModel client = null;

                if (condition) {
                    JSONObject clientModel = response.getJSONObject("client");
                    client = new ClientModel(clientModel.getString("username"),
                            clientModel.getString("password"),
                            UUID.fromString(clientModel.getString("id")));
                }

                result.result(condition, content, client);
                if (isServerOn)
                    closeEverything();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    private void sendRequest(ClientMessageModel request) {
        try {
            outputStream.writeObject(request);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isServerOn() {
        return isServerOn;
    }

    public boolean getInitialConnectionResponse() {
        return initialConnectionResponse;
    }
}
