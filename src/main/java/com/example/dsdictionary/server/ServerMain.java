package com.example.dsdictionary.server;

import com.example.dsdictionary.database.DatabaseConnection;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) {
        int port = 20017; // 选择一个端口号进行监听

        //创建一个存储字典数据的对象
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = DatabaseConnection.connect();

        DictionaryService dictionaryService = new DictionaryService(connection);
       dictionaryService.printAll();

        //创建线程池
        int threadPoolSize =10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);//创建一个固定大小的线程池

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept(); // 等待客户端连接
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket,dictionaryService); // 为每个客户端创建一个新线程
                executorService.execute(clientHandler); // 使用线程池来管理线程
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }finally {
            executorService.shutdown(); // 关闭线程池
        }
    }
}
