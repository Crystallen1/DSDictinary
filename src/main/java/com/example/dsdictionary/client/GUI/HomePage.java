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
import java.util.List;
import java.util.Locale;

public class HomePage extends Application {
    public static int port = 20017;
    public static String hostName = "localhost";


    @Override
    public void init() throws Exception {
        Parameters params = getParameters();
        List<String> rawArgs = params.getRaw();
        if (!rawArgs.isEmpty()) {
            // Set the host name from the first argument
            hostName = rawArgs.get(0);

            // Try to parse the port number from the second argument, if present
            if (rawArgs.size() > 1) {
                try {
                    port = Integer.parseInt(rawArgs.get(1));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number. Using default port " + port);
                }
            }
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        Word tempWord = new Word("");
        Request request=new Request("connect",tempWord);
        Gson gson = new Gson();
        String messageToSend = gson.toJson(request);

        ClientTask clientTask = new ClientTask(hostName, port, messageToSend, response -> {
            System.out.println("Received from server: " + response);
        });
        new Thread(clientTask).start();

        FXMLLoader fxmlLoader = new FXMLLoader(HomePage.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("Dictionary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US")); // 设置默认语言为英文
        launch(args);
    }
}