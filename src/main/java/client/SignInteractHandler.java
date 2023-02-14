package client;

import client.models.ClientMessageMode;
import client.models.ClientMessageModel;
import client.models.ClientModel;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
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

    private static final String SIGN_UP = "sign_up";
    private static final String LOGIN = "login";

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private JsonRequestResponse listener;
    private boolean isServerOn;
    private boolean isKicked;

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
        if (response.getMessageMode() == ServerMessageMode.SignInteract) {
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

            if (outputStream != null)
                outputStream.close();

            if (inputStream != null)
                inputStream.close();
            if (socket != null)
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpSocket() {
        try {
            this.socket = new Socket(InetAddress.getLoopbackAddress(), 4444);
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream.writeObject("SIGN_INTERACT");
            outputStream.flush();
            isServerOn = true;
            listenForMessage();
        } catch (IOException e) {
            System.out.println(RED_BOLD_BRIGHT + "AN ERROR OCCURRED DURING CONNECTING TO SERVER" + RESET);
            System.out.println("try again ?(Y/n)");
            switch (new Scanner(System.in).nextLine().trim().toLowerCase()) {
                case "y":
                    setUpSocket();
                default:
                    return;
            }
        }
    }

    public void signUp(InteractiveInterface<ClientModel> result, ClientModel newClient) {
        // /signup username password
        ClientMessageModel request = new ClientMessageModel<ClientModel>(null, SIGN_UP, newClient);
        if (isServerOn && socket.isConnected())
            sendRequest(request);
        else {
            setUpSocket();
        }
        listener = new JsonRequestResponse() {
            @Override
            public void result(JSONObject response) {
                // condition : SUCCESSFULL,ERROR,TAKEN,
                try {
                    boolean condition = response.getBoolean("condition");
                    String error = response.getString("content");
                    JSONObject clientModel = response.getJSONObject("client");
                    ClientModel client = new ClientModel(clientModel.getString("username"),
                            clientModel.getString("password"),
                            UUID.fromString(clientModel.getString("id")));
                    result.result(condition, error, client);
                    closeEverything();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void login(InteractiveInterface<ClientModel> result, String username, String password) {
        ClientMessageModel request = new ClientMessageModel<ClientModel>(null, LOGIN, ClientModel.factory(username, password));
        if (isServerOn && socket.isConnected())
            sendRequest(request);
        else
            setUpSocket();
        listener = new JsonRequestResponse() {
            @Override
            public void result(JSONObject response) {
                // condition : SUCCESS,ERROR,TAKEN,
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
                    closeEverything();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

}
