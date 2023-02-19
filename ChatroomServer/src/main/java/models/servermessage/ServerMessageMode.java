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
    FromServerAdmin,
    FromServerToClient,
    PMFromAdminToClient,
    PMFromClientToClient,
    PMFromClientToAdmin,
    ListFromServer,
    SignInteract
}