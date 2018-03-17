package com.sdm.sdmflash.fragmentYourWords;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener, SearchView.OnQueryTextListener, YourWordsRecyclerAdapter.AdapterToFragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private boolean blankList = false;
    private Button selectAllBt;
    private ConstraintLayout flexibleSpace;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String currentQuery = "";

    public YourWordsFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                blankList = AppDatabase.getInstance(getContext()).wordDao().countWords() == 0;
            }
        });
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_your_words, container, false);
        setHasOptionsMenu(true);

        appBarLayout = view.findViewById(R.id.your_words_app_bar_layout);
        final LinearLayout blankLayout = view.findViewById(R.id.your_words_blank_layout);
        recyclerView = view.findViewById(R.id.recycler_view_your_words);


        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        toolbar = view.findViewById(R.id.your_words_toolbar);
        final Toolbar blankToolbar = view.findViewById(R.id.your_words_blank_toolbar);

        activity.setSupportActionBar(toolbar);

        toolbar.setTitle("Your words");
        blankToolbar.setTitle("Your words");

        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        // Instance of ViewModel
        YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);

        final DialogFragment fragment = new WordInfoDialog();
        fragment.setTargetFragment(this, 0);

        if (blankList) {
            appBarLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

            blankLayout.setVisibility(View.VISIBLE);
        } else {
            blankLayout.setVisibility(View.GONE);

            appBarLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        //Setting up RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final io.reactivex.Observer<Integer> wordObserver = new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(final Integer id) {
                class OpenDialog extends AsyncTask<Context, Void, Bundle> {
                    int id;

                    public OpenDialog(int id) {
                        this.id = id;
                    }

                    @Override
                    protected Bundle doInBackground(Context... contexts) {
                        Word word = AppDatabase.getInstance(getContext()).wordDao().loadById(id);
                        DateFormat df = DateFormat.getDateInstance();
                        Bundle bundle = new Bundle();
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
                        return bundle;
                    }

                    @Override
                    protected void onPostExecute(Bundle bundle) {
                        fragment.setArguments(bundle);
                        fragment.show(getFragmentManager(), "showDialog");
                    }
                }
                new OpenDialog(id).execute(getContext());

            }

            @Override
            public void onError(Throwable e) {
                Log.i("debug", "Error: Rx java");
            }

            @Override
            public void onComplete() {
            }
        };
        final TextView blankText = view.findViewById(R.id.your_words_blank_text_view);
        final TextView number = view.findViewById(R.id.your_words_recycler_header_words_count_tv);
        final TextView notTestedWords = view.findViewById(R.id.your_words_recycler_header_not_tested_words_count_tv);
        selectAllBt = view.findViewById(R.id.your_words_recycler_header_select_all_button);
        flexibleSpace = view.findViewById(R.id.your_words_header);
        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbar);

        //Creating LiveData Observer
        final Observer<List<Word>> observer = new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                if (words.size() == 0) {
                    appBarLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    blankLayout.setVisibility(View.VISIBLE);
                }

                if (recyclerView.getAdapter() != null) {
                    YourWordsRecyclerAdapter adapter = (YourWordsRecyclerAdapter) recyclerView.getAdapter();
                    if (adapter.isSearching()) {
                        adapter.setAdapterRowsAndRearange(words);
                        adapter.getFilter().filter(currentQuery);
                    } else {
                        adapter.setAdapterRowsAndRearange(words);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    YourWordsRecyclerAdapter adapter = new YourWordsRecyclerAdapter(words, getContext(), YourWordsFragment.this);
                    adapter.getPositionClicks().subscribe(wordObserver);
                    recyclerView.setAdapter(adapter);
                }
                number.setText(String.valueOf(words.size()));

                class CountNotTestedWords extends AsyncTask<Context, Void, Integer> {

                    @Override
                    protected Integer doInBackground(Context... contexts) {
                        return AppDatabase.getInstance(contexts[0]).wordDao().notTestedWords();
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        notTestedWords.setText(String.valueOf(integer));
                    }
                }
                new CountNotTestedWords().execute(getContext());
            }
        };

        model.getWords().observe(this, observer);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.your_words_toolbar, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexibleSpace.setVisibility(View.GONE);
                AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                p.setScrollFlags(0);
                collapsingToolbarLayout.setLayoutParams(p);
                recyclerView.scrollToPosition(0);
                YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());
                if (adapter.getSelectable()) {
                    adapter.setSelectable(false);
                    toolbar.removeViewAt(0);
                    selectAllBt.setVisibility(View.GONE);
                }
                adapter.setSearching(true);
                adapter.removeLetters();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                flexibleSpace.setVisibility(View.VISIBLE);
                AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
                collapsingToolbarLayout.setLayoutParams(p);
                YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());
                adapter.setSearching(false);
                adapter.addLetters();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.your_words_toolbar_menu_select_items:
                final YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());
                adapter.setSelectable(!adapter.getSelectable());
                adapter.notifyDataSetChanged();

                if (adapter.getSelectable()) {
                    final Button bt = new Button(new ContextThemeWrapper(getContext(), R.style.ThemeOverlay_AppCompat_Dark));
                    bt.setText("Delete (0)");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.RIGHT;
                    bt.setLayoutParams(params);
                    bt.setTag("button");
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            class DeleteWords extends AsyncTask<Void, Void, Void> {

                                @Override
                                protected Void doInBackground(Void... voids) {

                                    WordDao dao = AppDatabase.getInstance(getContext()).wordDao();
                                    List<Integer> toDelete = adapter.getSelectedItemsID();
                                    for (int i = 0; i < toDelete.size(); i++) {
                                        dao.delete(dao.loadById(toDelete.get(i)));
                                    }
                                    adapter.getSelectedItemsID().clear();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    adapter.setSelectable(false);
                                    toolbar.removeViewAt(0);
                                    selectAllBt.setVisibility(View.GONE);
                                }
                            }
                            new DeleteWords().execute();

                        }
                    });
                    toolbar.addView(bt, 0);
                    selectAllBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!adapter.isAllSelected()) {
                                adapter.selectAllWords();
                            } else {
                                adapter.unselectAllWords();
                                Button bt = toolbar.findViewWithTag("button");
                                bt.setText("Delete (0)");
                            }
                        }
                    });
                    selectAllBt.setVisibility(View.VISIBLE);
                } else {
                    toolbar.removeViewAt(0);
                    selectAllBt.setVisibility(View.GONE);
                }
                break;

        }
        return true;
    }

    @Override
    public void updateToolbarButton(int number) {
        Button bt = toolbar.findViewWithTag("button");
        bt.setText("Delete (" + number + ")");
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        currentQuery = s;
        recyclerView.scrollToPosition(0);
        ((YourWordsRecyclerAdapter) recyclerView.getAdapter()).getFilter().filter(s);
        return true;
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


}
