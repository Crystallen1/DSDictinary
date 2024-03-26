package com.example.dsdictionary.models;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dictionary {
    private final ConcurrentHashMap<String, Word> words = new ConcurrentHashMap<>();

    public void addWord(String word,String partOfSpeech,  String definition, String example) {
        words.computeIfAbsent(word, k -> new Word(word)).addMeaning(new Meaning(partOfSpeech,definition, example));
    }
    public void addWord(String word,Meaning meaning) {
        words.computeIfAbsent(word, k -> new Word(word)).addMeaning(meaning);
    }

    public void addWord(String word,List<Meaning> meaning) {
        words.computeIfAbsent(word, k -> new Word(word)).addMeanings(meaning);
    }

    public void addWord(Word word){
        words.computeIfAbsent(word.getWord(),k->word);
    }

    public Word getWord(String word) {
        return words.get(word);
    }

    public void removeWord(String word){
        words.remove(word);
    }
    public void updateWord(String word, String partOfSpeech, String definition, String example) {
        Word existingWord = getWord(word);
        if (existingWord != null) {
            // Assuming Word class has a method to replace all meanings
            existingWord.setMeanings(List.of(new Meaning(partOfSpeech, definition, example)));
        }
    }

    public void addOrUpdateMeaning(String word, String partOfSpeech, String definition, String example) {
        Word existingWord = words.get(word);

        if (existingWord == null) {
            // 如果单词不存在，返回错误信息或抛出异常
            System.out.println("Error: The word '" + word + "' does not exist in the dictionary.");
            return;  // 提前返回，不执行添加操作
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
