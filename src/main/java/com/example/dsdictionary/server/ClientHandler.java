package com.example.dsdictionary.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientHandler extends Thread {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private Socket socket;
    private DictionaryService dictionaryService;
    private Gson gson;

    public ClientHandler(Socket socket, DictionaryService dictionaryService) {
        this.socket = socket;
        this.dictionaryService = dictionaryService;
        this.gson = new Gson();
    }


    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String requestJson;
            while ((requestJson = reader.readLine()) != null) {
                Request request = gson.fromJson(requestJson, Request.class);
                if (request != null) {
                    String command = request.getCommand();
                    Word word = request.getWord();
//                    word = word.replace(" ","");
                    switch (command.toUpperCase()) {
                        case "CONNECT":
                            logger.info("Connect");
                            writer.println(gson.toJson(new Response("success","connect success")));
                            break;
                        case "GET":
                            logger.info("Get");
                            List<Meaning> meaning = dictionaryService.getMeaning(word.getWord());
                            if (meaning.isEmpty()) {
                                String meaningsJson = "";
                                Response response = new Response("empty", meaningsJson);
                                String responseJson = gson.toJson(response);
                                System.out.println(responseJson);
                                writer.println(responseJson);
                            }else{
                                String meaningsJson = gson.toJson(meaning);
                                System.out.println("JSON String: " + meaningsJson);
                                Response response = new Response("success", meaningsJson);
                                String responseJson = gson.toJson(response);
                                System.out.println(responseJson);
                                writer.println(responseJson);
                            }

                            break;
                        case "ADD":
                            logger.info("Add");
                            if (dictionaryService.addWord(word.getWord(),word.getMeanings())){
                                writer.println(gson.toJson(new Response("success","word added")));
                            }else {
                                writer.println(gson.toJson(new Response("failure","word exist")));
                            }
                            break;
                        case "REMOVE":
                            logger.info("Remove");
                            if (dictionaryService.removeWord(word.getWord())){
                                writer.println(gson.toJson(new Response("success","word removed")));
                            }else {
                                writer.println(gson.toJson(new Response("failure","word not exist")));
                            }
                            break;
                        case "UPDATE":
                            logger.info("Update");
                            boolean updateResult = dictionaryService.updateWord(word.getWord(), word.getMeanings().getFirst());
                            // 根据 updateResult 返回不同的响应给前端
                            if (updateResult) {
                                // 更新成功
                                writer.println(gson.toJson(new Response("success", "Word updated")));
                            } else {
                                // 更新失败，意义已存在
                                writer.println(gson.toJson(new Response("error", "Update failed: The meaning already exists")));
                            }
                            break;

                        case "INIT":
                            logger.info("Init");
                            String totalDictionary = gson.toJson(dictionaryService.getDictionary());
                            System.out.println(totalDictionary);
                            writer.println(gson.toJson(new Response("success",totalDictionary)));
                            break;
                        default:
                            writer.println(gson.toJson(new Response("error","wrong command")));
                            break;
                    }
                    }
            }
        } catch (IOException e) {
            logger.error("Error handling client request: " + e.getMessage());
            System.out.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                logger.info("Socket success close");
                socket.close();
            } catch (IOException e) {
                logger.error("Error closing socket: " + e.getMessage());
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
