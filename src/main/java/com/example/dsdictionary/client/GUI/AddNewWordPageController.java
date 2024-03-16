package com.example.dsdictionary.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AddNewWordPageController {
    @FXML
    public TextArea wordText;
    @FXML
    public javafx.scene.control.Label Label;
    @FXML
    public TextArea partOfSpeechText;
    @FXML
    public TextArea meaningText;

    private WordAdder wordAdder;

    public void setWordAdder(WordAdder wordAdder) {
        this.wordAdder = wordAdder;
    }


    public void onConfirmButtonClick(ActionEvent actionEvent) {
        if (wordAdder != null) {
            boolean success = wordAdder.addWord(wordText.getText(), partOfSpeechText.getText(), meaningText.getText());
            if (success) {
                // 关闭窗口
                Stage stage = (Stage) wordText.getScene().getWindow();
                stage.close();
            } else {
                // 显示错误消息
                Alert alert = new Alert(Alert.AlertType.ERROR, "添加单词失败！");
                alert.showAndWait();
            }
        }
    }
}
