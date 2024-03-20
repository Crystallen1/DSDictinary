package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String messageToSend = "{\"command\": \"connect\", \"word\": \" "+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            System.out.println("Received from server: " + response);
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务

        FXMLLoader fxmlLoader = new FXMLLoader(HomePage.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("Dictionary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}