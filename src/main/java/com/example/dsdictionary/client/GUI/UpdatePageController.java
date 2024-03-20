package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class UpdatePageController {
    @FXML
    public TextArea wordText;
    @FXML
    public TextArea partOfSpeechText;
    @FXML
    public TextArea meaningText;

    public void onConfirmButtonClick(ActionEvent actionEvent) {
        String messageToSend = "{\"command\": \"update\", \"word\": \" "+wordText.getText()+"\", \"meaning\": \""+partOfSpeechText.getText()+","+meaningText.getText()+"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            System.out.println("Received from server: " + response);
            Stage stage = (Stage) wordText.getScene().getWindow();
            stage.close();
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务

    }
}
