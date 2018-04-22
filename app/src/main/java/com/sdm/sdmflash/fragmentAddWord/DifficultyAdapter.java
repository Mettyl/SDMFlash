package com.sdm.sdmflash.fragmentAddWord;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sdm.sdmflash.R;

public class DifficultyAdapter extends RecyclerView.Adapter<DifficultyAdapter.ImageViewHolder> {

    private Context context;
    private int selectedItem = 3;

    public DifficultyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_word_from_text_options_difficulty_row, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        Resources resources = context.getResources();
        holder.imageView.setImageResource(resources.getIdentifier("file" + (position + 1), "drawable", context.getPackageName()));
        if ((position + 1) == selectedItem) {
            Drawable drawable = resources.getDrawable(R.drawable.file_selected);
            holder.imageView.setBackground(drawable);
        } else {
            holder.imageView.setBackground(null);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(selectedItem - 1);
                selectedItem = holder.getAdapterPosition() + 1;
                notifyItemChanged(selectedItem - 1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ImageViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.add_word_aditional_options_difficulty_iv);
        }

    }
}
