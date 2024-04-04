package com.example.dsdictionary.models;

public class Meaning {
    private  String definition;
    private  String example;

    private  String partOfSpeech;

    @Override
    public String toString() {
        return "Meaning{" +
                "definition='" + definition + '\'' +
                ", example='" + example + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                '}';
    }

    public Meaning(String definition, String example, String partOfSpeech) {
        this.definition = definition;
        this.example = example;
        this.partOfSpeech = partOfSpeech;
    }


    public synchronized void setDefinition(String definition) {
        this.definition = definition;
    }

    public synchronized void setExample(String example) {
        this.example = example;
    }

    public synchronized void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public synchronized String getDefinition() {
        return definition;
    }

    public synchronized String getExample() {
        return example;
    }

    public synchronized String getPartOfSpeech() {
        return partOfSpeech;
    }
}
