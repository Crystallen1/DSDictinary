package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.models.word;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Objects;

public class DictionaryPageView {
    public ListView<word> listView;
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
                            Button button1 = new Button("Button 1");
                            Button button2 = new Button("Button 2");
                            // 设置按钮事件处理器，例如 button1.setOnAction(...);

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

    public void onSearchButtonClick(ActionEvent actionEvent) {
    }

    public void onAddNewWordButtonClick(ActionEvent actionEvent) {
        try {
            // 加载a.fxml文件
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("addNewWordPage-view.fxml")));

            // 创建新的Stage实例，代表新的窗口
            Stage newWindow = new Stage();
            newWindow.setTitle("New Window Title");

            // 设置模态性，使新窗口阻塞其父窗口
            newWindow.initModality(Modality.APPLICATION_MODAL);

            // 设置新窗口的场景
            Scene scene = new Scene(root);
            newWindow.setScene(scene);

            // 显示窗口
            newWindow.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
