package com.example.dsdictionary.models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Word {
    private String word;

    private List<Meaning> meanings;

    public static final Word NOT_FOUND = null; // 表示未找到的特殊实例


    public Word(String word) {
        this.word = word;
        this.meanings = new CopyOnWriteArrayList<>();
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", meanings=" + meanings +
                '}';
    }

    public synchronized void addMeaning(Meaning meaning) {
        this.meanings.add(meaning);
    }
    public synchronized void addMeanings(List<Meaning> meaning) {
        this.meanings.addAll(meaning);
    }

    public String getWord() {
        return word;
    }

    public List<Meaning> getMeanings() {
        return new CopyOnWriteArrayList<>(meanings);
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }

}
