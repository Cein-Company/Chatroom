package client.models;

import java.io.Serializable;

public enum ClientMessageMode implements Serializable {
    INITIAL_CONNECTION, SIGNING_IN, LOGIN_IN, MESSAGE
}
