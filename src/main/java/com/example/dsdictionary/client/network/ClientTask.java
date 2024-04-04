package com.example.dsdictionary.client.network;

import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientTask implements Runnable{
    private String hostname;
    private int port;
    private String messageToSend;
    private Consumer<Response> onMessageReceived;

    public ClientTask(String hostname, int port, String messageToSend, Consumer<Response> onMessageReceived) {
        this.hostname = hostname;
        this.port = port;
        this.messageToSend = messageToSend;
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(hostname, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
             Gson gson = new Gson();
//            String jsonRequest = gson.toJson(messageToSend);
            // Send the message to the server
            writer.println(messageToSend);

            // Read the response from the server
            String jasnResponse = reader.readLine();

            if (jasnResponse != null) {
                // Deserialize the JSON response into a Response object
                Response response = gson.fromJson(jasnResponse, Response.class);
                // Use JavaFX thread to handle UI updates based on the received response
                Platform.runLater(() -> onMessageReceived.accept(response));
            }

        }catch (Exception e) {
//            Alert alert = new Alert(Alert.AlertType.ERROR, "The number of line breaks is not consistent across the strings.！");
//            alert.showAndWait();
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }

    }
}
