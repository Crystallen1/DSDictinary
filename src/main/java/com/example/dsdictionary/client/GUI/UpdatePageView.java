package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdatePageView {

    @FXML
    public javafx.scene.control.Label Label;
    private UpdateCallBack callback;

    public UpdatePageView(UpdateCallBack callback) {
        this.callback = callback;
    }

    public void openNewWindow(ActionEvent event, Word word) {
        try {
            // 加载FXML文件
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UpdatePage-view.fxml"));
            Parent root = fxmlLoader.load();

            UpdatePageController controller = fxmlLoader.getController();
            controller.setCallback(callback);
            controller.wordText.setText(word.getWord());
            StringBuilder output = new StringBuilder();
            for (Meaning element : word.getMeanings()) {
                output.append(element.getPartOfSpeech());
                output.append(", ");
                output.append(element.getDefinition());
                output.append("\n");
                output.append("Example: ");
                output.append(element.getExample());
                output.append("\n");
            }
            System.out.println(output);
            controller.existingMeaningsText.setText(output.toString());


            // 创建新的窗口（Stage）
            Stage stage = new Stage();
            stage.setTitle("update");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
