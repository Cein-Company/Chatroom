package com.company;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ChatServer {

    static ServerSocket serverSocket;
    static Map<UUID,ChatClientRunnable> clients;
    public static void main(String[] args) {
	final int portNumber=4444;
        try {
            serverSocket=new ServerSocket(portNumber);
            acceptClients();
        } catch (IOException e) {
            System.err.println("Could not listen to port");
            e.printStackTrace();
        }

    }
    private static void acceptClients()
    {
        clients=new HashMap<>();
        while (true)
        {
                try {
                    Socket socket=serverSocket.accept();
                    System.out.println("New User Connection");
                    UUID id=UUID.randomUUID();
                    ChatClientRunnable client=new ChatClientRunnable(socket, id);
                    Thread thread=new Thread(client);
                    thread.start();
                    clients.put(id,client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    private static class ChatClientRunnable implements Runnable
    {
        private Socket socket;
        private UUID id;
        private Scanner in;
        private PrintWriter out;

        private ChatClientRunnable(Socket socket, UUID id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run()
        {
            try {
                in=new Scanner(socket.getInputStream(),"UTF-8");
                out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
                while (!socket.isClosed())
                {
                    if(in.hasNext())
                    {
                        String s=in.nextLine();
                        System.out.println(s);
                        for(UUID clientId:clients.keySet())
                        {
                            if(clientId==id)continue;
                            clients.get(clientId).getWriter().println(s);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public PrintWriter getWriter() {
            return out;
        }
    }
}
