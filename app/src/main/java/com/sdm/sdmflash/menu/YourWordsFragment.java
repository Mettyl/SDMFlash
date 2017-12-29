package com.sdm.sdmflash.menu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_words, container, false);
        listView = (ListView)view.findViewById(R.id.word_list);

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> itemsAdapter = new ListAdapter(getContext(),
                        AppDatabase.getInstance(getContext()).wordDao().loadWordColumn(),
                        AppDatabase.getInstance(getContext()).wordDao().loadTranslationColumn());

                listView.setAdapter(itemsAdapter);
            }
        });

        return view;
    }

}
