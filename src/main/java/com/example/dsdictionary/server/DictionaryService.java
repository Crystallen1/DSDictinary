package com.example.dsdictionary.server;

import com.example.dsdictionary.models.Dictionary;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryService {
    private final Dictionary dictionary = new Dictionary();
    private final Connection connection;

    public Dictionary getDictionary() {
        return dictionary;
    }

    public DictionaryService(Connection connection1) {
        connection = connection1;
        loadDictionary(connection);
    }

    public void addWordToDatabase(String word, List<Meaning> meanings) {
        String insertWordSql = "INSERT INTO words (word) VALUES (?);";
        String insertMeaningSql = "INSERT INTO meanings (word_id, partOfSpeech, definition, example) VALUES (?, ?, ?, ?);";

        try {
            // 开启事务
            connection.setAutoCommit(false);

            // 插入单词并获取生成的主键（word_id）
            try (PreparedStatement pstmt = connection.prepareStatement(insertWordSql)) {
                pstmt.setString(1, word);
                pstmt.executeUpdate();

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        long wordId = rs.getLong(1);

                    // 对于每个意义，插入到 meanings 表
                    try (PreparedStatement pstmtMeaning = connection.prepareStatement(insertMeaningSql)) {
                        for (Meaning meaning : meanings) {
                            pstmtMeaning.setLong(1, wordId);
                            pstmtMeaning.setString(2, meaning.getPartOfSpeech());
                            pstmtMeaning.setString(3, meaning.getDefinition());
                            pstmtMeaning.setString(4, meaning.getExample());
                            pstmtMeaning.executeUpdate();
                        }
                    }
                }
            }}

            // 提交事务
            connection.commit();
        } catch (SQLException e) {
            try {
                // 如果出错则回滚
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                // 恢复自动提交
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error setting auto commit: " + ex.getMessage());
            }
        }
    }

    public void deleteWordAndMeanings(String word) {
        // SQL 语句用于删除 meanings 表中的条目
        String deleteMeaningsSql = "DELETE FROM meanings WHERE word_id = (SELECT id FROM words WHERE word = ?);";

        // SQL 语句用于删除 words 表中的条目
        String deleteWordSql = "DELETE FROM words WHERE word = ?;";

        try {
            // 开启事务
            connection.setAutoCommit(false);

            // 首先删除关联的意义
            try (PreparedStatement pstmtMeaning = connection.prepareStatement(deleteMeaningsSql)) {
                pstmtMeaning.setString(1, word);
                pstmtMeaning.executeUpdate();
            }

            // 然后删除单词本身
            try (PreparedStatement pstmtWord = connection.prepareStatement(deleteWordSql)) {
                pstmtWord.setString(1, word);
                pstmtWord.executeUpdate();
            }

            // 提交事务
            connection.commit();
        } catch (SQLException e) {
            try {
                // 如果出错则回滚
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                // 恢复自动提交
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Error setting auto commit: " + ex.getMessage());
            }
        }
    }



    // 加载数据库中的词典数据
    public void loadDictionary(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet rsWords = statement.executeQuery("SELECT * FROM words");

            while (rsWords.next()) {
                String wordText = rsWords.getString("word");
                Word word = new Word(wordText);

                // 为这个单词加载意义，使用新的Statement避免冲突
                try (Statement innerStatement = connection.createStatement()) {
                    ResultSet rsMeanings = innerStatement.executeQuery("SELECT * FROM meanings WHERE word_id = " + rsWords.getInt("id"));

                    while (rsMeanings.next()) {
                        String partOfSpeech = rsMeanings.getString("partOfSpeech");
                        String definition = rsMeanings.getString("definition");
                        String example = rsMeanings.getString("example");
                        word.addMeaning(new Meaning( definition, example,partOfSpeech));
                    }
                } catch (SQLException e) {
                    System.out.println("Error loading meanings: " + e.getMessage());
                }

                dictionary.addWord(word);
            }
        } catch (SQLException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    public List<Meaning> getMeaning(String word) {
        return dictionary.getWord(word).getMeanings();
    }

    public void addWord(String word, List<Meaning> meaning) {
        dictionary.addWord(word,meaning);
        addWordToDatabase(word,meaning);
    }


    public void removeWord(String word) {
        dictionary.removeWord(word);
        deleteWordAndMeanings(word);
    }

    public void updateWord(String word, Meaning meaning) {
        dictionary.addOrUpdateMeaning(word, meaning.getDefinition(), meaning.getPartOfSpeech(), meaning.getExample());
    }

    // Additional methods as needed...
    public void printAll() {
        dictionary.printAll();
    }
}
