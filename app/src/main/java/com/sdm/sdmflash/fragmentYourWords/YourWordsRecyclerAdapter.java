package com.sdm.sdmflash.fragmentYourWords;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private int headerType;

    private List<AdapterRow> adapterRowsWithHeaders;
    private List<AdapterRow> adapterRowsWithoutHeaders;
    private List<AdapterRow> currentList;

    private List<Integer> selectedItemsID;

    private boolean canOpenDialog = true;
    private boolean selectable = false;
    private boolean allSelected = false;
    private boolean searching = false;

    private AdapterToFragment adapterToFragmentInterface;

    YourWordsRecyclerAdapter(List<Word> wordList, Context context, AdapterToFragment adapterToFragmentInterface) {

        headerType = R.id.your_words_popup_item_alphabet;

        this.context = context;
        this.adapterRowsWithHeaders = rearangeList(wordList, true);
        this.adapterRowsWithoutHeaders = rearangeList(wordList, false);
        this.adapterToFragmentInterface = adapterToFragmentInterface;

        selectedItemsID = new ArrayList<>();
        currentList = adapterRowsWithHeaders;
    }

    void setAdapterRowsAndRearange(List<Word> adapterRows) {
        this.adapterRowsWithHeaders = rearangeList(adapterRows, true);
        this.adapterRowsWithoutHeaders = rearangeList(adapterRows, false);
    }

    private List<AdapterRow> rearangeList(List<Word> list, boolean withHeaders) {

        List<AdapterRow> newList = new ArrayList<>();

        DateFormat fmt = DateFormat.getDateInstance();
        Resources res = context.getResources();
        char lett = ' ';
        Date date = new Date(0);
        String source = " ";
        int file = 0;
        boolean once = true;
        for (int i = 0; i < list.size(); i++) {
            switch (headerType) {
                case R.id.your_words_popup_item_alphabet:
                    if (!withHeaders || list.get(i).getWord().charAt(0) == lett) {

                        newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));
                    } else {
                        newList.add(new AdapterRow(String.valueOf(list.get(i).getWord().charAt(0)).toUpperCase()));
                        lett = list.get(i).getWord().charAt(0);
                        i--;
                    }
                    break;
                case R.id.your_words_popup_item_difficulty:

                    if (!withHeaders || list.get(i).getFile().getId() == file) {
                        Log.i("debug", "word added");
                        newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));

                    } else {
                        newList.add(new AdapterRow(res.getIdentifier(list.get(i).getFile().name(), "drawable", context.getPackageName())));
                        file = list.get(i).getFile().getId();
                        i--;
                    }
                    break;
                case R.id.your_words_popup_item_tested:

                    if (withHeaders && list.get(i).getChange_date() == null) {
                        if (once) {
                            once = false;
                            newList.add(new AdapterRow("Never"));
                            newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));
                            i--;
                        } else {
                            newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));
                        }
                    } else {
                        if (!withHeaders || fmt.format(list.get(i).getChange_date()).equals(fmt.format(date))) {
                            newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));
                        } else {
                            newList.add(new AdapterRow(fmt.format(list.get(i).getChange_date())));
                            date = list.get(i).getChange_date();
                            i--;
                        }
                    }
                    break;
                case R.id.your_words_popup_item_added:

                    if (!withHeaders || fmt.format(list.get(i).getAdd_date()).equals(fmt.format(date))) {
                        newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));
                    } else {
                        newList.add(new AdapterRow(fmt.format(list.get(i).getAdd_date())));
                        date = list.get(i).getAdd_date();
                        i--;
                    }
                    break;
                case R.id.your_words_popup_item_source:

                    if (!withHeaders || list.get(i).getSource().equals(source)) {
                        newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));

                    } else {
                        newList.add(new AdapterRow(String.valueOf(list.get(i).getSource().charAt(0)).toUpperCase() + list.get(i).getSource().substring(1)));
                        source = list.get(i).getSource();
                        i--;
                    }
                    break;
                default:
                    if (!withHeaders || list.get(i).getWord().charAt(0) == lett) {

                        newList.add(new AdapterRow(list.get(i).getWord(), list.get(i).getTranslation(), list.get(i).getId(), Type.WORD, false));

                    } else {
                        newList.add(new AdapterRow(String.valueOf(list.get(i).getWord().charAt(0)).toUpperCase()));
                        lett = list.get(i).getWord().charAt(0);
                        i--;
                    }
                    break;
            }
        }
        return newList;
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

        final AdapterRow adapterRow = currentList.get(position);

        switch (holder.getItemViewType()) {

            case WORD:

                final WordViewHolder wordViewHolder = (WordViewHolder) holder;

                wordViewHolder.word.setText(adapterRow.getWord());
                wordViewHolder.translation.setText(adapterRow.getTranslation());

                if (isSelectable()) {
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

                                if (selectedItemsID.size() == adapterRowsWithoutHeaders.size()) {
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
                        if (canOpenDialog) {
                            canOpenDialog = false;
                            onClickSubject.onNext(adapterRow.getId());
                        }
                    }
                });

                break;


            case LETTER:
                LetterViewHolder letterViewHolder = (LetterViewHolder) holder;
                if (adapterRow.hasImage()) {
                    letterViewHolder.letter.setVisibility(View.GONE);
                    letterViewHolder.imageView.setVisibility(View.VISIBLE);
                    letterViewHolder.imageView.setImageResource(adapterRow.getImageSource());
                } else {
                    letterViewHolder.imageView.setVisibility(View.GONE);
                    letterViewHolder.letter.setVisibility(View.VISIBLE);
                    letterViewHolder.letter.setText(adapterRow.getWord());
                }
                break;
        }
    }

    Observable<Integer> getPositionClicks() {
        return onClickSubject;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        if (currentList.get(position).getType() == Type.WORD) {
            viewType = Type.WORD.num;
        } else if (currentList.get(position).getType() == Type.LETTER) {
            viewType = Type.LETTER.num;
        }
        return viewType;
    }


    @Override
    public int getItemCount() {
        return currentList.size();
    }

    boolean isSelectable() {
        return selectable;
    }

    void setSelectable(boolean selectable) {
        this.selectable = selectable;
        selectedItemsID = new ArrayList<>();
        allSelected = false;
    }

    public boolean getCanOpenDialog() {
        return canOpenDialog;
    }

    public void setCanOpenDialog(boolean canOpenDialog) {
        this.canOpenDialog = canOpenDialog;
    }

    List<AdapterRow> getAdapterRowsWithoutHeaders() {
        return adapterRowsWithoutHeaders;
    }

    List<AdapterRow> getAdapterRowsWithHeaders() {
        return adapterRowsWithHeaders;
    }

    List<AdapterRow> getCurrentList() {
        return currentList;
    }

    public void setCurrentList(List<AdapterRow> currentList) {
        this.currentList = currentList;
    }


    boolean isAllSelected() {
        return allSelected;
    }

    void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
        if (allSelected) {
            for (AdapterRow r : currentList) {
                if (r.getType() == Type.WORD) {
                    selectedItemsID.add(r.getId());
                    r.setSelected(true);
                }
            }
        } else {
            selectedItemsID.clear();
            for (AdapterRow r : currentList) {
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

    int getHeaderType() {
        return headerType;
    }

    public void setHeaderType(int headerType) {
        this.headerType = headerType;
    }

    //Upravi list podle hledaneho slova
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                currentList = adapterRowsWithoutHeaders;
                if (!charString.isEmpty()) {
                    List<AdapterRow> filteredList = new ArrayList<>();
                    for (AdapterRow adapterRow : adapterRowsWithoutHeaders) {
                        if (adapterRow.getType() == Type.WORD) {
                            if (adapterRow.getWord().toLowerCase().contains(charString) || adapterRow.getTranslation().toLowerCase().contains(charString)) {
                                filteredList.add(adapterRow);
                            }
                        }
                    }
                    currentList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = currentList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                currentList = (ArrayList<AdapterRow>) filterResults.values;
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
        private ImageView imageView;

        private LetterViewHolder(View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.recycler_letter_text_view);
            imageView = itemView.findViewById(R.id.recycler_letter_iv);
        }
    }


    private class AdapterRow {

        private String word, translation;
        private int id, imageSource;
        private boolean selected, hasImage;
        private YourWordsRecyclerAdapter.Type type;

        public AdapterRow(int imageSource) {
            this.imageSource = imageSource;
            this.type = Type.LETTER;
            this.hasImage = true;
        }

        public AdapterRow(String headerText) {
            this.word = headerText;
            this.type = Type.LETTER;
            this.hasImage = false;
        }

        public AdapterRow(String word, String translation, int id, YourWordsRecyclerAdapter.Type type, boolean selected) {
            this.word = word;
            this.translation = translation;
            this.id = id;
            this.selected = selected;
            this.type = type;
            this.hasImage = false;
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

        public int getImageSource() {
            return imageSource;
        }

        public void setImageSource(int imageSource) {
            this.imageSource = imageSource;
        }

        public boolean hasImage() {
            return hasImage;
        }

        public void setHasImage(boolean hasImage) {
            this.hasImage = hasImage;
        }
    }


}
