package com.sdm.sdmflash.fragmentYourWords;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.AppDatabase;
import com.sdm.sdmflash.databases.structure.Word;
import com.sdm.sdmflash.databases.structure.WordDao;
import com.sdm.sdmflash.fragmentAddWord.AddWordFragment;

import java.text.DateFormat;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener, SearchView.OnQueryTextListener, YourWordsRecyclerAdapter.OnNotTestedWordListener {


    private RecyclerView recyclerView;
    private TextView notTestedWordsTV;
    public YourWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_your_words, container, false);

        Toolbar toolbar = view.findViewById(R.id.your_words_toolbar);
        toolbar.setTitle("Your words");
        DrawerLayout drawer = ((MainActivity) getActivity()).drawer;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);


        // Instance of ViewModel
        YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);

        final DialogFragment fragment = new WordInfoDialog();
        fragment.setTargetFragment(this, 0);


        notTestedWordsTV = view.findViewById(R.id.your_words_recycler_header_not_tested_words_count_tv);
        //Setting up RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_your_words);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        final io.reactivex.Observer<Word> wordObserver = new io.reactivex.Observer<Word>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Word word) {
                Bundle bundle = new Bundle();

                DateFormat df = DateFormat.getDateInstance();
                bundle.putString("slovo", word.getWord());
                bundle.putString("preklad", word.getTranslation());
                bundle.putString("zdroj", word.getSource());
                bundle.putString("datum_pridani", df.format(word.getAdd_date()));
                if (word.getChange_date() == null) {
                    bundle.putString("datum_zmeny", "Never");
                } else {
                    bundle.putString("datum_zmeny", df.format(word.getChange_date()));
                }
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
        //Creating LiveData Observer
        final Observer<List<Word>> observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                if (recyclerView.getAdapter() != null) {
                    YourWordsRecyclerAdapter adapter = (YourWordsRecyclerAdapter) recyclerView.getAdapter();
                    adapter.setWordList(words);
                    adapter.notifyDataSetChanged();
                } else {
                    YourWordsRecyclerAdapter adapter = new YourWordsRecyclerAdapter(words, YourWordsFragment.this);
                    adapter.getPositionClicks().subscribe(wordObserver);
                    recyclerView.setAdapter(adapter);
                }
                TextView number = view.findViewById(R.id.your_words_recycler_header_words_count_tv);
                number.setText(String.valueOf(words.size()));
            }
        };

        model.getWords().observe(this, observer);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("debug", "ahoj");

        inflater.inflate(R.menu.your_words_toolbar, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.i("debug", s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
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
        manager.beginTransaction().replace(R.id.content_frame, addWordFragment).addToBackStack(null).commit();

    }

    @Override
    public void onNotTestedWord(int words) {
        notTestedWordsTV.setText(String.valueOf(words));
    }
}
