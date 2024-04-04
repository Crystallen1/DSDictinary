package com.example.dsdictionary.models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Word {
    private String word;

    private List<Meaning> meanings;

    // A static instance representing a word not found in the dictionary
    public static final Word NOT_FOUND = null;


    public Word(String word) {
        this.word = word;
        this.meanings = new CopyOnWriteArrayList<>();// Initializes the list of meanings as a thread-safe list
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", meanings=" + meanings +
                '}';
    }

    // Synchronized method to add a single meaning to the word
    public synchronized void addMeaning(Meaning meaning) {
        this.meanings.add(meaning);
    }
    // Synchronized method to add multiple meanings to the word
    public synchronized void addMeanings(List<Meaning> meaning) {
        this.meanings.addAll(meaning);
    }

    public String getWord() {
        return word;
    }

    // Getter for the meanings list, returning a thread-safe copy
    public List<Meaning> getMeanings() {
        return new CopyOnWriteArrayList<>(meanings);
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

}
