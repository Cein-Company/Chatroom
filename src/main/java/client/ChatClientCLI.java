package client;

import files.ActiveUsersFiles;
import files.UsersFiles;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static utils.consts.ConsoleDetail.*;
import static utils.consts.ConsoleDetail.RESET;

public class ChatClientCLI {
    private static final Map<String, ClientModel> users = new HashMap<>();
    private static final ArrayList<String> activeUsers = new ArrayList<>();

    public static void startMenu() {
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
                    signUp();
                    break label;
                case "2":
                    login();
                    break label;
                case "3":
                    System.out.print(RED_BOLD_BRIGHT + "You have left the chatroom." + RESET);
                    break label;
                case "":
                    continue;
                default:
                    System.out.println(RED_BOLD_BRIGHT + "Please choose correctly." + RESET);
                    break;
            }
        }
    }

    private static void signUp() {
        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine();

            if (username.equals("0")) {
                startMenu();
                return;
            }

            if (getUsers().containsKey(username)) {
                System.out.println(RED_BOLD_BRIGHT + "Username taken. Try again." + RESET);
                continue;
            }

            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine();

            if (password.equals("0")) {
                startMenu();
                return;
            }

            ClientModel client = new ClientModel(username, password);

            getUsers().put(username, client);
            UsersFiles.writeUsers(users);

            startChat(username);

            break;
        }
    }

    private static void login() {
        String username;
        String password;

        while (true) {
            System.out.print(WHITE_BOLD_BRIGHT + "Username ('0' to return) : " + RESET);
            username = new Scanner(System.in).nextLine();

            if (username.equals("0")) {
                startMenu();
                return;
            }

            if (!getUsers().containsKey(username)) {
                System.out.println(RED_BOLD_BRIGHT + "No such username was found. Try again." + RESET);
                continue;
            }

            if (getActiveUsers().contains(username)) {
                System.out.println(RED_BOLD_BRIGHT + "User is already in the chatroom." + RESET);
                startMenu();
                return;
            }

            System.out.print(WHITE_BOLD_BRIGHT + "Password ('0' to return) : " + RESET);
            password = new Scanner(System.in).nextLine();

            if (password.equals("0")) {
                startMenu();
                return;
            }

            if (!getUsers().get(username).getPassword().equals(password)) {
                System.out.println(RED_BOLD_BRIGHT + "Password incorrect. Try again." + RESET);
                continue;
            }

            startChat(username);

            break;
        }
    }

    private static void startChat(String username) {
        try {
            Socket socket = new Socket(InetAddress.getLoopbackAddress(), 4444);
            ChatClient client = new ChatClient(socket, users.get(username));

            System.out.println(CYAN_BOLD_BRIGHT +
                    "Login successful. You can start chatting now.\n" +
                    "To exit the chatroom, just write '/exit'.\n" + RESET);

            addActiveUsers(username);

            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println(RED_BOLD_BRIGHT + "NO SERVER WAS FOUND" + RESET);
            e.printStackTrace();
        }
    }

    public static Map<String, ClientModel> getUsers() {
        Map<String, ClientModel> temp = UsersFiles.readUsers();
        if (temp != null) {
            users.clear();
            users.putAll(temp);
        }

        return users;
    }

    public static ArrayList<String> getActiveUsers() {
        ArrayList<String> tempActiveUsers = ActiveUsersFiles.readActiveUsers();
        if (tempActiveUsers != null) {
            activeUsers.clear();
            activeUsers.addAll(tempActiveUsers);
        }

        return activeUsers;
    }

    public static void addActiveUsers(String username) {
        getActiveUsers().add(username);
        ActiveUsersFiles.writeActiveUsers(activeUsers);
    }

    public static void removeActiveUsers(String username) {
        getActiveUsers().remove(username);
        ActiveUsersFiles.writeActiveUsers(activeUsers);
    }

    public static void main(String[] args) {
        startMenu();
    }
}
