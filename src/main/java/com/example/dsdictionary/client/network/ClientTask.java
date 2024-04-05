package com.example.dsdictionary.client.network;

import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.function.Consumer;

import static com.example.dsdictionary.client.GUI.HomePage.hostName;

public class ClientTask implements Runnable{
    private String hostname;
    private int port;
    private String messageToSend;
    private Consumer<Response> onMessageReceived;

    int connectionTimeout = 10000; // 连接超时时间，例如10秒
    int readTimeout = 5000; // 读取超时时间，例如5秒

    public ClientTask(String hostname, int port, String messageToSend, Consumer<Response> onMessageReceived) {
        this.hostname = hostname;
        this.port = port;
        this.messageToSend = messageToSend;
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void run() {
        try {
            // 创建一个未连接的Socket
            Socket socket = new Socket();

            // 设置连接超时
            socket.connect(new InetSocketAddress(hostname, port), connectionTimeout);

            // 设置读取超时
            socket.setSoTimeout(readTimeout);

            // 作为资源管理的一部分创建PrintWriter和BufferedReader
            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
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
            }
        }catch (SocketException e) {
            // 处理网络连接问题
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接出现问题，请检查你的网络连接。!");
            alert.showAndWait();});
        } catch (UnknownHostException e) {
            // 处理主机解析问题
            System.err.println("无法解析主机地址: " + hostName);
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "无法解析主机地址，请检查网络连接或服务器地址是否正确!");
            alert.showAndWait();});
        } catch (SocketTimeoutException e) {
            // 处理请求超时
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "网络请求超时，请检查网络连接并重试!");
            alert.showAndWait();});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
