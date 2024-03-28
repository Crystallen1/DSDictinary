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
            // 序列化请求对象为JSON字符串
            Gson gson = new Gson();
//            String jsonRequest = gson.toJson(messageToSend);
            // 发送消息到服务器
            writer.println(messageToSend);

            // 从服务器接收响应
            String jasnResponse = reader.readLine();

            if (jasnResponse != null) {
                Response response = gson.fromJson(jasnResponse, Response.class);
                // 使用JavaFX线程来更新UI
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
