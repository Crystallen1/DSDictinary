package com.example.dsdictionary.protocol;

public class Request {
    private String command;
    private String word;
    private String meaning;

    // Constructors, getters, and setters

    public Request() {}

    public Request(String command, String word, String meaning) {
        this.command = command;
        this.word = word;
        this.meaning = meaning;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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

    public void setDefinition(String definition) {
        this.meaning = definition;
    }
}
