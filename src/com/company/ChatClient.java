package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        final int portNumber=4444;
        try {
            Socket socket=new Socket(InetAddress.getLoopbackAddress(),portNumber);
            System.out.println("Enter your name! ");
            String name=new Scanner(System.in).nextLine();
            ChatServerRunnable server =new ChatServerRunnable(socket,name);
            Thread thread=new Thread(server);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static class ChatServerRunnable implements Runnable
    {
        private Socket socket;
        private String name;
        private PrintWriter out;

        private BufferedReader in;
        private BufferedReader userIn;
        private ChatServerRunnable(Socket socket, String name) {
            this.socket = socket;
            this.name = name;
        }

        @Override
        public void run()
        {
            try {
                out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
                in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                userIn=new BufferedReader(new InputStreamReader(System.in));
                while (!socket.isClosed())
                {
                    if(in.ready())
                    {
                        System.out.println("server Message:");
                        String msg=in.readLine();
                        System.out.println(msg);
                    }
                    if(userIn.ready())
                    {
                        out.println(name+"====> "+userIn.readLine());
                        System.out.println("sent!");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
