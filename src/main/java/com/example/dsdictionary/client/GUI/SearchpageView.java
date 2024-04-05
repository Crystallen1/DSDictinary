package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.List;

import static com.example.dsdictionary.client.GUI.HomePage.hostName;
import static com.example.dsdictionary.client.GUI.HomePage.port;

public class SearchpageView {
    @FXML
    public TextArea textBox;
    @FXML
    public Accordion meaningAccordion;

    public void onSearchButtonClick(ActionEvent actionEvent) {
        Word word = new Word(textBox.getText());
        Gson gson =new Gson();
        String messageToSend =gson.toJson(new Request("get",word));

        ClientTask clientTask = new ClientTask(hostName, port, messageToSend, response -> {
            if ("empty".equals(response.getStatus())){
                meaningAccordion.getPanes().clear();
                Alert alert = new Alert(Alert.AlertType.ERROR, "No word found!");
                alert.showAndWait();
            }else{
                String meaningsJson = response.getMessage();
                Type listType = new TypeToken<List<Meaning>>(){}.getType();
                List<Meaning> meanings = gson.fromJson(meaningsJson, listType);

                meaningAccordion.getPanes().clear();

                for (Meaning meaning : meanings) {
                    String title = meaning.getPartOfSpeech() + " - " + meaning.getDefinition();
                    String content = "Definition: " + meaning.getDefinition() + "\nExample: " + meaning.getExample();
                    Label contentLabel = new Label(content);
                    contentLabel.setWrapText(true);

                    TitledPane pane = new TitledPane(title, new VBox(contentLabel));
                    meaningAccordion.getPanes().add(pane);
                }
                //meaning.setText(response.getMessage());
                System.out.println("Received from server: " + response);
            }

        });
        new Thread(clientTask).start();
    }
}
