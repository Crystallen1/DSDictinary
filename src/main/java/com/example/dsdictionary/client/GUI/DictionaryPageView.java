package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.client.network.ClientTask;
import com.example.dsdictionary.models.Dictionary;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import com.example.dsdictionary.protocol.Request;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.google.gson.Gson;


import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.dsdictionary.client.GUI.HomePage.port;

public class DictionaryPageView implements WordAdder,UpdateCallBack{
    public ListView<Word> listView;
    @FXML
    public TextArea textBox;

    private final UpdatePageView updatePageView =new UpdatePageView(this);

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

                            button1.setOnAction(event->{updatePageView.openNewWindow(event,item);});
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

    public void updateList(){
        Word getWord = new Word("");
        Request request = new Request("init", getWord);

        Gson gson = new Gson();
        String messageToSend = gson.toJson(request);

        ClientTask clientTask = new ClientTask("localhost", port, messageToSend, response-> {
            System.out.println("Received from server: " + response);

            Type type = new TypeToken<ConcurrentHashMap<String, ConcurrentHashMap<String, Word>>>(){}.getType();
            ConcurrentHashMap<String, ConcurrentHashMap<String, Word>> deserializedMap = gson.fromJson(response.getMessage(), type);

            System.out.println("Deserialized map: " + deserializedMap);
            String wordKey;
            Word wordDetail;

            for (Map.Entry<String, ConcurrentHashMap<String, Word>> entry : deserializedMap.entrySet()) {
                wordKey = entry.getKey();
                ConcurrentHashMap<String, Word> innerMap = entry.getValue();

                for (Map.Entry<String, Word> entry2 : innerMap.entrySet()) {
                    String innerKey = entry2.getKey();
                    wordDetail = entry2.getValue();
                    listView.getItems().add(wordDetail);
                    String value = entry2.toString();
                    System.out.println(value);
                //listView.getItems().add();
            }}
        });
        new Thread(clientTask).start();
    }

    public void deleteWord(ActionEvent event, Word word){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        Word deletedWord = new Word(word.getWord());
        Request request = new Request("remove", word);
        Gson gson = new Gson();
        String messageToSend = gson.toJson(request);
        ClientTask clientTask = new ClientTask("localhost", port, messageToSend, response -> {
            System.out.println("Received from server: " + response);
            clearList();
            updateList();
        });
        new Thread(clientTask).start();
    }}

    public void onSearchButtonClick(ActionEvent actionEvent) {
        Word word = new Word(textBox.getText());
        Gson gson =new Gson();
        String messageToSend =gson.toJson(new Request("get",word));

        ClientTask clientTask = new ClientTask("localhost", port, messageToSend, response -> {
            if ("empty".equals(response.getStatus())){
                Alert alert = new Alert(Alert.AlertType.ERROR, "未查询到单词");
                alert.showAndWait();
            }else{
                String meaningsJson = response.getMessage();
                Type listType = new TypeToken<List<Meaning>>(){}.getType();
                List<Meaning> meanings = gson.fromJson(meaningsJson, listType);

                clearList();
                word.addMeanings(meanings);
                listView.getItems().add(word);
                //meaning.setText(response.getMessage());
                System.out.println("Received from server: " + response);
            }

        });
        new Thread(clientTask).start();
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
    public boolean addWord(String label, List<Meaning> meaning) {
        try {
            label= label.replace(" ","");
            Word word = new Word(label);
            word.addMeanings(meaning);
            Request request = new Request("add", word);

            Gson gson = new Gson();
            String messageToSend = gson.toJson(request);
            System.out.println(messageToSend);
            ClientTask clientTask = new ClientTask("localhost", port, messageToSend, response -> {
                System.out.println("Received from server: " + response);
                if ("failure".equals(response.getStatus())){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "添加重复单词");
                    alert.showAndWait();
                }else {
                    clearList();
                    updateList();
                }

            });
            new Thread(clientTask).start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onShowAllButtonClick(ActionEvent actionEvent) {
        clearList();
        updateList();
    }
}
