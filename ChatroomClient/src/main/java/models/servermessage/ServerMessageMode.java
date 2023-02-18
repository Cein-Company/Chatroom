package models.servermessage;

import java.io.Serializable;

public enum ServerMessageMode implements Serializable {
    ServerShutdownMsg,
    ServerKickMsg,
    GoodbyeFromServer,
    FromServer,
    FromServerAboutClient,
    FromClient,
    ToAdminister,
    ToAdministerAboutAClient,
    FromServerToClient,
    PMFromServerToClient,
    PMFromClientToClient,
    PMFromClientToServer,
    ListFromServer,
    SignInteract
}