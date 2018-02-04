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
import com.sdm.sdmflash.YourWordsFragment.YourWordsListAdapter;
import com.sdm.sdmflash.YourWordsFragment.YourWordsViewModel;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;
import com.sdm.sdmflash.db.structure.WordDao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener {


    public YourWordsFragment() {
        // Required empty public constructor
    }

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instance of ViewModel
        YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);

        final DialogFragment fragment = new WordInfoDialog();
        fragment.setTargetFragment(this, 0);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_words, container, false);

        listView = view.findViewById(R.id.your_words_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Word word = (Word) listView.getItemAtPosition(i);

                Bundle bundle = new Bundle();

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                bundle.putString("slovo", word.getWord());
                bundle.putString("preklad", word.getTranslation());
                bundle.putString("zdroj", word.getSource());
                bundle.putString("datum_pridani", df.format(word.getAdd_date()));
                bundle.putString("datum_zmeny", df.format(word.getChange_date()));
                bundle.putInt("ID", word.getId());

                fragment.setArguments(bundle);
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


    @Override
    public void onDialogNeutralClick(final int id) {
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                WordDao dao = AppDatabase.getInstance(getContext()).wordDao();
                dao.delete(dao.loadById(id));
            }
        });
    }

}
