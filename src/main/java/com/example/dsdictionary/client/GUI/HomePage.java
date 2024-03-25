package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.Word;
import com.example.dsdictionary.protocol.Request;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Word tempWord = new Word("");
        Request request=new Request("connect",tempWord);
        Gson gson = new Gson();
        String messageToSend = gson.toJson(request);

        ClientTask clientTask = new ClientTask("localhost", 20017, messageToSend, response -> {
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