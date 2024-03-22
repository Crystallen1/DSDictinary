package com.example.dsdictionary.server;

import com.example.dsdictionary.models.Dictionary;
import com.example.dsdictionary.models.Meaning;
import com.example.dsdictionary.models.Word;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryService {
    private final Dictionary dictionary = new Dictionary();

    public Dictionary getDictionary() {
        return dictionary;
    }

    public DictionaryService(Connection connection) {
        loadDictionary(connection);
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
                        word.addMeaning(new Meaning(partOfSpeech, definition, example));
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
    }


    public void removeWord(String word) {
        dictionary.removeWord(word);
    }

    public void updateWord(String word, Meaning meaning) {
        dictionary.addOrUpdateMeaning(word, meaning.getDefinition(), meaning.getPartOfSpeech(), meaning.getExample());
    }

    // Additional methods as needed...
    public void printAll() {
        dictionary.printAll();
    }
}
