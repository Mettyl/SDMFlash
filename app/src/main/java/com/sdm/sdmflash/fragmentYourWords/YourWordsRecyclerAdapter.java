package com.sdm.sdmflash.fragmentYourWords;

import android.support.v7.widget.RecyclerView;
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

    private final PublishSubject<Word> onClickSubject = PublishSubject.create();
    private final int LETTER = 0;
    private final int WORD = 1;
    private final int HEADER = 2;
    private List<Word> wordList;
    private OnNotTestedWordListener notTestedListener;

    public YourWordsRecyclerAdapter(List<Word> wordList, OnNotTestedWordListener listener) {
        this.notTestedListener = listener;
        this.wordList = rearangeList(wordList);
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = rearangeList(wordList);
    }

    public List<Word> rearangeList(List<Word> list) {
        List<Word> newList = new ArrayList<>();
        char lett = ' ';
        int notTested = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getChange_date() == null) {
                notTested++;
            }
            if (list.get(i).getWord().charAt(0) == lett) {
                newList.add(list.get(i));
            } else {
                newList.add(new Word(null, String.valueOf(list.get(i).getWord().charAt(0)).toUpperCase(), null, null, null, null, null));
                lett = list.get(i).getWord().charAt(0);
                i--;
            }
        }
        notTestedListener.onNotTestedWord(notTested);
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
        final Word word = wordList.get(position);
        switch (holder.getItemViewType()) {
            case WORD:
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
                LetterViewHolder letterViewHolder = (LetterViewHolder) holder;
                letterViewHolder.letter.setText(word.getWord());
                break;
        }
    }

    public Observable<Word> getPositionClicks() {
        return onClickSubject;
    }

    @Override
    public int getItemViewType(int position) {
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
        return wordList.size();
    }

    public interface OnNotTestedWordListener {
        void onNotTestedWord(int words);
    }

    private class WordViewHolder extends RecyclerView.ViewHolder {

        private TextView word, translation;

        private WordViewHolder(View itemView) {
            super(itemView);
            word = itemView.findViewById(R.id.recycler_adapter_word_your_words);
            translation = itemView.findViewById(R.id.recycler_adapter_translation_your_words);
        }
    }

    private class LetterViewHolder extends RecyclerView.ViewHolder {

        private TextView letter;

        private LetterViewHolder(View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.recycler_letter_text_view);
        }
    }



}
