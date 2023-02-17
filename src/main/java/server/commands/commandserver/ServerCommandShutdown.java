package server.commands.commandserver;

import server.ChatServer;
import models.servermessage.ServerMessageMode;
import models.servermessage.ServerMessageModel;

import java.util.Locale;
import java.util.Scanner;

import static utils.ConsoleDetail.RED_BOLD_BRIGHT;
import static utils.ConsoleDetail.RESET;

public class ServerCommandShutdown {
    protected static ServerMessageModel closeCommand() {
        System.out.println(RED_BOLD_BRIGHT + "YOU ARE ABOUT TO CLOSE & SHUTDOWN THE SEVER." + RESET);

        while (true) {
            System.out.println(RED_BOLD_BRIGHT + "\nDO YOU CONFIRM? (Y/N)" + RESET);

            String response = new Scanner(System.in).nextLine().trim().toLowerCase(Locale.ROOT);

            if (response.equals("y")) {
                return getServerShutdownMsg();
            } else if (response.equals("n")) {
                return getServerShutdownAbortedMsg();
            } else {
                System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
            }
        }
    }

    private static ServerMessageModel getServerShutdownMsg() {
        return new ServerMessageModel(ServerMessageMode.ServerShutdownMsg, "YOU HAVE CLOSED THE SERVER.");
    }

    private static ServerMessageModel getServerShutdownAbortedMsg() {
        return new ServerMessageModel(ServerMessageMode.ToAdminister, "SERVER SHUTDOWN ABORTED.");
    }
}
