package com.example.dsdictionary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:assets/dictionary.db"; // 指定数据库文件的路径
    // 指定SQLite数据库文件的路径

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }
    }


    /**
     * 连接到SQLite数据库，如果数据库文件不存在则创建
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTables() {
        // SQL语句创建words表和meanings表
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

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // 创建表
            stmt.execute(sqlWords);
            stmt.execute(sqlMeanings);
            System.out.println("Tables created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

