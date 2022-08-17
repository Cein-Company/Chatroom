package client;

import files.ActiveUsersFiles;
import files.UserFiles;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static utils.consts.ConsoleColors.*;
import static utils.consts.ConsoleColors.RESET;

public class ChatClientCLI {
    private static final Map<String, String> users = new HashMap<>();
    private static final ArrayList<String> activeUsers = new ArrayList<>();

    static void startMenu(Socket socket) {
        System.out.println(
                """
                \033[1;97mWelcome to our local chatroom.
                                
                1. Sign up
                2. Login
                3. Exit
                \033[0m""");

        label:
        while (true) {
            System.out.print(CYAN_BOLD_BRIGHT + ">" + RESET);

            String choice = new Scanner(System.in).nextLine();

            switch (choice) {
                case "1":
                    signUp(socket);
                    break label;
                case "2":
                    login(socket);
                    break label;
                case "3":
                    try {
                        System.out.print(RED_BOLD_BRIGHT + "You have left the chatroom." + RESET);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break label;
                case "":
                    continue;
                default:
                    System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
                    break;
            }
        }
    }

    private static void signUp(Socket socket) {
        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine();

            if (username.equals("0")) {
                startMenu(socket);
                return;
            }

            if (users.containsKey(username)) {
                System.out.println(RED_BOLD_BRIGHT + "Username taken. Try again." + RESET);
                continue;
            }

            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine();

            if (password.equals("0")) {
                startMenu(socket);
                return;
            }

            users.put(username, password);
            UserFiles.writeUsers(users);

            activeUsers.add(username);
            ActiveUsersFiles.writeUsers(activeUsers);

            System.out.println(CYAN_BOLD_BRIGHT +
                    "Sign in successful. You can start chatting now.\n" +
                    "To exit the chatroom, just write 'exit'.\n" + RESET);
            break;
        }

        ChatClient.runChat(socket, username);
    }

    private static void login(Socket socket) {
        ArrayList<String> temp = ActiveUsersFiles.readUsers();
        if (temp != null)
            activeUsers.addAll(temp);

        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine();

            if (username.equals("0")) {
                startMenu(socket);
                return;
            }

            if (!users.containsKey(username)) {
                System.out.println(RED_BOLD_BRIGHT + "No such username was found. Try again." + RESET);
                continue;
            }

            if (activeUsers.contains(username)) {
                System.out.println(RED_BOLD_BRIGHT + "User is already in the chatroom." + RESET);
                startMenu(socket);
                return;
            }

            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine();

            if (password.equals("0")) {
                startMenu(socket);
                return;
            }

            if (!users.get(username).equals(password)) {
                System.out.println(RED_BOLD_BRIGHT + "Password incorrect. Try again." + RESET);
                continue;
            }

            System.out.println(CYAN_BOLD_BRIGHT +
                    "Login successful. You can start chatting now.\n" +
                    "To exit the chatroom, just write 'exit'.\n" + RESET);

            activeUsers.add(username);
            ActiveUsersFiles.writeUsers(activeUsers);

            break;
        }

        ChatClient.runChat(socket, username);
    }

    public static Map<String, String> getUsers() {
        return users;
    }

    public static ArrayList<String> getActiveUsers() {
        return activeUsers;
    }
}
