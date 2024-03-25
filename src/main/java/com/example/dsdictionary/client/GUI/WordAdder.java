package com.example.dsdictionary.client.GUI;

import com.example.dsdictionary.models.Meaning;

import java.util.List;

public interface WordAdder {
    boolean addWord(String label, List<Meaning> meaning);
}
