package com.sdm.sdmflash.fragmentYourWords;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;
import com.sdm.sdmflash.databases.structure.appDatabase.WordDao;
import com.sdm.sdmflash.fragmentAddWord.AddWordFragment;
import com.sdm.sdmflash.fragmentAddWord.AddWordFromText;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourWordsFragment extends Fragment implements WordInfoDialog.WordInfoDialogListener, SearchView.OnQueryTextListener, YourWordsRecyclerAdapter.AdapterToFragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ConstraintLayout flexibleSpaceLayout;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button ascBt;


    private boolean appBarCollapsed = false;
    private boolean blankList = false;
    private boolean asc = true;
    private String currentQuery = "";

    public YourWordsFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Spustí nové vlákno, které zjistí jesti není databáze prázdná
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {

                blankList = AppDatabase.getInstance(getContext()).wordDao().countWords() == 0;
            }
        });

        final View view = inflater.inflate(R.layout.fragment_your_words, container, false);


        // Zavolá metodu onCreateOptionsMenu aby upravila menu
        setHasOptionsMenu(true);


        // Vytváření toolbaru a drawer layout toggle

        toolbar = view.findViewById(R.id.your_words_toolbar);

        final Toolbar blankToolbar = view.findViewById(R.id.your_words_blank_toolbar);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.setSupportActionBar(toolbar);

        toolbar.setTitle("Your words");

        blankToolbar.setTitle("Your words");

        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        // Vytváření info dialog fragmentu
        final DialogFragment fragment = new WordInfoDialog();
        fragment.setTargetFragment(this, 0);


        // Vyrváření posluchače na zmáčknutí slova - užití RxJava
        final io.reactivex.Observer<Integer> wordObserver = new io.reactivex.Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(final Integer id) {

                //Vytvoří vlákno které najde v databázi informace o slově a otveře info dialog
                class OpenDialog extends AsyncTask<Context, Void, Bundle> {

                    private int id;

                    private OpenDialog(int id) {
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
                        bundle.putString("kartoteka", word.getFile().name());
                        bundle.putInt("ID", word.getId());

                        if (word.getChange_date() == null) {
                            bundle.putString("datum_zmeny", "Never");
                        } else {
                            bundle.putString("datum_zmeny", df.format(word.getChange_date()));

                        }
                        return bundle;
                    }

                    @Override
                    protected void onPostExecute(Bundle bundle) {

                        fragment.setArguments(bundle);
                        fragment.show(getFragmentManager(), "showDialog");

                    }
                }
                //Spustí vlákno
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


        //RecyclerHeader views
        final TextView number = view.findViewById(R.id.your_words_recycler_header_words_count_tv);
        final TextView notTestedWords = view.findViewById(R.id.your_words_recycler_header_not_tested_words_count_tv);
        ascBt = view.findViewById(R.id.your_words_recycler_header_asc_button);


        appBarLayout = view.findViewById(R.id.your_words_app_bar_layout);

        //Slouží k z jištění jestli je appbar collapse nebo ne (mohlo by se hodit)
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                appBarCollapsed = Math.abs(verticalOffset) + toolbar.getHeight() - appBarLayout.getTotalScrollRange() >= 0;
            }
        });

        flexibleSpaceLayout = view.findViewById(R.id.your_words_header);
        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbar);

        if (appBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }


        //Setting up RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_your_words);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        // Zajistí zobrazení jiného layoutu pokud je databáze prázdná
        final LinearLayout blankLayout = view.findViewById(R.id.your_words_blank_layout);

        if (blankList) {
            appBarLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            blankLayout.setVisibility(View.VISIBLE);

        } else {
            blankLayout.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        }

        //Vytváření LiveData Observer, zavolá se při změně dat v databázi
        final Observer<List<Word>> observer = new Observer<List<Word>>() {

            @Override
            public void onChanged(@Nullable List<Word> words) {
                //kdyby uživatel odstranil všechny slova, zobrazí se blank layout
                if (words.size() == 0) {

                    appBarLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    blankLayout.setVisibility(View.VISIBLE);
                }
                if (recyclerView.getAdapter() != null) {

                    YourWordsRecyclerAdapter adapter = (YourWordsRecyclerAdapter) recyclerView.getAdapter();

                    if (!asc) {
                        Collections.reverse(words);
                    }
                    adapter.setAdapterRowsAndRearange(words);

                    //pokud je zapnuto  vyhledávání, zobrazí se znovu vyhledané slova (například když je nějaké odstraněno)
                    if (adapter.isSearching()) {
                        adapter.setCurrentList(adapter.getAdapterRowsWithoutHeaders());
                        adapter.getFilter().filter(currentQuery);
                    } else {
                        adapter.setCurrentList(adapter.getAdapterRowsWithHeaders());
                    }
                    adapter.notifyDataSetChanged();
                } else {

                    //nastane při vytvoření fragmentu
                    YourWordsRecyclerAdapter adapter = new YourWordsRecyclerAdapter(words, getContext(), YourWordsFragment.this);

                    adapter.getPositionClicks().subscribe(wordObserver);

                    recyclerView.setAdapter(adapter);
                }
                number.setText(String.valueOf(words.size()));

                //Vytvoří nové vlákno které spočítá netestovaná slova a nastaví je TextView
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


        // Instance ViewModelu
        final YourWordsViewModel model = ViewModelProviders.of(this).get(YourWordsViewModel.class);
        model.getWordsByAlphabet().observe(this, observer);

        //Nastavení filtrování slov
        final Button popup = view.findViewById(R.id.your_words_recycler_header_filter_words_button);

        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.ThemeOverlay_AppCompat_Dark);

                PopupMenu menu = new PopupMenu(wrapper, popup);

                menu.getMenuInflater().inflate(R.menu.your_words_filter_popup, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {

                        setAsc(true);
                        model.getWords().removeObservers(YourWordsFragment.this);
                        ((YourWordsRecyclerAdapter) recyclerView.getAdapter()).setHeaderType(item.getItemId());

                        switch (item.getItemId()) {
                            case R.id.your_words_popup_item_alphabet:
                                model.getWordsByAlphabet().observe(YourWordsFragment.this, observer);
                                break;
                            case R.id.your_words_popup_item_difficulty:
                                model.getWordsByDifficulty().observe(YourWordsFragment.this, observer);
                                break;
                            case R.id.your_words_popup_item_tested:
                                model.getWordsByTest().observe(YourWordsFragment.this, observer);
                                break;
                            case R.id.your_words_popup_item_added:
                                model.getWordsByAdded().observe(YourWordsFragment.this, observer);
                                break;
                            case R.id.your_words_popup_item_source:
                                model.getWordsBySource().observe(YourWordsFragment.this, observer);
                                break;
                        }

                        return true;
                    }
                });

                menu.show();

            }
        });

        ascBt.setText("ASC");
        ascBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAsc(!asc);

                YourWordsRecyclerAdapter adapter = (YourWordsRecyclerAdapter) recyclerView.getAdapter();
                if (adapter.isSelectable()) {
                    setRecyclerSelectable(false);
                }
                List<Word> list = model.getWords().getValue();
                Collections.reverse(list);
                adapter.setAdapterRowsAndRearange(list);
                adapter.setCurrentList(adapter.getAdapterRowsWithHeaders());
                adapter.notifyDataSetChanged();
            }
        });

        Button addWord = view.findViewById(R.id.your_words_recycler_header_add_word_button);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddWordFromText.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.your_words_toolbar, menu);

        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(this);
        //nastane při kliknutí na ikonu lupy, zruší header, zakáže zajíždění toolbaru
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                flexibleSpaceLayout.setVisibility(View.GONE);

                AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                p.setScrollFlags(0);

                collapsingToolbarLayout.setLayoutParams(p);


                recyclerView.scrollToPosition(0);

                YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());

                if (adapter.isSelectable()) {
                    setRecyclerSelectable(false);
                }

                adapter.setSearching(true);
                adapter.setCurrentList(adapter.getAdapterRowsWithoutHeaders());
                adapter.notifyDataSetChanged();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {

                flexibleSpaceLayout.setVisibility(View.VISIBLE);

                AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);

                collapsingToolbarLayout.setLayoutParams(p);

                recyclerView.scrollToPosition(0);

                YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());

                adapter.setSearching(false);
                adapter.setCurrentList(adapter.getAdapterRowsWithHeaders());

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * Zavolá se při výběru tlačítka v nastavení.
     *
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());

        switch (item.getItemId()) {

            case R.id.your_words_toolbar_menu_select_items:

                setRecyclerSelectable(!adapter.isSelectable());

                break;

            case R.id.your_words_toolbar_menu_select_all_items:

                if (adapter.isSelectable()) {

                    if (adapter.isAllSelected()) {

                        adapter.setAllSelected(false);
                        updateToolbarButton(0);
                    } else {

                        adapter.setAllSelected(true);
                        updateToolbarButton(adapter.getAdapterRowsWithoutHeaders().size());
                    }
                } else {

                    setRecyclerSelectable(true);
                    adapter.setAllSelected(true);
                    updateToolbarButton(adapter.getAdapterRowsWithoutHeaders().size());
                }

                adapter.notifyDataSetChanged();
        }
        return true;
    }


    /**
     * Nastaví vše potřebné, aby bylo možné vybrat a odstranit několik slov najednou.
     *
     * @param selectable
     */
    @SuppressLint("SetTextI18n")
    public void setRecyclerSelectable(boolean selectable) {

        final YourWordsRecyclerAdapter adapter = ((YourWordsRecyclerAdapter) recyclerView.getAdapter());

        adapter.setSelectable(selectable);
        if (selectable) {


            //Nastavovíní tlačítka v toolbaru na odstranění vybraných slov
            final Button bt = new Button(new ContextThemeWrapper(getContext(), R.style.ThemeOverlay_AppCompat_Dark));

            bt.setText(getString(R.string.deleteBtInToolbar) + "0)");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
            bt.setLayoutParams(params);

            bt.setTag("button");

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    class DeleteWords extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {

                            WordDao dao = AppDatabase.getInstance(getContext()).wordDao();

                            if (adapter.isAllSelected()) {
                                dao.deleteAll();
                                Toast.makeText(getContext(), R.string.toast_your_words_deleted_all, Toast.LENGTH_SHORT).show();
                            } else {

                                List<Integer> toDelete = adapter.getSelectedItemsID();

                                for (int i = 0; i < toDelete.size(); i++) {
                                    dao.delete(dao.loadById(toDelete.get(i)));
                                }
                                Toast.makeText(getContext(), getString(R.string.toast_your_words_you_have_deleted) + String.valueOf(toDelete.size()) + getString(R.string.toast_your_words_words), Toast.LENGTH_SHORT).show();
                                adapter.getSelectedItemsID().clear();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {

                            setRecyclerSelectable(false);

                        }
                    }
                    new DeleteWords().execute();
                }
            });

            toolbar.addView(bt, 0);


        } else {
            adapter.setAllSelected(false);
            toolbar.removeViewAt(0);
        }


        adapter.notifyDataSetChanged();
    }

    public void setAsc(boolean asc) {
        ascBt.setText(asc ? "ASC" : "DESC");
        this.asc = asc;
    }


    /**
     * Metoda volána z adapteru zajistí, aby tlačítko v toolbaru
     * zobrazovalo číslo právě zvolených slov.
     *
     * @param number počet zvolených slov.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void updateToolbarButton(int number) {

        Button bt = toolbar.findViewWithTag("button");
        bt.setText(getString(R.string.deleteBtInToolbar) + number + ")");

    }

    //Zbytečné, protože se všecho vyřeší v onQueryTextChange
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    /**
     * Poslouchá změny ve vyhledávači a filtruje slova.
     *
     * @param s vyhledávané slovo.
     * @return vrací true.
     */
    @Override
    public boolean onQueryTextChange(String s) {

        //Zajistí, že se při odstranění slova zobrzí opět vyhledávané výsledky
        currentQuery = s;

        recyclerView.scrollToPosition(0);

        ((YourWordsRecyclerAdapter) recyclerView.getAdapter()).getFilter().filter(s);
        return true;

    }

    /**
     * Zavolá se při zmáčknutí tlačítka delete v dialogu.
     * Odstraní vybrané slovo z databáze.
     *
     * @param id id slova k odstranění.
     */
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

    /**
     * Zavolá se při zmáčknutí tlačítka edit v dialogu.
     * Spustí nový fragment AddWord a pošle mu jako argument id slova k předvyplnění
     * pro rychlejší úpravu.
     *
     * @param id id slova k úpravě.
     */
    @Override
    public void onDialogNegativeClick(int id) {

        AddWordFragment addWordFragment = new AddWordFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        addWordFragment.setArguments(bundle);

        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_frame, addWordFragment).addToBackStack(null).commit();

    }

    /**
     * Zabraňuje, aby aplikace spadla z důvodu rychlého mačknání dvou slov najednou.
     */
    @Override
    public void onDialogDismiss() {

        ((YourWordsRecyclerAdapter) recyclerView.getAdapter()).setCanOpenDialog(true);
    }
}
