package com.sdm.sdmflash.YourWordsFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.db.structure.Word;

import java.util.List;

/**
 * Created by mety on 3.2.18.
 */

public class YourWordsListAdapter extends ArrayAdapter<Word> {


    public YourWordsListAdapter(@NonNull Context context, List<Word> words) {
        super(context, R.layout.your_words_list_row, words);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.your_words_list_row, parent, false);

        TextView word = customView.findViewById(R.id.list_adapter_word);
        TextView translation = customView.findViewById(R.id.list_adapter_translation);

        word.setText(getItem(position).getWord());
        translation.setText(getItem(position).getTranslation());

        return customView;
    }
}
