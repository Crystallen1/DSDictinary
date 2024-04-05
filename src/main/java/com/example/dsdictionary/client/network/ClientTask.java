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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                // 创建一个未连接的Socket
                Socket socket = new Socket();

                // 异步执行DNS解析和连接
                socket.connect(new InetSocketAddress(hostname, port), connectionTimeout);

                // 设置读取超时
                socket.setSoTimeout(readTimeout);

                try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    Gson gson = new Gson();
                    writer.println(messageToSend);

                    String jsonResponse = reader.readLine();
                    if (jsonResponse != null) {
                        Response response = gson.fromJson(jsonResponse, Response.class);
                        Platform.runLater(() -> onMessageReceived.accept(response));
                    }
                }
            } catch (SocketException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Network connection error, please check your network connection.");
                    alert.showAndWait();
                });
            } catch (UnknownHostException e) {
                System.err.println("Unable to resolve the host address: " + hostName);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to resolve the host address, please check if the network connection or server address is correct.");
                    alert.showAndWait();
                });
            } catch (SocketTimeoutException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Network request timed out, please check your network connection and try again.");
                    alert.showAndWait();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown(); // 不要忘记关闭ExecutorService来释放资源
    }

}
