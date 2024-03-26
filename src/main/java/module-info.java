module com.example.dsdictionary {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;
    requires java.sql;
    requires org.apache.logging.log4j;

    exports com.example.dsdictionary.client.GUI;
    opens com.example.dsdictionary.client.GUI to javafx.fxml;

    exports com.example.dsdictionary.models;
    opens com.example.dsdictionary.protocol to com.google.gson;

    opens com.example.dsdictionary.database to java.sql;

    opens com.example.dsdictionary.models to com.google.gson;

}