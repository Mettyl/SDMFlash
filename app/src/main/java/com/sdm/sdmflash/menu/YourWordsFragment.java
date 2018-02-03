package com.sdm.sdmflash.menu;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.YourWordsFragment.WordInfoDialog;
import com.sdm.sdmflash.YourWordsFragment.YourWordsViewModel;
import com.sdm.sdmflash.db.structure.Word;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment {


    public YourWordsFragment() {
        // Required empty public constructor
    }

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instance of ViewModel
        YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_words, container, false);

        listView = view.findViewById(R.id.your_words_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Word word = (Word) listView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString("jedna", word.getWord());
                DialogFragment fragment = new WordInfoDialog();
                fragment.onCreate(bundle);
                fragment.show(getFragmentManager(), "showDialog");
            }
        });

        //Creating LiveData Observer
        final Observer<List<Word>> observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                listView.setAdapter(new YourWordsListAdapter(getContext(), words));
            }
        };

        model.getWords().observe(this, observer);


        return view;
    }

}
