package com.example.dsdictionary.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;

    private DictionaryLoader dictionaryLoader;

    public ClientHandler(Socket socket, DictionaryLoader dictionaryLoader) {
        this.socket = socket;
        this.dictionaryLoader = dictionaryLoader;
    }


    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text;

            do {
                text = reader.readLine(); // 读取客户端发送的消息
                System.out.println("Received from client: " + text);
                writer.println("Echo: " + text); // 将接收到的消息回发给客户端
            } while (!text.equals("bye"));

            socket.close(); // 客户端发送 "bye" 后关闭连接
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
