package com.example.dsdictionary.client.GUI;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class SearchpageView {
    public Label meaning;

    public void onSearchButtonClick(ActionEvent actionEvent) {
        meaning.setText("hello!");
    }
}
