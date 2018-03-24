package com.sdm.sdmflash.fragmentYourWords;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by mety on 3.2.18.
 */

public class YourWordsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final Context context;

    private final PublishSubject<Integer> onClickSubject = PublishSubject.create();

    private final int LETTER = 0;
    private final int WORD = 1;

    private List<AdapterRow> adapterRows;
    private List<AdapterRow> adapterRowsCopy;
    private List<Integer> selectedItemsID;

    private boolean selectable = false;
    private boolean allSelected = false;
    private boolean searching = false;

    private AdapterToFragment adapterToFragmentInterface;

    YourWordsRecyclerAdapter(List<Word> wordList, Context context, AdapterToFragment adapterToFragmentInterface) {

        this.adapterRows = rearangeList(wordList, true);
        this.adapterRowsCopy = rearangeList(wordList, false);
        this.context = context;
        this.adapterToFragmentInterface = adapterToFragmentInterface;
        selectedItemsID = new ArrayList<>();
    }

    void setAdapterRowsAndRearange(List<Word> adapterRows) {
        this.adapterRows = rearangeList(adapterRows, !searching);
        this.adapterRowsCopy = rearangeList(adapterRows, false);
    }

    private List<AdapterRow> rearangeList(List<Word> list, boolean withLetters) {

        List<AdapterRow> newList = new ArrayList<>();

        char lett = ' ';
        for (int i = 0; i < list.size(); i++) {

            if (!withLetters || list.get(i).getWord().charAt(0) == lett) {
                newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));

            } else {
                newList.add(new AdapterRow(String.valueOf(list.get(i).getWord().charAt(0)).toUpperCase(), null, 0, Type.LETTER, false));
                lett = list.get(i).getWord().charAt(0);
                i--;

            }
        }
        return newList;
    }

    void addLetters() {

        List<AdapterRow> newList = new ArrayList<>();

        char lett = ' ';
        for (int i = 0; i < adapterRows.size(); i++) {

            if (adapterRows.get(i).getWord().charAt(0) == lett) {
                newList.add(new AdapterRow(adapterRows.get(i).getWord(), adapterRows.get(i).getTranslation(), adapterRows.get(i).getId(), Type.WORD, false));

            } else {
                newList.add(new AdapterRow(String.valueOf(adapterRows.get(i).getWord().charAt(0)).toUpperCase(), null, 0, Type.LETTER, false));
                lett = adapterRows.get(i).getWord().charAt(0);
                i--;

            }
        }
        adapterRows = newList;
    }

    void removeLetters() {
        adapterRows = adapterRowsCopy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WORD:
                View viewW = inflater.inflate(R.layout.your_words_recycler_row, parent, false);
                return new WordViewHolder(viewW);
            case LETTER:
                View viewL = inflater.inflate(R.layout.your_words_recycler_letter_row, parent, false);
                return new LetterViewHolder(viewL);
            default:
                View view = inflater.inflate(R.layout.your_words_recycler_row, parent, false);
                return new WordViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final AdapterRow adapterRow = adapterRows.get(position);

        switch (holder.getItemViewType()) {

            case WORD:

                final WordViewHolder wordViewHolder = (WordViewHolder) holder;

                wordViewHolder.word.setText(adapterRow.getWord());
                wordViewHolder.translation.setText(adapterRow.getTranslation());

                if (getSelectable()) {
                    wordViewHolder.checkBox.setVisibility(View.VISIBLE);
                    wordViewHolder.checkBox.setOnCheckedChangeListener(null);
                    wordViewHolder.checkBox.setChecked(adapterRow.isSelected());
                    wordViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            adapterRow.setSelected(b);

                            if (isAllSelected()) {
                                allSelected = false;
                            }
                            if (b) {
                                wordViewHolder.word.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                                wordViewHolder.translation.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

                                selectedItemsID.add(adapterRow.getId());
                                adapterToFragmentInterface.updateToolbarButton(selectedItemsID.size());

                                if (selectedItemsID.size() == adapterRowsCopy.size()) {
                                    allSelected = true;
                                }

                            } else {

                                wordViewHolder.word.setTextColor(ContextCompat.getColor(context, R.color.black));
                                wordViewHolder.translation.setTextColor(ContextCompat.getColor(context, R.color.black));

                                selectedItemsID.remove(selectedItemsID.indexOf(adapterRow.getId()));
                                adapterToFragmentInterface.updateToolbarButton(selectedItemsID.size());
                            }
                        }
                    });

                    if (adapterRow.isSelected()) {

                        wordViewHolder.word.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        wordViewHolder.translation.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

                    } else {

                        wordViewHolder.word.setTextColor(ContextCompat.getColor(context, R.color.black));
                        wordViewHolder.translation.setTextColor(ContextCompat.getColor(context, R.color.black));

                    }
                } else {

                    adapterRow.setSelected(false);
                    wordViewHolder.checkBox.setVisibility(View.INVISIBLE);
                    wordViewHolder.word.setTextColor(ContextCompat.getColor(context, R.color.black));
                    wordViewHolder.translation.setTextColor(ContextCompat.getColor(context, R.color.black));
                }

                wordViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickSubject.onNext(adapterRow.getId());
                    }
                });

                break;


            case LETTER:
                LetterViewHolder letterViewHolder = (LetterViewHolder) holder;
                letterViewHolder.letter.setText(adapterRow.getWord());
                break;
        }
    }

    Observable<Integer> getPositionClicks() {
        return onClickSubject;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (adapterRows.get(position).getType() == Type.WORD) {
            viewType = Type.WORD.num;
        } else {
            viewType = Type.LETTER.num;
        }
        return viewType;
    }


    //Getters
    @Override
    public int getItemCount() {
        return adapterRows.size();
    }

    boolean getSelectable() {
        return selectable;
    }

    //Setters
    void setSelectable(boolean selectable) {
        this.selectable = selectable;
        selectedItemsID = new ArrayList<>();
        allSelected = false;
    }

    boolean isAllSelected() {
        return allSelected;
    }

    void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
        if (allSelected) {
            for (AdapterRow r : adapterRows) {
                if (r.getType() == Type.WORD) {
                    selectedItemsID.add(r.getId());
                    r.setSelected(true);
                }
            }
        } else {
            selectedItemsID.clear();
            for (AdapterRow r : adapterRows) {
                if (r.getType() == Type.WORD) {
                    r.setSelected(false);
                }
            }
        }
    }

    List<Integer> getSelectedItemsID() {
        return selectedItemsID;
    }

    boolean isSearching() {
        return searching;
    }

    void setSearching(boolean searching) {
        this.searching = searching;
    }

    List<AdapterRow> getAdapterRowsCopy() {
        return adapterRowsCopy;
    }

    //Upravi list podle hledaneho slova
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                adapterRows = adapterRowsCopy;
                if (!charString.isEmpty()) {
                    List<AdapterRow> filteredList = new ArrayList<>();
                    for (AdapterRow adapterRow : adapterRows) {
                        if (adapterRow.getType() == Type.WORD) {
                            if (adapterRow.getWord().toLowerCase().contains(charString) || adapterRow.getTranslation().toLowerCase().contains(charString)) {
                                filteredList.add(adapterRow);
                            }
                        }
                    }
                    adapterRows = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = adapterRows;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                adapterRows = (ArrayList<AdapterRow>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public enum Type {
        LETTER(0), WORD(1);

        private byte num;

        Type(int num) {
            this.num = (byte) num;
        }
    }

    public interface AdapterToFragment {
        void updateToolbarButton(int number);
    }

    private class WordViewHolder extends RecyclerView.ViewHolder {

        private TextView word, translation;
        private CheckBox checkBox;
        private LinearLayout layout;

        private WordViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.recycler_adapter_word_your_words);
            translation = itemView.findViewById(R.id.recycler_adapter_translation_your_words);
            checkBox = itemView.findViewById(R.id.recycler_adapter_switch);
            layout = itemView.findViewById(R.id.recycler_adapter_linear_layout);
        }
    }

    private class LetterViewHolder extends RecyclerView.ViewHolder {

        private TextView letter;

        private LetterViewHolder(View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.recycler_letter_text_view);
        }
    }


    private class AdapterRow {

        private String word, translation;
        private int id;
        private boolean selected;
        private YourWordsRecyclerAdapter.Type type;

        public AdapterRow(String letter, YourWordsRecyclerAdapter.Type type) {
            this.word = letter;
            this.type = type;
        }

        public AdapterRow(String word, String translation, int id, YourWordsRecyclerAdapter.Type type, boolean selected) {
            this.word = word;
            this.translation = translation;
            this.id = id;
            this.selected = selected;
            this.type = type;
        }

        public String getWord() {
            return word;
        }

        public String getTranslation() {
            return translation;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }
    }


}
