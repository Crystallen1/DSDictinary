package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.models.Meaning;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddNewWordPageController {
    @FXML
    public TextArea wordText;
    @FXML
    public javafx.scene.control.Label Label;
    @FXML
    public TextArea partOfSpeechText;
    @FXML
    public TextArea meaningText;
    @FXML
    public TextArea exampleText;

    private WordAdder wordAdder;

    public void setWordAdder(WordAdder wordAdder) {
        this.wordAdder = wordAdder;
    }


    public void onConfirmButtonClick(ActionEvent actionEvent) {
        if (wordAdder != null) {
            String partOfSpeech = partOfSpeechText.getText();
            String meaning= meaningText.getText();
            String example = exampleText.getText();

            int partOfSpeechLineBreaks = countLineBreaks(partOfSpeech);
            int meaningLineBreaks = countLineBreaks(meaning);
            int exampleLineBreaks = countLineBreaks(example);
            List<Meaning> meaningList = new ArrayList<>();

            if (partOfSpeechLineBreaks == meaningLineBreaks && partOfSpeechLineBreaks == exampleLineBreaks) {
                // 如果数量相同，输出或记录数量
                String[] partOfSpeechParts = partOfSpeech.split("\n");
                String[] meaningParts = meaning.split("\n");
                String[] exampleParts = example.split("\n");

                // 循环遍历数组，并将相应的部分组合在一起
                for (int i = 0; i <= partOfSpeechLineBreaks; i++) {
                    Meaning meaning1 = new Meaning(meaningParts[i],exampleParts[i],partOfSpeechParts[i] );
                    meaningList.add(meaning1);
                    String combined = partOfSpeechParts[i] + " " + meaningParts[i] + " " + exampleParts[i];
                    System.out.println(combined);
                }
            } else {
                // 如果数量不同，处理不一致的情况
                // 显示错误消息
                Alert alert = new Alert(Alert.AlertType.ERROR, "The number of line breaks is not consistent across the strings.！");
                alert.showAndWait();
                System.out.println("The number of line breaks is not consistent across the strings.");
            }


            boolean success = wordAdder.addWord(wordText.getText(),meaningList);
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
    private int countLineBreaks(String text) {
        return (int) text.chars().filter(ch -> ch == '\n').count();
    }
}
