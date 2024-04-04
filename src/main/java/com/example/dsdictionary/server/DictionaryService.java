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

    // Instance of Dictionary for word management
    private final Dictionary dictionary = new Dictionary();

    // Database connection instance
    private final Connection connection;

    public Dictionary getDictionary() {
        return dictionary;
    }

    // Constructor that initializes the DictionaryService with a database connection
    public DictionaryService(Connection connection1) {
        connection = connection1;
        loadDictionary(connection);
    }

    public void addWordToDatabase(String word, List<Meaning> meanings) {
        String insertWordSql = "INSERT INTO words (word) VALUES (?);";
        String insertMeaningSql = "INSERT INTO meanings (word_id, partOfSpeech, definition, example) VALUES (?, ?, ?, ?);";

        try {
            connection.setAutoCommit(false);// Start a transaction

            try (PreparedStatement pstmt = connection.prepareStatement(insertWordSql)) {
                pstmt.setString(1, word); // Set the word in the SQL statement
                pstmt.executeUpdate(); // Execute the insert operation

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        long wordId = rs.getLong(1); // Get the ID of the newly inserted word

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

            connection.commit();// Commit the transaction
        } catch (SQLException e) {
            try {
                logger.info("rollback: "+ e.getMessage());
                connection.rollback();// Roll back the transaction in case of error
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: " + ex.getMessage());
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                logger.info("add success");
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Error setting auto commit: " + ex.getMessage());
                System.out.println("Error setting auto commit: " + ex.getMessage());
            }
        }
    }

    // Method to delete a word and its related meanings from the database
    public void deleteWordAndMeanings(String word) {
        String deleteMeaningsSql = "DELETE FROM meanings WHERE word_id = (SELECT id FROM words WHERE word = ?);";

        String deleteWordSql = "DELETE FROM words WHERE word = ?;";

        try {
            connection.setAutoCommit(false);

            // Prepare and execute the deletion of meanings related to the word
            try (PreparedStatement pstmtMeaning = connection.prepareStatement(deleteMeaningsSql)) {
                pstmtMeaning.setString(1, word);
                pstmtMeaning.executeUpdate();
            }

            // Prepare and execute the deletion of the word
            try (PreparedStatement pstmtWord = connection.prepareStatement(deleteWordSql)) {
                pstmtWord.setString(1, word);
                pstmtWord.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                logger.info("SQL Error: " + e.getMessage());
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction: " + ex.getMessage());
                System.out.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                logger.info("delete success");
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.error("Error setting auto commit: " + ex.getMessage());
                System.out.println("Error setting auto commit: " + ex.getMessage());
            }
        }
    }



    // Method to load the dictionary from the database
    public void loadDictionary(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet rsWords = statement.executeQuery("SELECT * FROM words");

            // Iterate over each word in the result set
            while (rsWords.next()) {
                String wordText = rsWords.getString("word");
                Word word = new Word(wordText);

                // Load meanings for the current word, using a new Statement to avoid conflicts
                try (Statement innerStatement = connection.createStatement()) {
                    ResultSet rsMeanings = innerStatement.executeQuery("SELECT * FROM meanings WHERE word_id = " + rsWords.getInt("id"));

                    // Iterate over each meaning in the result set
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
                // Add the full word to the dictionary
                dictionary.addWord(word);
            }
        } catch (SQLException e) {
            logger.error("Error loading dictionary: " + e.getMessage());
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    // Method to add or update a meaning in the database for a given word
    public void addOrUpdateMeaningInDB(String word, String partOfSpeech, String definition, String example) {
        PreparedStatement checkWordStmt = null;
        PreparedStatement insertMeaningStmt = null;
        ResultSet resultSet = null;

        try {
            // Disable auto-commit to start a transaction
            connection.setAutoCommit(false);

            // Check if the word exists in the database
            checkWordStmt = connection.prepareStatement("SELECT id FROM words WHERE word = ?");
            checkWordStmt.setString(1, word);
            resultSet = checkWordStmt.executeQuery();

            // If the word does not exist, log an error and exit the method
            if (!resultSet.next()) {
                logger.error("Error: The word '" + word + "' does not exist in the dictionary.");
                System.out.println("Error: The word '" + word + "' does not exist in the dictionary.");
                return;
            }

            int wordId = resultSet.getInt("id");

            // Insert the new meaning into the database
            insertMeaningStmt = connection.prepareStatement("INSERT INTO meanings (word_id, partOfSpeech, definition, example) VALUES (?, ?, ?, ?)");
            insertMeaningStmt.setInt(1, wordId);
            insertMeaningStmt.setString(2, partOfSpeech);
            insertMeaningStmt.setString(3, definition);
            insertMeaningStmt.setString(4, example);
            insertMeaningStmt.executeUpdate();

            // Commit the transaction if all operations were successful
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                logger.info("SQL Error: "+e.getMessage());
            } catch (SQLException ex) {
                logger.error("Error Update Dictionary: "+ex.getMessage());
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
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


    // Method to retrieve the meanings of a specified word
    public List<Meaning> getMeaning(String word) {
        // Check if the word exists in the dictionary
        if (dictionary.getWord(word)==Word.NOT_FOUND){
            // If the word is not found, return an empty list
            return Collections.emptyList();
        }else {
            return dictionary.getWord(word).getMeanings();
        }
    }

    // Method to add a word and its meanings to the dictionary and database
    public boolean addWord(String word, List<Meaning> meaning) {
        // Check if the word already exists in the dictionary
        if (dictionary.getWord(word)==Word.NOT_FOUND){
            dictionary.addWord(word,meaning);
            addWordToDatabase(word,meaning);
            return true;
        }else {
            // If the word already exists, return false and do not add it
            return false;
        }

    }


    // Method to remove a word and its meanings from the dictionary and database
    public boolean removeWord(String word) {
        // Check if the word exists in the dictionary
        if (dictionary.getWord(word)==Word.NOT_FOUND){
            // If the word is not found, return false indicating removal was unsuccessful
            return false;
        }else {
            dictionary.removeWord(word);
            deleteWordAndMeanings(word);
            return true;
        }

    }

    // Method to update the meaning of a word in the dictionary and database
    public boolean updateWord(String word, Meaning meaning) {
        // Retrieve the word from the dictionary
        Word searchWord = dictionary.getWord(word);

        // Check if the new meaning already exists for the word
        for (Meaning existingMeaning : searchWord.getMeanings()) {
            if (existingMeaning.getDefinition().equals(meaning.getDefinition()) &&
                    existingMeaning.getPartOfSpeech().equals(meaning.getPartOfSpeech())) {
                // If the meaning already exists, no update is needed
                System.out.println("Info: The meaning already exists for the word '" + word + "'. No update needed.");
                return false;
            }
        }

        // If the meaning does not exist, add the meaning in the dictionary
        dictionary.addOrUpdateMeaning(word,  meaning.getPartOfSpeech(),meaning.getDefinition(), meaning.getExample());
        addOrUpdateMeaningInDB(word, meaning.getPartOfSpeech(), meaning.getDefinition(), meaning.getExample());
        return true;
    }



    //test method
    public void printAll() {
        dictionary.printAll();
    }
}
