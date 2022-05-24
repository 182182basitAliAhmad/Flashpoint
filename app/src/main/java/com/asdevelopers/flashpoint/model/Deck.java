package com.asdevelopers.flashpoint.model;

import java.util.ArrayList;

public class Deck {
    private int id;
    private String title;
    private int noOfCards;

    public Deck(int id, String title, int noOfCards) {
        this.id = id;
        this.title = title;
        this.noOfCards = noOfCards;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getNoOfCards() {
        return noOfCards;
    }
}
