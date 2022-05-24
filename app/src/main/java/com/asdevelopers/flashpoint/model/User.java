package com.asdevelopers.flashpoint.model;

import java.util.HashMap;

public class User {
    private String name;
    private String email;

    private HashMap<String, HashMap<String, String>> cards;

    public User(String name, String email) {
        this.name = name;
        this.email = email;

        this.cards = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean addDeck(String title) {
        if (this.cards.containsKey(title))
            return false;
        this.cards.put(title, new HashMap<>());
        return true;
    }

    public boolean addCard(String deckTitle, String front, String back) {
        HashMap<String, String> deck = this.cards.get(deckTitle);

        if (deck.containsKey(front))
            return false;

        deck.put(front, back);
        return true;
    }
}
