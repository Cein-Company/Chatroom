package server.commandserver;

import server.ChatServer;
import server.models.servermessage.ServerMessageMode;
import server.models.servermessage.ServerMessageModel;

import java.util.Locale;
import java.util.Scanner;

import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class ServerCommandShutdown {
    public static ServerMessageModel closeCommand() {
        System.out.println(RED_BOLD_BRIGHT + "YOU ARE ABOUT TO CLOSE & SHUTDOWN THE SEVER." + RESET);

        while (true) {
            System.out.println(RED_BOLD_BRIGHT + "\nDO YOU CONFIRM? (Y/N)" + RESET);

            String response = new Scanner(System.in).nextLine().trim().toLowerCase(Locale.ROOT);

            if (response.equals("y")) {
                ChatServer.closeServerSocket();
                return getServerShutdownMsg();
            } else if (response.equals("n")) {
                return getServerShutdownAbortedMsg();
            } else {
                System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
            }
        }
    }

    private static ServerMessageModel getServerShutdownMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "YOU HAVE CLOSED THE SERVER.");
    }

    private static ServerMessageModel getServerShutdownAbortedMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "SERVER SHUTDOWN ABORTED.");
    }
}
