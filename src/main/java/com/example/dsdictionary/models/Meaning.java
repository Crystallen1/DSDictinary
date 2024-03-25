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


    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExample() {
        return example;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }
}
