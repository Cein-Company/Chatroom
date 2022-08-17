package client;

import files.ActiveUsersFiles;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static client.ChatClientCLI.getActiveUsers;
import static utils.consts.ConsoleColors.*;

record ChatServerRunnable(Socket socket, String username) implements Runnable {
    private static final String CLIENT_COLOR = BOLD_BRIGHTS_COLORS[new Random().nextInt(BOLD_BRIGHTS_COLORS.length)];

    @Override
    public void run() {
        String userInput;

        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

            out.println(CLIENT_COLOR + username + " has entered the chatroom." + RESET);

            System.out.print(CLIENT_COLOR + "Your message: " + RESET);

            while (!socket.isClosed()) {
                if (in.ready()) {
                    System.out.print("\n" + GREEN_BOLD_BRIGHT + "Server: " + RESET);
                    String msg = in.readLine();
                    System.out.println(msg);
                    System.out.print(CLIENT_COLOR + "Your message: " + RESET);
                }

                if (userIn.ready()) {
                    userInput = userIn.readLine();

                    if (userInput.equals("exit")) {
                        out.println(RED_BOLD_BRIGHT + username + " has left the chatroom." + RESET);
                        System.out.print(RED_BOLD_BRIGHT + "You have left the chatroom. Goodbye." + RESET);

                        getActiveUsers().remove(username);
                        ActiveUsersFiles.writeUsers(getActiveUsers());

                        socket.close();
                    } else if (userInput.equals("")) {
                        continue;
                    } else {
                        out.println(
                                CLIENT_COLOR + username + RESET +
                                        CYAN_BOLD_BRIGHT + " ====> " + RESET +
                                        CLIENT_COLOR + userInput + RESET);

                        System.out.print(CLIENT_COLOR + "Your message: " + RESET);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}