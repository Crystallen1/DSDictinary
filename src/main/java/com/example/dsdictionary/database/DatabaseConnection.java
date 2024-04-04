package com.example.dsdictionary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }


    /**
     * Connect to SQL database
     */
    public static Connection connect(String dbFilePath) {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:" + dbFilePath;
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTables() {
        String sqlWords = "CREATE TABLE IF NOT EXISTS words (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL UNIQUE);";

        String sqlMeanings = "CREATE TABLE IF NOT EXISTS meanings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word_id INTEGER NOT NULL," +
                "partOfSpeech TEXT," +
                "definition TEXT," +
                "example TEXT," +
                "FOREIGN KEY(word_id) REFERENCES words(id));";

        try (Connection conn = connect("assets/dictionary.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlWords);
            stmt.execute(sqlMeanings);
            System.out.println("Tables created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

