package com.example.dsdictionary.server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        int port = 20016; // 选择一个端口号进行监听
        String dictionaryFilePath = "assets/dictionary.txt";

        DictionaryLoader dictionaryLoader = new DictionaryLoader(dictionaryFilePath);
        dictionaryLoader.printAll();


        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);



            while (true) {
                Socket socket = serverSocket.accept(); // 等待客户端连接
                System.out.println("New client connected");

                new ClientHandler(socket,dictionaryLoader).start(); // 为每个客户端创建一个新线程
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
