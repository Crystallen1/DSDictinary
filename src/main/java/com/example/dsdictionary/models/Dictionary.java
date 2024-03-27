package com.example.dsdictionary.models;

import com.example.dsdictionary.server.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {

    private static final Logger logger = LogManager.getLogger(Dictionary.class);

    private final ConcurrentHashMap<String, Word> words = new ConcurrentHashMap<>();
    public void addWord(String word, String partOfSpeech, String definition, String example) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeaning(new Meaning(partOfSpeech, definition, example));
            logger.info("Added word: {} with partOfSpeech: {}, definition: {}, example: {}", word, partOfSpeech, definition, example);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    public void addWord(String word, Meaning meaning) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeaning(meaning);
            logger.info("Added word: {} with meaning: {}", word, meaning);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    public void addWord(String word, List<Meaning> meanings) {
        try {
            words.computeIfAbsent(word, k -> new Word(word)).addMeanings(meanings);
            logger.info("Added word: {} with meanings: {}", word, meanings);
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word, e.getMessage(), e);
        }
    }

    public void addWord(Word word) {
        try {
            words.computeIfAbsent(word.getWord(), k -> word);
            logger.info("Added word: {}", word.getWord());
        } catch (Exception e) {
            logger.error("Error adding word '{}': {}", word.getWord(), e.getMessage(), e);
        }
    }


    public Word getWord(String word) {
        return words.get(word);
    }

    public void removeWord(String word) {
        try {
            words.remove(word);
            logger.info("Removed word: {}", word);
        } catch (Exception e) {
            logger.error("Error removing word '{}': {}", word, e.getMessage(), e);
        }
    }

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


    public void addOrUpdateMeaning(String word, String partOfSpeech, String definition, String example) {
        Word existingWord = words.get(word);

        if (existingWord == null) {
            // 如果单词不存在，返回错误信息或抛出异常
            logger.error("Error: The word '" + word + "' does not exist in the dictionary.");
            System.out.println("Error: The word '" + word + "' does not exist in the dictionary.");
            return;  // 提前返回，不执行添加操作
        }

        for (Meaning meaning : existingWord.getMeanings()){
            if (meaning.getDefinition().equals(definition)){

            }
        }

        // 如果单词已存在，添加新的意义
        existingWord.addMeaning(new Meaning(partOfSpeech, definition, example));

        // 可选：更新映射（如果Word对象是可变的，这可能不是必需的）
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
