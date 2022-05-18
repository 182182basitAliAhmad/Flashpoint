package com.asdevelopers.flashpoint.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class Database {
    private Context context;

    public Database(Context context) {
        this.context = context;
    }

    public void addDeck(String deckTitle) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("Title", deckTitle);

        sqLiteDatabase.insert("Deck", null, contentValues);

        dbHelper.close();
    }

    public void editDeck(int id, String newName) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Title", newName);

        sqLiteDatabase.update("Deck", contentValues, "DeckID = ?", new String[]{String.valueOf(id)});

        dbHelper.close();
    }

    public void deleteDeck(int id) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.delete("Deck", "DeckID = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.delete("Card", "DeckID = ?", new String[]{String.valueOf(id)});

        dbHelper.close();
    }

    public Deck getDeck(int id) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String query = "SELECT Deck.DeckID as ID, Deck.Title as Title, COUNT(Card.Front) as Count " +
                "FROM Deck LEFT JOIN Card ON Deck.DeckID=Card.DeckID " +
                "WHERE ID=" + id +
                " GROUP BY Deck.DeckID ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        int deckID = 0, count = 0;
        String title = "";

        if (cursor.moveToFirst()) {
            deckID = Integer.parseInt(cursor.getString(0));
            title = cursor.getString(1);
            count = Integer.parseInt(cursor.getString(2));
        }
        dbHelper.close();
        return new Deck(deckID, title, count);
    }

    public void addCard(int deckID, String front, String back) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("DeckID", deckID);
        contentValues.put("Front", front);
        contentValues.put("Back", back);

        sqLiteDatabase.insert("Card", null, contentValues);

        dbHelper.close();
    }

    public Card editCard(int deckID, Card card, String front, String back) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("Front", front);
        contentValues.put("Back", back);

        sqLiteDatabase.update("Card", contentValues, "DeckID = ? AND Front = ? AND Back = ?", new String[]{String.valueOf(deckID), card.getFront(), card.getBack()});

        dbHelper.close();

        return new Card(front, back);
    }

    public void deleteCard(int deckID, Card card) {
        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        sqLiteDatabase.delete("Card", "DeckID = ? AND Front = ? AND Back = ?", new String[]{String.valueOf(deckID), card.getFront(), card.getBack()});

        dbHelper.close();
    }

    public ArrayList<Deck> getAllDecks(String keyWord) {
        ArrayList<Deck> decks = new ArrayList<>();

        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        String query = "SELECT Deck.DeckID as ID, Deck.Title as Title, COUNT(Card.Front) as Count " +
                "FROM Deck LEFT JOIN Card ON Deck.DeckID=Card.DeckID";

        if (keyWord.length() != 0)
            query += " WHERE LOWER(Deck.Title) LIKE '%" + keyWord + "%'";

        query += " GROUP BY Deck.DeckID";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        int id, count;
        String title;

        while (cursor.moveToNext()) {
            id = Integer.parseInt(cursor.getString(0));
            title = cursor.getString(1);
            count = Integer.parseInt(cursor.getString(2));

            decks.add(new Deck(id, title, count));
        }
        dbHelper.close();
        return decks;
    }

    public ArrayList<Card> getAllCards(int deckID, String keyWord) {
        ArrayList<Card> cards = new ArrayList<>();

        FlashpointSQLiteHelper dbHelper = new FlashpointSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM Card WHERE DeckID=" + deckID;

        if (keyWord.length() != 0)
            query += " AND (Front LIKE '%" + keyWord + "%' OR Back LIKE '%" + keyWord + "%')";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        String f, b;

        while (cursor.moveToNext()) {
            f = cursor.getString(1);
            b = cursor.getString(2);

            cards.add(new Card(f, b));
        }
        dbHelper.close();
        return cards;
    }
}
