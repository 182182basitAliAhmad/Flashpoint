package com.asdevelopers.flashpoint.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asdevelopers.flashpoint.R;
//import com.asdevelopers.flashpoint.activity.DeckActivity;
import com.asdevelopers.flashpoint.activity.DeckActivity;
import com.asdevelopers.flashpoint.activity.MainActivity;
import com.asdevelopers.flashpoint.model.Database;
import com.asdevelopers.flashpoint.model.Deck;
import com.asdevelopers.flashpoint.model.FlashpointSQLiteHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private Database database;
    private ArrayList<Deck> decksToShow;


    public HomeRecyclerViewAdapter(Context context, Database dbObject) {
        this.context = context;
        database = dbObject;

        decksToShow = database.getAllDecks("");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deck deck = decksToShow.get(position);

        holder.getDeckTitleTextView().setText(deck.getTitle());
        holder.getCardCountTextView().setText("Total cards: " + deck.getNoOfCards());

        holder.setDeck(deck);
    }

    @Override
    public int getItemCount() {
        return decksToShow.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase(Locale.ROOT).trim();
            ArrayList<Deck> filteredDecks = database.getAllDecks(searchQuery);

            Log.v("Basit", filteredDecks.toString());

            FilterResults results = new FilterResults();
            results.values = filteredDecks;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            decksToShow = new ArrayList<>();
            decksToShow.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView getDeckTitleTextView() {
            return deckTitleTextView;
        }

        public TextView getCardCountTextView() {
            return cardCountTextView;
        }

        private final TextView deckTitleTextView;
        private final TextView cardCountTextView;
        private final FloatingActionButton editButton;
        private final FloatingActionButton deleteButton;

        private Deck deck;

        public void setDeck(Deck deck) {
            this.deck = deck;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.deckTitleTextView = itemView.findViewById(R.id.card_title);
            this.cardCountTextView = itemView.findViewById(R.id.card_count);

            this.editButton = itemView.findViewById(R.id.editFAB);
            this.deleteButton = itemView.findViewById(R.id.deleteFAB);

            this.editButton.setOnClickListener(view ->  {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);


                    alert.setTitle("Edit name of Deck");
                    alert.setMessage("Enter new name of Deck and press Update");

                    final EditText input = new EditText(context);

                    input.setSingleLine();
                    FrameLayout container = new FrameLayout(context);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(30, 0, 30, 0);
                    input.setLayoutParams(params);

                    input.setHint("Enter name of deck");
                    input.setText(deck.getTitle());

                    container.addView(input);
                    alert.setView(container);
                    alert.setPositiveButton("Update", (dialog, whichButton) -> {
                        database.editDeck(deck.getId(), input.getText().toString());
                        ((MainActivity) context).resetDisplayedData();
                        Toast.makeText(context, "Deck updated Successfully", Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("Cancel", (dialog, whichButton) -> {
                        // Canceled.
                    });

                    alert.show();
                });

            this.deleteButton.setOnClickListener(view -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to delete the deck?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            database.deleteDeck(deck.getId());
                            ((MainActivity) context).resetDisplayedData();
                            Toast.makeText(context, "Deck deleted Successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {

                        }).show();
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, DeckActivity.class);
            intent.putExtra("DeckID", this.deck.getId());
            context.startActivity(intent);
        }
    }
}
