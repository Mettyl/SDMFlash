package com.sdm.sdmflash.YourWordsFragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.EnWord;
import com.sdm.sdmflash.db.structure.Word;
import com.sdm.sdmflash.db.structure.WordDao;
import com.sdm.sdmflash.menu.AddWordFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.sdm.sdmflash.MainActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener {


    public YourWordsFragment() {
        // Required empty public constructor
    }

    private ListView listView;

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

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
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
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getInstance(getContext());
//                List<EnWord> en =  database.enWordDao().getAll();
//                List<CzWord> cz = database.czWordDao().getAll();
//                List<EnCzJoin> joins = database.enCzJoinDao().getAll();
//
//                for (CzWord word : cz){
//                    Log.i(TAG, word.getWord());
//                }
//                for (EnWord word : en){
//                    Log.i(TAG, word.getWord());
//                }
//                for (EnCzJoin word : joins){
//                    Log.i(TAG, "Preklady: " + word.getEnWordId() + " + " + word.getCzWordId());
                //             }
                List<EnWord> slova = database.enCzJoinDao().translateToEn(database.czWordDao().findByWord("skok"));
                for (int i = 0; i < slova.size(); i++) {
                    Log.i(TAG, slova.get(i).getWord());
                }
            }
        });


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

    @Override
    public void onDialogNegativeClick(int id) {
        AddWordFragment addWordFragment = new AddWordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        addWordFragment.setArguments(bundle);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.main_wall, addWordFragment).addToBackStack(null).commit();

    }

}
