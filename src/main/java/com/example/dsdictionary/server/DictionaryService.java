package com.example.dsdictionary.server;

import com.example.dsdictionary.models.Dictionary;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryService {
    private static final Logger logger = LogManager.getLogger(DictionaryService.class);

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
                logger.info("rollback: "+ e.getMessage());
                // 如果出错则回滚
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: " + ex.getMessage());
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                // 恢复自动提交
                logger.info("add success");
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Error setting auto commit: " + ex.getMessage());
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
                logger.info("SQL Error: " + e.getMessage());
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: " + ex.getMessage());
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                // 恢复自动提交
                logger.info("delete success");
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Error setting auto commit: " + ex.getMessage());
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
                    logger.error("Error loading meanings: " + e.getMessage());
                    System.out.println("Error loading meanings: " + e.getMessage());
                }
                dictionary.addWord(word);
            }
        } catch (SQLException e) {
            logger.error("Error loading dictionary: " + e.getMessage());
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    public void addOrUpdateMeaningInDB(String word, String partOfSpeech, String definition, String example) {
        PreparedStatement checkWordStmt = null;
        PreparedStatement insertMeaningStmt = null;
        ResultSet resultSet = null;

        try {
            // 获取数据库连接
            // 关闭自动提交，开始事务
            connection.setAutoCommit(false);

            // 检查单词是否存在
            checkWordStmt = connection.prepareStatement("SELECT id FROM words WHERE word = ?");
            checkWordStmt.setString(1, word);
            resultSet = checkWordStmt.executeQuery();

            if (!resultSet.next()) {
                logger.error("Error: The word '" + word + "' does not exist in the dictionary.");
                System.out.println("Error: The word '" + word + "' does not exist in the dictionary.");
                return;
            }

            int wordId = resultSet.getInt("id");

            // 插入新的意义
            insertMeaningStmt = connection.prepareStatement("INSERT INTO meanings (word_id, partOfSpeech, definition, example) VALUES (?, ?, ?, ?)");
            insertMeaningStmt.setInt(1, wordId);
            insertMeaningStmt.setString(2, partOfSpeech);
            insertMeaningStmt.setString(3, definition);
            insertMeaningStmt.setString(4, example);
            insertMeaningStmt.executeUpdate();

            // 操作成功，提交事务
            connection.commit();
        } catch (SQLException e) {
            try {
                // 出现异常，回滚事务
                connection.rollback();
                logger.info("SQL Error: "+e.getMessage());
            } catch (SQLException ex) {
                logger.error("Error Update Dictionary: "+ex.getMessage());
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (checkWordStmt != null) checkWordStmt.close();
                if (insertMeaningStmt != null) insertMeaningStmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true); // 恢复自动提交
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Meaning> getMeaning(String word) {
        if (dictionary.getWord(word)==Word.NOT_FOUND){
            return Collections.emptyList();
        }else {
            return dictionary.getWord(word).getMeanings();
        }
    }

    public void addWord(String word, List<Meaning> meaning) {
        dictionary.addWord(word,meaning);
        addWordToDatabase(word,meaning);
    }


    public void removeWord(String word) {
        dictionary.removeWord(word);
        deleteWordAndMeanings(word);
    }

    public boolean updateWord(String word, Meaning meaning) {
        Word searchWord = dictionary.getWord(word);
        for (Meaning existingMeaning : searchWord.getMeanings()) {
            if (existingMeaning.getDefinition().equals(meaning.getDefinition()) &&
                    existingMeaning.getPartOfSpeech().equals(meaning.getPartOfSpeech())) {
                // 意义已存在，更新不成功
                System.out.println("Info: The meaning already exists for the word '" + word + "'. No update needed.");
                return false;  // 返回 false 表示更新不成功
            }
        }

        // 添加或更新意义
        dictionary.addOrUpdateMeaning(word,  meaning.getPartOfSpeech(),meaning.getDefinition(), meaning.getExample());
        addOrUpdateMeaningInDB(word, meaning.getPartOfSpeech(), meaning.getDefinition(), meaning.getExample());
        return true;  // 返回 true 表示更新成功
    }



    // Additional methods as needed...
    public void printAll() {
        dictionary.printAll();
    }
}
