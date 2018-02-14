package com.sdm.sdmflash.fragmentYourWords;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.AppDatabase;
import com.sdm.sdmflash.databases.structure.Word;
import com.sdm.sdmflash.databases.structure.WordDao;
import com.sdm.sdmflash.fragmentAddWord.AddWordFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener {


    public YourWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instance of ViewModel
        YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);

        final DialogFragment fragment = new WordInfoDialog();
        fragment.setTargetFragment(this, 0);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_your_words, container, false);

        //Setting up RecyclerView
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_your_words);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getContext(),
        //       R.drawable.item_decorator)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Creating LiveData Observer
        final Observer<List<Word>> observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                YourWordsRecyclerAdapter adapter = new YourWordsRecyclerAdapter(words);
                io.reactivex.Observer<Word> wordObserver = new io.reactivex.Observer<Word>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Word word) {
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

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                adapter.getPositionClicks().subscribe(wordObserver);
                recyclerView.setAdapter(adapter);
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
