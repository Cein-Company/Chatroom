package models.servermessage;

import java.io.Serializable;

public enum ServerMessageMode implements Serializable {
    ServerShutdownMsg,
    ServerKickMsg,
    FromSerer,
    FromServerAboutClient,
    FromClient,
    ToAdminister,
    ToAdministerAboutAClient,
    PMFromServerToClient,
    PMFromClientToClient,
    PMFromClientToServer,
    ListFromServer,
    SignInteract
}