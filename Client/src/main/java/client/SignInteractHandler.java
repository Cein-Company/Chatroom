package client;

import models.clientmodels.ClientMessageMode;
import models.clientmodels.ClientMessageModel;
import models.clientmodels.ClientModel;
import org.json.JSONException;
import org.json.JSONObject;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;
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
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private JsonRequestResponse listener;
    private boolean isServerOn;

    // TODO: Why is isKicked here?
    private boolean isKicked;

    private boolean initialConnectionResponse;

    public SignInteractHandler() {
        setUpSocket();
    }

    private void listenForMessage() {
        new Thread(() -> {
            ServerMessageModel response;

            while (isServerOn && socket.isConnected()) {
                try {
                    if (isServerOn) {
                        response = (ServerMessageModel) objectInputStream.readObject();

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

    private void closeEverything() {
        try {
            isServerOn = false;

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

    private void setUpSocket() {
        outer:
        while (true) {
            try {
                this.socket = new Socket(InetAddress.getLoopbackAddress(), 4444);
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());

                ClientMessageModel initialConnectionMsg = new ClientMessageModel(ClientMessageMode.INITIAL_CONNECTION);
                sendRequest(initialConnectionMsg);

                isServerOn = true;
                initialConnectionResponse = true;

                listenForMessage();
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
        ClientMessageModel signUpRequest = new ClientMessageModel(ClientMessageMode.SIGNING_IN, newClient);

        // TODO: Why setUpSocket when server is off?
        if (initialConnectionResponse && socket.isConnected()) {
            sendRequest(signUpRequest);
        } else {
            setUpSocket();
        }

        listener = response -> {
            // condition : SUCCESS, ERROR, TAKEN
            try {
                boolean condition = response.getBoolean("condition");
                String content = response.getString("content");
                ClientModel client = null;

                if (condition) {
                    JSONObject clientModelJsonResponse = response.getJSONObject("client");
                    client = new ClientModel(
                            clientModelJsonResponse.getString("username"),
                            clientModelJsonResponse.getString("password"),
                            UUID.fromString(clientModelJsonResponse.getString("id")),
                            clientModelJsonResponse.getString("CLIENT_COLOR"));
                }

                result.result(condition, content, client);
                if (isServerOn)
                    closeEverything();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    public void login(InteractiveInterface<ClientModel> result, String username, String password) {
        ClientMessageModel loginRequest = new ClientMessageModel(ClientMessageMode.LOGIN_IN, ClientModel.factory(username, password));

        if (initialConnectionResponse && socket.isConnected())
            sendRequest(loginRequest);
        else
            setUpSocket();

        listener = response -> {
            // condition : SUCCESS, ERROR, TAKEN
            try {
                boolean condition = response.getBoolean("condition");
                String content = response.getString("content");
                ClientModel client = null;

                if (condition) {
                    JSONObject clientModelJsonResponse = response.getJSONObject("client");
                    client = new ClientModel(
                            clientModelJsonResponse.getString("username"),
                            clientModelJsonResponse.getString("password"),
                            UUID.fromString(clientModelJsonResponse.getString("id")),
                            clientModelJsonResponse.getString("CLIENT_COLOR"));
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
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
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
