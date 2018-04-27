package com.sdm.sdmflash.fragmentAddWord;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdm.sdmflash.R;

import java.util.List;

public class TranslationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;

    private List<String> data;
    private Context context;
    private ClickListener clickListener;

    public TranslationAdapter(Context context, List<String> data, ClickListener clickListener) {
        this.context = context;
        this.data = data;
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_word_from_text_translation_row, parent, false);

        return new WordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String word = data.get(position);

        final WordHolder viewHolder = (WordHolder) holder;
        viewHolder.word.setText(word);

        if (selectedPos == position) {

            viewHolder.layout.getBackground().mutate().setColorFilter(new
                    PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY));
            viewHolder.word.setTextColor(Color.WHITE);

        } else {

            viewHolder.layout.getBackground().mutate().setColorFilter(new
                    PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
            viewHolder.word.setTextColor(Color.BLACK);

        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notifyItemChanged(selectedPos);
                selectedPos = viewHolder.getLayoutPosition();
                notifyItemChanged(selectedPos);
                clickListener.onItemclick(selectedPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void unselectAll() {
        int i = selectedPos;
        selectedPos = RecyclerView.NO_POSITION;
        notifyItemChanged(i);
    }

    public boolean isSelected() {
        return selectedPos != RecyclerView.NO_POSITION;
    }

    public String getSelectedTranslation() {
        return data.get(selectedPos);
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public interface ClickListener {

        void onItemclick(int id);
    }

    class WordHolder extends RecyclerView.ViewHolder {

        private TextView word;
        private LinearLayout layout;

        public WordHolder(View itemView) {
            super(itemView);
            this.word = itemView.findViewById(R.id.add_word_from_text_translation_tv);
            this.layout = itemView.findViewById(R.id.add_word_from_text_translation_layout);
        }

    }
}
