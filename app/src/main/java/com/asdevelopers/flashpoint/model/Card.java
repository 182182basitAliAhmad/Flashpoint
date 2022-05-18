package com.asdevelopers.flashpoint.model;

public class Card {
    private String Front;
    private String back;

    public Card(String question, String answer) {
        this.Front = question;
        this.back = answer;
    }

    public String getFront() {
        return Front;
    }

    public String getBack() {
        return back;
    }
}
