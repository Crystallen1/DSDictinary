package com.example.dsdictionary.server;

import java.io.*;
import java.net.Socket;

import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import com.google.gson.Gson;


public class ClientHandler extends Thread {
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
                    String word = request.getWord();
                    word = word.replace(" ","");
                    switch (command.toUpperCase()) {
                        case "CONNECT":
                            writer.println(gson.toJson(new Response("success","connect success")));
                            break;
                        case "GET":
                            String meaning = dictionaryService.getMeaning(word);
                            System.out.println(word);
                            writer.println(gson.toJson(new Response("success",meaning)));
                            break;
                        case "ADD":
                            String meaningToAdd = request.getMeaning();
                            dictionaryService.addWord(word,meaningToAdd);
                            writer.println(gson.toJson(new Response("success","word added")));
                            break;
                        case "REMOVE":
                            dictionaryService.removeWord(word);
                            writer.println(gson.toJson(new Response("success","word removed")));
                            break;
                        case "UPDATE":
                            String meaningToUpdate = request.getMeaning();
                            dictionaryService.updateWord(word,meaningToUpdate);
                            writer.println(gson.toJson(new Response("success","Word updated")));
                            break;
                        case "INIT":
                            String totalDictionary = gson.toJson(dictionaryService.getDictionary());
                            writer.println(gson.toJson(new Response("success",totalDictionary)));
                            break;
                        default:
                            writer.println(gson.toJson(new Response("error","wrong command")));
                            break;
                    }
                    }
            }
        } catch (IOException e) {
            System.out.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
