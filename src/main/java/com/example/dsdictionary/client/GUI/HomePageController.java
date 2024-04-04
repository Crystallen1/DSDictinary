package com.example.dsdictionary.client.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Objects;

public class HomePageController {
    public TextArea textBox;
    public TabPane tabPane;
    public Tab homeTab;
    public Tab settingsTab;
    @FXML
    private Label welcomeText;

    public void initialize() {
        try {
            Node homeView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("searchpage-view.fxml")));
            homeTab.setContent(homeView);

            Node settingsView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dictionaryPage-view.fxml")));
            settingsTab.setContent(settingsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onHelloButtonClick() {
        textBox.appendText("123");
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}