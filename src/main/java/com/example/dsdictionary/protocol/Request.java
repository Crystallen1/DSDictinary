package com.example.dsdictionary.protocol;

import com.example.dsdictionary.models.Word;

public class Request {
    private String command;
    private Word word;

    // Constructors, getters, and setters

    public Request() {}

    public Request(String command, Word word) {
        this.command = command;
        this.word = word;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

}
