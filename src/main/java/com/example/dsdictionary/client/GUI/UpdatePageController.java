package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import com.example.dsdictionary.protocol.Request;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.dsdictionary.client.GUI.HomePage.port;

public class UpdatePageController {

    @FXML
    public Label label;
    @FXML
    public TextField partOfSpeechText;
    @FXML
    public TextField meaningText;
    @FXML
    public TextField exampleText;
    @FXML
    public TextArea existingMeaningsText;
    @FXML
    public TextArea wordText;
    @FXML
    public VBox mainContainer;

    private UpdateCallBack callback;

    public void setCallback(UpdateCallBack callback) {
        this.callback = callback;
    }

    public void onConfirmButtonClick(ActionEvent actionEvent) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        Word word = new Word(wordText.getText());
        Meaning meaning = new Meaning(meaningText.getText(),exampleText.getText(),partOfSpeechText.getText());
        word.addMeaning(meaning);

        Gson gson = new Gson();
        String messageToSend = gson.toJson(new Request("update",word));
        //String messageToSend = "{\"command\": \"update\", \"word\": \" "+wordText.getText()+"\", \"meaning\": \""+partOfSpeechText.getText()+","+meaningText.getText()+"\"}";
        ClientTask clientTask = new ClientTask("localhost", port, messageToSend, response -> {
            System.out.println("Received from server: " + response);
            if ("error".equals(response.getStatus())) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to update the word: " + response.getMessage());
                    alert.showAndWait();
                });
            } else {
                Platform.runLater(() -> {
                    Stage stage = (Stage) wordText.getScene().getWindow();
                    callback.clearList();
                    callback.updateList();
                    stage.close();
                });
            }
        });
        new Thread(clientTask).start();

    }}
    private int countLineBreaks(String text) {
        return (int) text.chars().filter(ch -> ch == '\n').count();
    }


    public void onAddMoreButtonClick(ActionEvent actionEvent) {
        HBox newMeaningBox = new HBox(10);

        Label partOfSpeechLabel = new Label("Part of speech");
        TextField partOfSpeechField = new TextField();
        partOfSpeechField.setPromptText("Enter part of speech");

        Label meaningLabel = new Label("Meaning");
        TextField meaningField = new TextField();
        meaningField.setPromptText("Enter meaning");

        Label exampleLabel = new Label("Example");
        TextField exampleField = new TextField();
        exampleField.setPromptText("Enter example");

        newMeaningBox.getChildren().addAll(partOfSpeechLabel, partOfSpeechField, meaningLabel, meaningField, exampleLabel, exampleField);

        mainContainer.getChildren().add(newMeaningBox);
    }
}
