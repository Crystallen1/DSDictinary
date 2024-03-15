package com.example.dsdictionary.models;

public class word {
    private String word;

    private String meaning;

    private String partOfSpeech;

    public word(String word, String meaning, String partOfSpeech) {
        this.word = word;
        this.meaning = meaning;
        this.partOfSpeech = partOfSpeech;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }
}
