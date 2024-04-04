package com.example.dsdictionary.models;

import com.example.dsdictionary.server.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {

    private static final Logger logger = LogManager.getLogger(Dictionary.class);

    // ConcurrentHashMap to store words for thread-safe access
    private final ConcurrentHashMap<String, Word> words = new ConcurrentHashMap<>();
    // Adds a word with its meaning to the dictionary
    public void addWord(String word, String partOfSpeech, String definition, String example) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeaning(new Meaning(partOfSpeech, definition, example));
            logger.info("Added word: {} with partOfSpeech: {}, definition: {}, example: {}", word, partOfSpeech, definition, example);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    // Overloaded method to add a word with a Meaning object
    public void addWord(String word, Meaning meaning) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeaning(meaning);
            logger.info("Added word: {} with meaning: {}", word, meaning);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    // Overloaded method to add a word with a list of meanings
    public void addWord(String word, List<Meaning> meanings) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeanings(meanings);
            logger.info("Added word: {} with meanings: {}", word, meanings);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    // Overloaded method to add a Word object directly
    public void addWord(Word word) {
        try {
            words.computeIfAbsent(word.getWord(), k -> word);
            logger.info("Added word: {}", word.getWord());
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word.getWord(), e.getMessage(), e);
        }
    }


    // Retrieves a word from the dictionary; returns NOT_FOUND if the word does not exist
    public Word getWord(String word) {
        Word foundWord = words.get(word);
        if (foundWord == null) {
            // 如果没有找到单词，返回 Word.NOT_FOUND
            return Word.NOT_FOUND;
        } else {
            return foundWord;
        }
    }


    // Removes a word from the dictionary
    public void removeWord(String word) {
        try {
            words.remove(word);
            logger.info("Removed word: {}", word);
        } catch (Exception e) {
            logger.error("Error removing word '{}': {}", word, e.getMessage(), e);
        }
    }

    // Updates the meaning of an existing word
    public void updateWord(String word, String partOfSpeech, String definition, String example) {
        try {
            Word existingWord = getWord(word);
            if (existingWord != null) {
                existingWord.setMeanings(List.of(new Meaning(partOfSpeech, definition, example)));
                logger.info("Updated word: {} with new meaning: partOfSpeech={}, definition={}, example={}",
                        word, partOfSpeech, definition, example);
            } else {
                logger.warn("Attempted to update non-existing word: {}", word);
            }
        } catch (Exception e) {
            logger.error("Error updating word '{}': {}", word, e.getMessage(), e);
        }
    }


    // Adds meaning of an existing word
    public void addOrUpdateMeaning(String word, String partOfSpeech, String definition, String example) {
        Word existingWord = words.get(word);

//        if (existingWord == null) {
//            // Log an error if the word does not exist
//            logger.error("Error: The word '" + word + "' does not exist in the dictionary.");
//            System.out.println("Error: The word '" + word + "' does not exist in the dictionary.");
//            return;
//        }

        // Add the new meaning to the existing word
        existingWord.addMeaning(new Meaning(partOfSpeech, definition, example));
        words.put(word, existingWord);
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "words=" + words +
                '}';
    }


    public void printAll() {
        for (Map.Entry<String, Word> entry : words.entrySet()) {
            Word word = entry.getValue();
            System.out.println("Word: " + word.getWord());
            for (Meaning meaning : word.getMeanings()) {
                System.out.println("  Part of Speech: " + meaning.getPartOfSpeech());
                System.out.println("  Definition: " + meaning.getDefinition());
                System.out.println("  Example: " + meaning.getExample());
            }
        }
    }
}
