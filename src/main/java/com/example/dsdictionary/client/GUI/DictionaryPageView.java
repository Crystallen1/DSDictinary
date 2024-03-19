package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.word;
import com.example.dsdictionary.protocol.Request;
import com.example.dsdictionary.protocol.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Objects;

public class DictionaryPageView implements WordAdder{
    public ListView<word> listView;
    @FXML
    public TextArea textBox;

    private final UpdatePageView updatePageView =new UpdatePageView();


    public void initialize() {
        listView.setCellFactory(new Callback<ListView<word>, ListCell<word>>() {
            @Override
            public ListCell<word> call(ListView<word> param) {
                return new ListCell<word>() {
                    @Override
                    protected void updateItem(word item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label label = new Label(item.getWord());
                            Button button1 = new Button("update");
                            Button button2 = new Button("delete");

                            button1.setOnAction(event-> updatePageView.openNewWindow(event,item));
                            button2.setOnAction(event ->deleteWord(event,item));

                            HBox hBox = new HBox(10, label, button1, button2);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });

        // 示例：加载数据到ListView
        listView.getItems().addAll(new word("Label 1","v","a"), new word("Label 2","a","b"), new word("Label 3","a","g"));
    }

    public void deleteWord(ActionEvent event, word word){
        String messageToSend = "{\"command\": \"remove\", \"word\": \" "+word.getWord()+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            // 更新UI，显示来自服务器的响应
            System.out.println("Received from server: " + response);
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }

    public void onSearchButtonClick(ActionEvent actionEvent) {
        String messageToSend = "{\"command\": \"get\", \"word\": \" "+textBox.getText()+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            // 更新UI，显示来自服务器的响应
            System.out.println("Received from server: " + response);
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }

    public void onAddNewWordButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewWordPage-view.fxml"));
            Parent root = loader.load();

            AddNewWordPageController addWordController = loader.getController();
            addWordController.setWordAdder(this);

            Stage newWindow = new Stage();
            newWindow.setTitle("Add New Word");
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setScene(new Scene(root));
            newWindow.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean addWord(String label, String partOfSpeech, String meaning) {
        try {
            // 添加单词的逻辑，这里简单地添加到ListView作为示例
            listView.getItems().addAll(new word(label, partOfSpeech, meaning));
            return true; // 成功
        } catch (Exception e) {
            // 处理任何异常
            e.printStackTrace();
            return false; // 失败
        }    }

}
