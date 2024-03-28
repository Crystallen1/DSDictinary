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

public class SearchpageView {
    @FXML
    public TextArea textBox;
    @FXML
    public Accordion meaningAccordion;

    public void onSearchButtonClick(ActionEvent actionEvent) {
        Word word = new Word(textBox.getText());
        Gson gson =new Gson();
        String messageToSend =gson.toJson(new Request("get",word));

        ClientTask clientTask = new ClientTask("localhost", 20017, messageToSend, response -> {
            if ("empty".equals(response.getStatus())){
                meaningAccordion.getPanes().clear();
                Alert alert = new Alert(Alert.AlertType.ERROR, "未查询到单词");
                alert.showAndWait();
            }else{
                String meaningsJson = response.getMessage(); // 直接获取 JSON 字符串
                Type listType = new TypeToken<List<Meaning>>(){}.getType();
                List<Meaning> meanings = gson.fromJson(meaningsJson, listType); // 直接解析为 List<Meaning>

                meaningAccordion.getPanes().clear();

                // 为每个含义创建一个 TitledPane
                for (Meaning meaning : meanings) {
                    String title = meaning.getPartOfSpeech() + " - " + meaning.getDefinition();
                    String content = "Definition: " + meaning.getDefinition() + "\nExample: " + meaning.getExample();
                    Label contentLabel = new Label(content);
                    contentLabel.setWrapText(true);  // 设置文本换行

                    TitledPane pane = new TitledPane(title, new VBox(contentLabel));
                    meaningAccordion.getPanes().add(pane);
                }
                //meaning.setText(response.getMessage());
                // 更新UI，显示来自服务器的响应
                System.out.println("Received from server: " + response);
            }

        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }
}
