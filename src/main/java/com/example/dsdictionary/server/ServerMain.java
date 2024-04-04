package com.example.dsdictionary.server;

import com.example.dsdictionary.database.DatabaseConnection;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.dsdictionary.models.MyThreadPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerMain {
    private static final Logger logger = LogManager.getLogger(ServerMain.class);
    private static final int DEFAULT_PORT = 20017;
    private static final String DEFAULT_DB_PATH = "assets/dictionary.db";

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String dbFilePath = DEFAULT_DB_PATH;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("Invalid port number provided. Using default port " + port);
                System.err.println("Invalid port number provided. Using default port " + port);
            }
        }

        if (args.length > 1) {
            dbFilePath = args[1];
        }

        System.out.println("Using port: " + port);
        System.out.println("Using database file: " + dbFilePath);


        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = DatabaseConnection.connect(dbFilePath);

        DictionaryService dictionaryService = new DictionaryService(connection);
        dictionaryService.printAll();

        int threadPoolSize =16;
        //ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        MyThreadPool myThreadPool = new MyThreadPool(threadPoolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket,dictionaryService);
                myThreadPool.execute(clientHandler);
                //executorService.execute(clientHandler);
            }
        } catch (Exception e) {
            logger.error("Server exception: " + e.getMessage());
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }finally {
            myThreadPool.shutdown();
            //executorService.shutdown();
        }
    }
}
