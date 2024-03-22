package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.Word;
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
import com.google.gson.Gson;


import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryPageView implements WordAdder{
    public ListView<Word> listView;
    @FXML
    public TextArea textBox;

    private final UpdatePageView updatePageView =new UpdatePageView();


    public void initialize() {
        listView.setCellFactory(new Callback<ListView<Word>, ListCell<Word>>() {
            @Override
            public ListCell<Word> call(ListView<Word> param) {
                return new ListCell<Word>() {
                    @Override
                    protected void updateItem(Word item, boolean empty) {
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
        updateList();
    }

    public void clearList(){
        listView.getItems().clear();
    }

    public  void updateList(){
        String messageToSend = "{\"command\": \"init\", \"word\": \" "+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            // 更新UI，显示来自服务器的响应
            System.out.println("Received from server: " + response);
            Gson gson = new Gson();

            ConcurrentHashMap<String, String> deserializedMap = gson.fromJson(response.getMessage(), ConcurrentHashMap.class);
            System.out.println("Deserialized map: " + deserializedMap);
            String word;
            String POS="";
            String meaning="";
            for (Map.Entry<String,String> entry:deserializedMap.entrySet()){
                word=entry.getKey();
                String value = entry.getValue();
                String[] parts = value.split(",", 2);  // 分割成两部分
                if (parts.length == 2) {  // 确保value确实包含两部分
                    meaning = parts[0];
                    POS = parts[1];
                }
                //listView.getItems().add(new Word(word,POS,meaning));
            }
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }

    public void deleteWord(ActionEvent event, Word word){
        String messageToSend = "{\"command\": \"remove\", \"word\": \" "+word.getWord()+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            // 更新UI，显示来自服务器的响应
            System.out.println("Received from server: " + response);
            clearList();
            updateList();
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }

    public void onSearchButtonClick(ActionEvent actionEvent) {
        String messageToSend = "{\"command\": \"get\", \"word\": \" "+textBox.getText()+"\", \"meaning\": \"\"}";
        ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
            System.out.println("Received from server: " + response);
            String answer;
            answer=response.getMessage();
            if (Objects.equals(answer, "Word not found")){
                clearList();
            }else{
                clearList();
               // listView.getItems().add(new Word(textBox.getText(),"a","a"));
            }
        });
        new Thread(clientTask).start(); // 在新线程中运行客户端任务
    }

    public void onAddNewWordButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewWordPage-view.fxml"));
            Parent root = loader.load();

//            AddNewWordPageController addWordController = loader.getController();
//            addWordController.setWordAdder(this);

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
            label= label.replace(" ","");
            partOfSpeech=partOfSpeech.replace(" ","");
            meaning=meaning.replace(" ","");
            String messageToSend = "{\"command\": \"add\", \"word\": \" "+label+"\", \"meaning\": \""+partOfSpeech+meaning+"\"}";
            ClientTask clientTask = new ClientTask("localhost", 20016, messageToSend, response -> {
                // 更新UI，显示来自服务器的响应
                System.out.println("Received from server: " + response);
                clearList();
                updateList();
            });
            new Thread(clientTask).start(); // 在新线程中运行客户端任务
            return true; // 成功
        } catch (Exception e) {
            // 处理任何异常
            e.printStackTrace();
            return false; // 失败
        }
    }

    public void onShowAllButtonClick(ActionEvent actionEvent) {
        clearList();
        updateList();
    }
}
