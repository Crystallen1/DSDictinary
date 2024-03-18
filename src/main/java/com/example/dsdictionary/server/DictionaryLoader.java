package com.example.dsdictionary.server;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryLoader {
    private Map<String, String> dictionary;

    public DictionaryLoader(String filePath) {
        this.dictionary = new ConcurrentHashMap<>();//保证线程安全
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
                        dictionary.put(word, partOfSpeech + ": " + definition);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary file: " + e.getMessage());
        }
    }

    public String getDefinition(String word) {
        return dictionary.getOrDefault(word, "Word not found");
    }

    // For testing and verification
    public void printAll() {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
