package client;

import client.models.ClientModel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static utils.ConsoleDetail.*;

// TODO: Sign-Up let's user reuse used Usernames
// TODO: Wrong Password Exception doesn't work correctly
// TODO: startMenu is not looped
// TODO: sign-up & login empty values
// TODO: Client's chat enter message doesn't show for themself

public class ChatClientCLI {

    private static SignInteractHandler signHandler;
    private static Socket serverSocket;
    private static final int DURATION = 1000;
    private static ClientModel client;

    public static void startMenu() throws InterruptedException {
        System.out.println(
                """
                        \033[1;96mWelcome to our local chatroom.\033[0m
                        \033[1;97m
                        1. Sign up
                        2. Login
                        3. Exit
                        \033[0m""");

        System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

        String choice = new Scanner(System.in).nextLine();

        switch (choice) {
            case "1" -> signUp();
            case "2" -> login();
            case "3" -> System.out.print(RED_BOLD_BRIGHT + "You have left the chatroom." + RESET);
            default -> System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
        }
    }

    private static void signUp() throws InterruptedException {
        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine();

            if (username.equals("0")) {
                startMenu();
                return;
            }

            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine();

            if (password.equals("0")) {
                startMenu();
                return;
            }

            final boolean[] hasResult = {false};
            signHandler.signUp((result, message, data) -> {
                System.out.println(message);
                hasResult[0] = result;
                client = data;
            }, ClientModel.factory(username, password));

            Thread.sleep(DURATION);

            if (hasResult[0])
                startChat(client);
            else
                startMenu();
            break;
        }
    }

    private static void login() throws InterruptedException {
        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine().trim();

            if (username.equals("0")) {
                startMenu();
                return;
            }
            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine().trim();
            if (password.equals("0")) {
                startMenu();
                return;
            }
            final boolean[] hasResult = {false};
            signHandler.login((result, message, data) -> {
                System.out.println(message);
                hasResult[0] = result;
                client = data;
            }, username, password);

            Thread.sleep(DURATION);

            if (hasResult[0])
                startChat(client);
            else
                startMenu();
            break;
        }
    }

    private static void startChat(ClientModel clientModel) {
        try {
            serverSocket = new Socket(InetAddress.getLoopbackAddress(), 4444);
        } catch (IOException e) {
            System.out.println(RED_BOLD_BRIGHT + "AN ERROR OCCURRED DURING CONNECTING TO SERVER" + RESET);
        }
        ChatClient client = new ChatClient(serverSocket, clientModel);

        System.out.println(CYAN_BOLD_BRIGHT +
                "To see a list of available commands, use '/help'.\n" +
                "To exit the chatroom, just write '/exit'.\n" + RESET);

        client.listenForMessage();
        client.sendMessage();

    }

    public static void main(String[] args) {
        signHandler = new SignInteractHandler();

        if (signHandler.isServerOn()) {
            System.out.println(CYAN_BOLD_BRIGHT + "CONNECTION TO SERVER WAS SUCCESSFUL." + RESET + "\n");
            try {
                startMenu();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(RED_BOLD_BRIGHT + """
                    CONNECTING TO SERVER WAS UNSUCCESSFUL.
                    CLOSING THE APP NOW...""" + RESET);
        }
    }
}
