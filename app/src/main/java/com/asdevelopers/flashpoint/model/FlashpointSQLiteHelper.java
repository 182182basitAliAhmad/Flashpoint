package com.asdevelopers.flashpoint.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FlashpointSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Flashpoint.db";

    public FlashpointSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String deckTable = "CREATE TABLE Deck (DeckID INTEGER PRIMARY KEY, " +
                "Title TEXT UNIQUE)";
        String cardTable = "CREATE TABLE Card (DeckID INTEGER NOT NULL, " +
                "Front TEXT, " +
                "Back Text, " +
                "FOREIGN KEY (DeckID) REFERENCES Deck(DeckID))";

        sqLiteDatabase.execSQL(deckTable);
        sqLiteDatabase.execSQL(cardTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Deck");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Card");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
