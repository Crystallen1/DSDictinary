package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class SearchpageView {
    public Label meaning;
    @FXML
    public TextArea textBox;

    public void onSearchButtonClick(ActionEvent actionEvent) {
        String messageToSend = "{\"command\": \"get\", \"word\": \" " + textBox.getText() + "\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            meaning.setText(response.getMessage());
            // 更新UI，显示来自服务器的响应
            System.out.println("Received from server: " + response);
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }
}
