package com.asdevelopers.flashpoint.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.asdevelopers.flashpoint.R;
import com.asdevelopers.flashpoint.activity.DeckActivity;
import com.asdevelopers.flashpoint.activity.MainActivity;
import com.asdevelopers.flashpoint.model.Card;
import com.asdevelopers.flashpoint.model.Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class DeckRecyclerViewAdapter extends RecyclerView.Adapter<DeckRecyclerViewAdapter.ViewHolder> implements Filterable {

    private Context context;
    private Database database;
    private int deckID;
    private ArrayList<Card> cardsToShow;

    public DeckRecyclerViewAdapter(Context context, Database database, int deckID) {
        this.context = context;
        this.deckID = deckID;
        this.database = database;
        this.cardsToShow = database.getAllCards(deckID, "");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flipping_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = cardsToShow.get(position);

        holder.getFrontTextView().setText(card.getFront());
        holder.getBackTextView().setText(card.getBack());

        holder.setCard(card);
    }

    @Override
    public int getItemCount() {
        return cardsToShow.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase(Locale.ROOT).trim();
            ArrayList<Card> filteredCards = database.getAllCards(deckID, searchQuery);

            FilterResults results = new FilterResults();
            results.values = filteredCards;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            cardsToShow = new ArrayList<>();
            cardsToShow.addAll((ArrayList<Card>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Card card;

        private final TextView frontTextView;
        private final TextView backTextView;

        private final FloatingActionButton editButton;
        private final FloatingActionButton deleteButton;

        private boolean isFront;

        private final AnimatorSet frontAnimator;
        private final AnimatorSet backAnimator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frontTextView = itemView.findViewById(R.id.card_front_text_view);
            backTextView = itemView.findViewById(R.id.card_back_text_view);

            editButton = itemView.findViewById(R.id.editFAB);
            deleteButton = itemView.findViewById(R.id.deleteFAB);

            float scale = context.getResources().getDisplayMetrics().density;
            isFront = true;

            frontTextView.setCameraDistance(8000 * scale);
            backTextView.setCameraDistance(8000 * scale);

            frontAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.front_animation);

            backAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.back_animator);

            editButton.setOnClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                alert.setTitle("Edit Card");
                alert.setMessage("Enter new front and back of card");

                final EditText frontText = new EditText(context);
                final EditText backText = new EditText(context);

                frontText.setSingleLine();
                backText.setSingleLine();

                frontText.setText(card.getFront());
                backText.setText(card.getBack());

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.setMargins(30, 0, 30, 0);
                frontText.setLayoutParams(params);
                backText.setLayoutParams(params);

                frontText.setHint("Enter new front side text");
                backText.setHint("Enter new back size text");

                linearLayout.addView(frontText);
                linearLayout.addView(backText);

                alert.setView(linearLayout);
                alert.setPositiveButton("Update", (dialogInterface, i) -> {
                    card = database.editCard(deckID, card, frontText.getText().toString(), backText.getText().toString());
                    Toast.makeText(context, "Card updated Successfully", Toast.LENGTH_SHORT).show();
                    ((DeckActivity) context).resetDisplayedData();
                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                    // Cancelled
                });
                alert.show();
            });

            deleteButton.setOnClickListener(view -> {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to delete the card?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            database.deleteCard(deckID, card);
                            ((DeckActivity) context).resetDisplayedData();
                            Toast.makeText(context, "Card deleted Successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> {

                        }).show();
            });

            itemView.setOnClickListener(this);
        }

        public TextView getFrontTextView() {
            return frontTextView;
        }

        public TextView getBackTextView() {
            return backTextView;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        @Override
        public void onClick(View view) {
            if (isFront) {
                frontAnimator.setTarget(frontTextView);
                backAnimator.setTarget(backTextView);
                frontAnimator.start();
                backAnimator.start();
                isFront = false;
            } else {
                frontAnimator.setTarget(backTextView);
                backAnimator.setTarget(frontTextView);
                backAnimator.start();
                frontAnimator.start();
                isFront = true;
            }
        }
    }
}
