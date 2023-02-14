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
import java.net.Socket;
import java.util.UUID;

import static utils.ConsoleDetail.CYAN_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class SignInteractHandler {

    private static final String SIGN_UP = "sign_up";
    private static final String LOGIN = "login";

    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private JsonRequestResponse listener;
    private boolean isServerOn;
    private boolean isKicked;
    private String currentOperation;

    public SignInteractHandler(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        isServerOn = true;
        listenForMessage();
    }

    public void listenForMessage() {
        final String colon = CYAN_BOLD_BRIGHT + ": " + RESET;

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
            if (socket != null)
                socket.close();

            if (outputStream != null)
                outputStream.close();

            if (inputStream != null)
                inputStream.close();

            if (!isServerOn || isKicked)
                System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signUp(InteractiveInterface<ClientModel> result, ClientModel newClient) {
        // /signup username password
        ClientMessageModel request = new ClientMessageModel<ClientModel>(null, SIGN_UP, newClient);
        if (isServerOn && socket.isConnected())
            sendRequest(request);
        listener = new JsonRequestResponse() {
            @Override
            public void result(JSONObject response) {
                // condition : SUCCESSFULL,ERROR,TAKEN,
                try {
                    boolean condition = response.getBoolean("condition");
                    String error = response.getString("error");
                    JSONObject clientModel = response.getJSONObject("model");
                    ClientModel client = new ClientModel(clientModel.getString("username"),
                            clientModel.getString("password"),
                            UUID.fromString(clientModel.getString("id")));
                    result.result(condition,error,client);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void login(InteractiveInterface<ClientModel> result,String username, String password) {
        ClientMessageModel request = new ClientMessageModel<ClientModel>(null, LOGIN, ClientModel.factory(username,password));
        if (isServerOn && socket.isConnected())
            sendRequest(request);
        listener = new JsonRequestResponse() {
            @Override
            public void result(JSONObject response) {
                // condition : SUCCESS,ERROR,TAKEN,
                try {
                    boolean condition = response.getBoolean("condition");
                    String error = response.getString("error");
                    JSONObject clientModel = response.getJSONObject("model");
                    ClientModel client = new ClientModel(clientModel.getString("username"),
                            clientModel.getString("password"),
                            UUID.fromString(clientModel.getString("id")));
                    result.result(condition,error,client);
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
