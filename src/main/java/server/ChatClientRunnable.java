package server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

import static server.ChatServer.clients;

public class ChatClientRunnable implements Runnable {
    private final Socket socket;
    private final UUID id;
    private Scanner in;
    private PrintWriter out;

    ChatClientRunnable(Socket socket, UUID id) {
        this.socket = socket;
        this.id = id;
    }
    public PrintWriter getWriter() {
        return out;
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            while (!socket.isClosed()) {
                if (in.hasNext()) {
                    String userInput = in.nextLine();
                    System.out.println(userInput);
                    for (UUID clientId : clients.keySet()) {
                        if (clientId == id)
                            continue;
                        clients.get(clientId).getWriter().println(userInput);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
