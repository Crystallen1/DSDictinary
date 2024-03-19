package com.example.dsdictionary.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryService {
    private final Map<String, String> dictionary = new ConcurrentHashMap<>();

    public DictionaryService(String filePath) {
        loadDictionary(filePath);
    }

    private void loadDictionary(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                // 分割单词、词性和定义
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    // 进一步分割词性和定义
                    String[] details = parts[1].split(",", 2);
                    if (details.length == 2) {
                        String partOfSpeech = details[0].trim();
                        String definition = details[1].trim();
                        dictionary.put(word, partOfSpeech + ", " + definition);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary file: " + e.getMessage());
        }
    }

    public String getMeaning(String word) {
        return dictionary.getOrDefault(word, "Word not found");
    }

    public void addWord(String word, String meaning) {
        dictionary.put(word, meaning);
    }

    public void removeWord(String word) {
        dictionary.remove(word);
    }

    public void updateWord(String word, String newMeaning) {
        dictionary.replace(word, newMeaning);
    }

    // Additional methods as needed...
    public void printAll() {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
