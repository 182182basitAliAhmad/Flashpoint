package com.asdevelopers.flashpoint.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asdevelopers.flashpoint.R;
//import com.asdevelopers.flashpoint.adapter.DeckRecyclerViewAdapter;
import com.asdevelopers.flashpoint.adapter.DeckRecyclerViewAdapter;
import com.asdevelopers.flashpoint.model.Database;
import com.asdevelopers.flashpoint.model.Deck;

public class DeckActivity extends AppCompatActivity {

    private Database database;
    private Deck deck;

    private TextView deckTitleTextView;
    private TextView cardCountTextView;
    private RecyclerView recyclerView;
    private SearchView searchView;

    DeckRecyclerViewAdapter deckRecyclerViewAdapter;

    private int deckID;

    public void resetDisplayedData() {
        deck = database.getDeck(deckID);

        deckTitleTextView.setText(deck.getTitle());
        cardCountTextView.setText("Total Cards: " + deck.getNoOfCards());

        deckRecyclerViewAdapter = new DeckRecyclerViewAdapter(this, database, deckID);

        recyclerView.setAdapter(deckRecyclerViewAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                deckRecyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database(this);

        deckID = getIntent().getIntExtra("DeckID", 0);

        deckTitleTextView = findViewById(R.id.activity_title);
        cardCountTextView = findViewById(R.id.activity_subtitle);

        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search for a card");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        resetDisplayedData();

        findViewById(R.id.add_button).setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(DeckActivity.this);

            alert.setTitle("Add new Card");
            alert.setMessage("Enter front and back of card");

            final EditText frontText = new EditText(DeckActivity.this);
            final EditText backText = new EditText(DeckActivity.this);

            frontText.setSingleLine();
            backText.setSingleLine();

            LinearLayout linearLayout = new LinearLayout(DeckActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(30, 0, 30, 0);
            frontText.setLayoutParams(params);
            backText.setLayoutParams(params);

            frontText.setHint("Enter front side text");
            backText.setHint("Enter back size text");

            linearLayout.addView(frontText);
            linearLayout.addView(backText);

            alert.setView(linearLayout);
            alert.setPositiveButton("Add", (dialogInterface, i) -> {
                database.addCard(deck.getId(), frontText.getText().toString(), backText.getText().toString());
                Toast.makeText(DeckActivity.this, "Card created Successfully", Toast.LENGTH_SHORT).show();
                resetDisplayedData();
            }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                // Cancelled
            });
            alert.show();
        });


    }
}