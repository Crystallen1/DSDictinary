package com.example.dsdictionary.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
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
                    Word word = request.getWord();
//                    word = word.replace(" ","");
                    switch (command.toUpperCase()) {
                        case "CONNECT":
                            writer.println(gson.toJson(new Response("success","connect success")));
                            break;
                        case "GET":
                            List<Meaning> meaning = dictionaryService.getMeaning(word.getWord());
                            String meaningsJson = gson.toJson(meaning);
                            System.out.println("JSON String: " + meaningsJson);
                            Response response = new Response("success", meaningsJson);
                            String responseJson = gson.toJson(response);
                            System.out.println(responseJson);
                            writer.println(responseJson);
                            break;
                        case "ADD":
                            dictionaryService.addWord(word.getWord(),word.getMeanings());
                            writer.println(gson.toJson(new Response("success","word added")));
                            break;
                        case "REMOVE":
                            dictionaryService.removeWord(word.getWord());
                            writer.println(gson.toJson(new Response("success","word removed")));
                            break;
                        case "UPDATE":
                            //String meaningToUpdate = request.getMeaning();
                            dictionaryService.updateWord(word.getWord(),word.getMeanings().getFirst());
                            writer.println(gson.toJson(new Response("success","Word updated")));
                            break;
                        case "INIT":
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
