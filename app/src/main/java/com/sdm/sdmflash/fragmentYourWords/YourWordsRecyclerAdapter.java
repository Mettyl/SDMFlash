package com.sdm.sdmflash.fragmentYourWords;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.Word;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by mety on 3.2.18.
 */

public class YourWordsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Word> wordList;
    private final PublishSubject<Word> onClickSubject = PublishSubject.create();
    private final int LETTER = 0;
    private final int WORD = 1;

    public class WordViewHolder extends RecyclerView.ViewHolder {

        public TextView word, translation;

        public WordViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.recycler_adapter_word_your_words);
            translation = itemView.findViewById(R.id.recycler_adapter_translation_your_words);
        }
    }

    public class LetterViewHolder extends RecyclerView.ViewHolder {

        public TextView letter;

        public LetterViewHolder(View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.recycler_letter_text_view);
        }
    }

    public YourWordsRecyclerAdapter(List<Word> wordList) {
        this.wordList = rearangeList(wordList);
    }

    public List<Word> rearangeList(List<Word> list) {
        List<Word> newList = new ArrayList<>();
        char lett = ' ';
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWord().charAt(0) == lett) {
                newList.add(list.get(i));
            } else {
                newList.add(new Word(null, String.valueOf(list.get(i).getWord().charAt(0)).toUpperCase(), null, null, null, null, null));
                lett = list.get(i).getWord().charAt(0);
                i--;
            }
        }
        return newList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.i("debug", "create");
        switch (viewType) {
            case WORD:
                View viewW = inflater.inflate(R.layout.your_words_recycler_row, parent, false);
                WordViewHolder holderW = new WordViewHolder(viewW);
                return holderW;
            case LETTER:
                View viewL = inflater.inflate(R.layout.your_words_recycler_letter_row, parent, false);
                LetterViewHolder holderL = new LetterViewHolder(viewL);
                return holderL;
            default:
                View view = inflater.inflate(R.layout.your_words_recycler_row, parent, false);
                WordViewHolder holder = new WordViewHolder(view);
                return holder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Word word = wordList.get(position);
        switch (holder.getItemViewType()) {
            case WORD:
                Log.i("debug", "added word");
                WordViewHolder wordViewHolder = (WordViewHolder) holder;
                wordViewHolder.word.setText(word.getWord());
                wordViewHolder.translation.setText(word.getTranslation());
                wordViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickSubject.onNext(word);
                    }
                });
                break;
            case LETTER:
                Log.i("debug", "added letter");
                LetterViewHolder letterViewHolder = (LetterViewHolder) holder;
                letterViewHolder.letter.setText(word.getWord());
        }
    }

    public Observable<Word> getPositionClicks() {
        return onClickSubject;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("debug", "getType");
        int viewType = 0;
        if (wordList.get(position).getLanguage() != null) {
            viewType = WORD;
        } else {
            viewType = LETTER;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        Log.i("debug", "getCount");
        return wordList.size();
    }


}
