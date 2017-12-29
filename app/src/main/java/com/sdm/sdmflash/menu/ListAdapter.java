package com.sdm.sdmflash.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdm.sdmflash.R;

/**
 * Created by Dominik on 29.12.2017.
 */

public class ListAdapter extends ArrayAdapter<String> {

    private String[] words;
    private String[] translations;

    public ListAdapter(@NonNull Context context, String[] words, String[] translations) {
        super(context, R.layout.word_list_item, words);
        this.words = words;
        this.translations = translations;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.word_list_item, parent, false);

        TextView word = (TextView)customView.findViewById(R.id.list_adapter_word);
        TextView translation = (TextView)customView.findViewById(R.id.list_adapter_translation);

        word.setText(words[position]);
        translation.setText(translations[position]);

        return customView;
    }
}
