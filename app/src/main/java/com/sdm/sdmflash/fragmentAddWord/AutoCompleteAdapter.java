package com.sdm.sdmflash.fragmentAddWord;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;
import com.sdm.sdmflash.databases.structure.dictionaryDatabase.DictionaryDatabase;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter {

    private List<AutoCompleteRow> dataList;
    private int itemLayout;

    private AutoCompleteAdapter.ListFilter listFilter = new AutoCompleteAdapter.ListFilter();

    public AutoCompleteAdapter(Context context, int resource, List<AutoCompleteRow> words) {
        super(context, resource, words);
        dataList = words;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position).getWord();
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView word = (TextView) view.findViewById(R.id.add_word_from_text_autocomplete_row_word);
        word.setText(getItem(position));

        ImageView flag = (ImageView) view.findViewById(R.id.add_word_from_text_autocomplete_row_image);
        if (dataList.get(position).getLanguage() == Language.CZ) {
            flag.setImageResource(R.drawable.cz_flag);
        } else {
            flag.setImageResource(R.drawable.gb_flag);
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<AutoCompleteRow>();
                    results.count = 0;
                }
            } else {
                final String search = prefix.toString().toLowerCase();

                List<AutoCompleteRow> autoCompleteRows = new ArrayList<>();
                List<String> words =  DictionaryDatabase.getInstance(getContext()).czWordDao().findWord(search + "%", 5);
                for (String s : words) {
                    autoCompleteRows.add(new AutoCompleteRow(s, Language.CZ));
                }
                words =  DictionaryDatabase.getInstance(getContext()).enWordDao().findWord(search + "%", 5);
                for (String s : words) {
                    autoCompleteRows.add(new AutoCompleteRow(s, Language.EN));
                }

                results.values = autoCompleteRows;
                results.count = autoCompleteRows.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.values != null) {
                dataList = (ArrayList<AutoCompleteRow>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

}
     class AutoCompleteRow{

        private String word;
        private Language language;

        public AutoCompleteRow(String word, Language language) {
            this.word = word;
            this.language = language;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }
    }
