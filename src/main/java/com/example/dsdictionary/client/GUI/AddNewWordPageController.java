package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.models.Meaning;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to add it?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

        if (wordAdder != null) {
            String partOfSpeech = partOfSpeechText.getText();
            String meaning= meaningText.getText();
            String example = exampleText.getText();

            int partOfSpeechLineBreaks = countLineBreaks(partOfSpeech);
            int meaningLineBreaks = countLineBreaks(meaning);
            int exampleLineBreaks = countLineBreaks(example);
            List<Meaning> meaningList = new ArrayList<>();

            boolean success=false;

            if (partOfSpeechLineBreaks == meaningLineBreaks && partOfSpeechLineBreaks == exampleLineBreaks && !partOfSpeech.isEmpty()&&!meaning.isEmpty()&&!example.isEmpty()) {
                String[] partOfSpeechParts = partOfSpeech.split("\n");
                String[] meaningParts = meaning.split("\n");
                String[] exampleParts = example.split("\n");

                for (int i = 0; i <= partOfSpeechLineBreaks; i++) {
                    Meaning meaning1 = new Meaning(meaningParts[i],exampleParts[i],partOfSpeechParts[i] );
                    meaningList.add(meaning1);
                    String combined = partOfSpeechParts[i] + " " + meaningParts[i] + " " + exampleParts[i];
                    System.out.println(combined);
                }
                success = wordAdder.addWord(wordText.getText(),meaningList);
            } else if (partOfSpeech.isEmpty()||meaning.isEmpty()||example.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No meaning added!");
                alert.showAndWait();
                System.out.println("The number of line breaks is not consistent across the strings.");
            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR, "Not a complete meaning structure, you need to check the missing of part of speech or meaning or example!");
                alert.showAndWait();
                System.out.println("The number of line breaks is not consistent across the strings.");
            }

            if (success) {
                Stage stage = (Stage) wordText.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add word!");
                alert.showAndWait();
            }
        }
        }
    }
    private int countLineBreaks(String text) {
        return (int) text.chars().filter(ch -> ch == '\n').count();
    }
}
