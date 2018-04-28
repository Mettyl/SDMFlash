package com.sdm.sdmflash.fragmentAddWord;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdm.sdmflash.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Card> cards;
    private Context context;
    private OnCardClickListener clickListener;

    public interface OnCardClickListener {

        void onClick(int position);

    }

    public CardAdapter(Context context, OnCardClickListener listener) {
        this.context = context;
        clickListener = listener;

        cards = new ArrayList<>();

        Resources res = context.getResources();
        cards.add(new Card("Add by typing", "Write your word manualy", R.drawable.card_pen, res.getColor(R.color.red)));
        cards.add(new Card("Add by photo", "Take a photo of words", R.drawable.card_camera, res.getColor(R.color.blue)));
        cards.add(new Card("Import from picture", "Chose words from picture", R.drawable.card_image, res.getColor(R.color.orange)));
        cards.add(new Card("Import from XLSX", "Load words from excel file", R.drawable.card_excel, res.getColor(R.color.green)));
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_word_card, parent, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        Card card = cards.get(position);

        holder.cardView.setCardBackgroundColor(card.getColour());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(holder.getAdapterPosition());
            }
        });

        holder.title.setText(card.getTitle());
        holder.description.setText(card.getDescription());
        holder.imageView.setImageResource(card.getImagePath());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private ImageView imageView;
        private CardView cardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.add_word_card_title);
            description = itemView.findViewById(R.id.add_word_card_description);
            imageView = itemView.findViewById(R.id.add_word_card_image);
            cardView = itemView.findViewById(R.id.add_word_card_view);
        }
    }

    public class Card {

        private String title, description;
        private int imagePath, colour;

        public Card(String title, String description, int imagePath, int cardColour) {
            this.title = title;
            this.description = description;
            this.imagePath = imagePath;
            this.colour = cardColour;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getImagePath() {
            return imagePath;
        }

        public void setImagePath(int imagePath) {
            this.imagePath = imagePath;
        }

        public int getColour() {
            return colour;
        }

        public void setColour(int colour) {
            this.colour = colour;
        }
    }


}
