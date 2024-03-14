module com.example.dsdictionary {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;

    exports com.example.dsdictionary.client.GUI;
    opens com.example.dsdictionary.client.GUI to javafx.fxml;
}