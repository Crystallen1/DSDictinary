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
        words.compute(word, (k, existingWord) -> {
            if (existingWord == null) {
                existingWord = new Word(word);
            }
            existingWord.addMeaning(new Meaning(partOfSpeech, definition, example));
            return existingWord;
        });
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
