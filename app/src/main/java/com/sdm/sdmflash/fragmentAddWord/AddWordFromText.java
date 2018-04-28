package com.sdm.sdmflash.fragmentAddWord;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.Source;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;
import com.sdm.sdmflash.databases.structure.dictionaryDatabase.DictionaryDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

public class AddWordFromText extends AppCompatActivity implements VerticalStepperForm, TranslationAdapter.ClickListener {

    private VerticalStepperFormLayout verticalStepperForm;
    private RecyclerView translationsRecycler;
    private RecyclerView difficultyRecycler;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView notFoundTV;
    private EditText enterTranslation;
    private final int maxTextLength = 50;
    private EditText descriptionET;
    private String addedWord = "";
    private String addedTranslation = "";
    private EditText tagET;
    private String addedDescription = "";
    private String addedTag = "";

    private Word intentWord;

    public AddWordFromText() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word_from_text);


        // Nastaveni toolbaru
        Toolbar toolbar = findViewById(R.id.toolbar_add_word_from_text);
        toolbar.setTitle(R.string.add_word_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Nasteveni nadpisu, podnadpisu a barev v stepperu
        String[] mySteps = {getString(R.string.add_word_word), getString(R.string.add_word_translation), getString(R.string.add_word_options)};
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getInt("your_words") != -1) {

                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        intentWord = AppDatabase.getInstance(getApplicationContext()).wordDao().loadById(getIntent().getExtras().getInt("your_words"));
                    }
                });

            }
        }
        // Sestaveni stepperu
        verticalStepperForm = findViewById(R.id.add_word_from_text_vertical_stepper);
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
//                .confirmationStepEnabled(true)
                .displayBottomNavigation(true)
                .materialDesignInDisabledSteps(true)
                .showVerticalLineWhenStepsAreCollapsed(true)
                .stepsSubtitles(new String[]{getResources().getString(R.string.add_word_unknown), getResources().getString(R.string.add_word_unknown), " "})
                .init();


    }


    /**
     * Metoda zinicalizuje vsechny layouty v steppru
     *
     * @param stepNumber
     * @return View
     */
    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;

        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        switch (stepNumber) {
            case 0:
                view = createWordStep(inflater);
                break;
            case 1:
                view = createTranslationStep(inflater);
                break;
            case 2:
                view = createOptionsStep(inflater);
                break;
        }
        return view;
    }

    /**
     * Inicializuje Word step
     *
     * @param inflater
     * @return View
     */
    private View createWordStep(LayoutInflater inflater) {

        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_word, null, false);

        // Vytvareni autocomplete textview
        autoCompleteTextView = layout.findViewById(R.id.add_word_from_text_autocomplete);

        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, R.layout.add_word_from_text_autocomplete_row, new ArrayList<AutoCompleteRow>());
        autoCompleteTextView.setAdapter(adapter);

        if (intentWord != null) {
            autoCompleteTextView.setText(intentWord.getWord());
            addedWord = intentWord.getWord();
        }
        // pri zmene textu zavola metodu checkWord
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().startsWith(" ")) {
                    autoCompleteTextView.setText("");

                } else {
                    addedWord = charSequence.toString().toLowerCase().replaceAll("\\s+$", "");

                    if (translationsRecycler.getAdapter().getItemCount() > 0) {

                        TranslationAdapter translationAdapter = ((TranslationAdapter) translationsRecycler.getAdapter());

                        if (translationAdapter.isSelected()) {

                            if (!enterTranslation.getText().toString().isEmpty()) {
                                addedTranslation = enterTranslation.getText().toString().toLowerCase().replaceAll("\\s+$", "");
                                verticalStepperForm.setStepSubtitle(1, addedTranslation);
                                enterTranslation.setTextColor(getResources().getColor(R.color.colorPrimary));
                            } else {
                                addedTranslation = "";
                                verticalStepperForm.setStepSubtitle(1, getString(R.string.add_word_unknown));
                                verticalStepperForm.setStepAsUncompleted(1, getResources().getString(R.string.add_word_choose_or_add));
                            }
                        }
                        translationAdapter.unselectAll();
                        translationAdapter.getData().clear();
                        translationAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        checkWord();
                    }
                }, 10);
            }
        });

        // pri kliknuti na polozku v dropdownu zacne hledat mozne preklady
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                dismisKeyboard();
                autoCompleteTextView.clearFocus();

            }
        });
        // pri zmacnuti enteru na klavesnici ji schova a uzavre dropdown napovedu
        autoCompleteTextView.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {

                            autoCompleteTextView.dismissDropDown();
                            autoCompleteTextView.clearFocus();
                            dismisKeyboard();
                        }

                        return true;
                    }
                });
        //pri ztrate focusu zacne hledat preklady
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!addedWord.isEmpty()) {
                        new FindTranslation().execute(addedWord);
                    }
                }
            }
        });


        // zabranuje pridani cisel a znaku mimo abecedu
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c, source)) {
                        sb.append(c);
                    } else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c, CharSequence s) {
                return (Character.isLetter(c) || Character.isSpaceChar(c));
            }

        };

        autoCompleteTextView.setFilters(new InputFilter[]{filter});


        final ImageView imageView = layout.findViewById(R.id.add_word_from_text_language_iv);

        // nastavi imageview vedle autocomplete textview s opacnou vlajkou nez je lokalni jazyk
        if (Locale.getDefault().getLanguage().equals("en")) {

            imageView.setImageResource(R.drawable.cz_flag);
            adapter.setSearchedLanguage(Language.CZ);
        } else {

            imageView.setImageResource(R.drawable.gb_flag);
            adapter.setSearchedLanguage(Language.EN);
        }

        if (intentWord != null) {
            imageView.setImageResource(intentWord.getLanguage() == Language.CZ ? R.drawable.cz_flag : R.drawable.gb_flag);
            adapter.setSearchedLanguage(intentWord.getLanguage());
        }

        //prohodi obrazek vlajky a informuje autocomplete adapter
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapter.getSearchedLanguage() == Language.EN) {

                    imageView.setImageResource(R.drawable.cz_flag);
                    adapter.setSearchedLanguage(Language.CZ);
                } else {

                    imageView.setImageResource(R.drawable.gb_flag);
                    adapter.setSearchedLanguage(Language.EN);
                }

                ((TranslationAdapter) translationsRecycler.getAdapter()).unselectAll();
                new FindTranslation().execute(addedWord);
            }
        });


        return layout;
    }


    private View createTranslationStep(LayoutInflater inflater) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_translation, null, false);

        translationsRecycler = layout.findViewById(R.id.add_word_translation_recycler_view);
        notFoundTV = layout.findViewById(R.id.add_word_translation_not_found_tv);
        enterTranslation = layout.findViewById(R.id.add_word_translation_et);
        enterTranslation.setHighlightColor(getResources().getColor(R.color.colorPrimary));

        // inicializace recycleru pro zobrazeni dostupnych prekladu
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        translationsRecycler.setLayoutManager(layoutManager);
        translationsRecycler.setItemAnimator(new DefaultItemAnimator());
        translationsRecycler.setAdapter(new TranslationAdapter(getApplicationContext(), new ArrayList<String>(), this));

        if (intentWord != null) {
            enterTranslation.setText(intentWord.getTranslation());
            addedTranslation = intentWord.getTranslation();
            enterTranslation.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        // pri kliknuti na text view se unselectne item z recycleru, aby bylo jasne ze uzivatel zadava svuj vlastni preklad
        enterTranslation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    ((TranslationAdapter) translationsRecycler.getAdapter()).unselectAll();
                    addedTranslation = enterTranslation.getText().toString().toLowerCase().replaceAll("\\s+$", "");
                    enterTranslation.setTextColor(getResources().getColor(R.color.colorPrimary));
                    checkTrranslation();
                }
            }
        });


        enterTranslation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (verticalStepperForm.getActiveStepNumber() == 1) {

                    if (charSequence.toString().startsWith(" ")) {
                        enterTranslation.setText("");

                    } else {
                        addedTranslation = charSequence.toString().toLowerCase().replaceAll("\\s+$", "");
                        checkTrranslation();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        enterTranslation.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            enterTranslation.clearFocus();
                            dismisKeyboard();
                        }

                        return true;
                    }
                });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c, source)) {
                        sb.append(c);
                    } else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c, CharSequence s) {
                return (Character.isLetter(c) || Character.isSpaceChar(c));
            }

        };

        enterTranslation.setFilters(new InputFilter[]{filter});

        if (intentWord != null) {
            new FindTranslation().execute(addedWord);
        }


        return layout;
    }

    private View createOptionsStep(LayoutInflater inflater) {

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.add_word_from_text_step_options, null, false);

        descriptionET = layout.findViewById(R.id.add_word_aditional_options_description_et);
        tagET = layout.findViewById(R.id.add_word_aditional_options_tag_et);

        if (intentWord != null) {
            descriptionET.setText(intentWord.getDescription());
            tagET.setText(intentWord.getSource());
        }

        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().startsWith(" ")) {
                    descriptionET.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tagET.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            tagET.clearFocus();
                            dismisKeyboard();
                        }

                        return true;
                    }
                });

        tagET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().startsWith(" ")) {
                    tagET.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        difficultyRecycler = layout.findViewById(R.id.add_word_aditional_options_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        difficultyRecycler.setLayoutManager(layoutManager);
        difficultyRecycler.setItemAnimator(new DefaultItemAnimator());
        difficultyRecycler.setHasFixedSize(true);
        difficultyRecycler.setAdapter(new DifficultyAdapter(getApplicationContext()));


        if (intentWord != null) {
            ((DifficultyAdapter) difficultyRecycler.getAdapter()).setSelectedItem(intentWord.getFile().getId());
        }

        return layout;
    }

    /**
     * Zavola se kdykoliv je otevren novy step
     *
     * @param stepNumber
     */
    @Override
    public void onStepOpening(int stepNumber) {

        switch (stepNumber) {

            case 0:
                //zkontroluje, jestli zadane slovo vyhovuje podminkam, pokud ano, zpristupni tlacitko continue
                checkWord();
                //
                if (verticalStepperForm.isStepCompleted(1)) {
                    verticalStepperForm.setStepSubtitle(1, addedTranslation);
                }
                break;
            case 1:
                //zabranuje pokracovat dokud se nevybere nebo nenapise preklad + nastavi popisek na zadany preklad
                verticalStepperForm.setStepAsUncompleted(1, "");
                verticalStepperForm.setStepSubtitle(0, addedWord);

                //zkontroluje, jestli zadane slovo vyhovuje podminkam, pokud ano, zpristupni tlacitko continue
                checkTrranslation();
                break;
            case 2:
                verticalStepperForm.setStepAsCompleted(2);
                verticalStepperForm.setStepSubtitle(1, addedTranslation);
                break;
        }

    }

    // zkontroluje jestli zadane slovo vyhovuje pozadavkum
    public void checkWord() {

        if (addedWord.length() > 50) {
            verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.add_word_too_long));

        } else if (addedWord.isEmpty()) {
            verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.add_word_add_some));

        } else {
            verticalStepperForm.setActiveStepAsCompleted();
        }
    }

    // zkontroluje jestli zadany preklad vyhovuje pozadavkum
    public void checkTrranslation() {

        TranslationAdapter adapter = (TranslationAdapter) translationsRecycler.getAdapter();

        if (!adapter.isSelected() && !enterTranslation.getText().toString().isEmpty() && addedTranslation.length() > 50) {

            verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.add_word_translation_long));

        } else if (!enterTranslation.getText().toString().isEmpty() || adapter.isSelected()) {
            // kontroluje zda zadane slovni spojeni uz existuje v databazi, jestli ano, zakaze pridani
            new WordsExist().execute(addedTranslation);

        } else {
            verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.add_word_choose_or_add));
        }

    }


    @Override
    public void sendData() {
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase database = AppDatabase.getInstance(getApplicationContext());

                if (intentWord != null) {
                    database.wordDao().delete(intentWord);
                }

                if (Locale.getDefault().getLanguage().equals("en") && ((AutoCompleteAdapter) autoCompleteTextView.getAdapter()).getSearchedLanguage() == Language.EN) {
                    String s = addedWord;
                    addedWord = addedTranslation;
                    addedTranslation = s;
                } else if (((AutoCompleteAdapter) autoCompleteTextView.getAdapter()).getSearchedLanguage() == Language.CZ) {
                    String s = addedWord;
                    addedWord = addedTranslation;
                    addedTranslation = s;

                }
                String tag = tagET.getText().toString().replaceAll("\\s+$", "");

                if (database.sourceDao().findIdBySource(tag) == 0) {
                    database.sourceDao().insertAll(new Source(tag));
                }

                database.wordDao().insertAll(
                        new Word(Language.EN, addedWord, addedTranslation, descriptionET.getText().toString().replaceAll("\\s+$", ""), tag, new Date(),
                                null, WordFile.findById(((DifficultyAdapter) difficultyRecycler.getAdapter()).getSelectedItem())));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.add_word_successfully_added, Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
    }

    //pri zmacknuti sipky zpet se aktivita zavre
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Zavola se pokud uzivatel klikne na preklad v recycleru
     *
     * @param id
     */
    @Override
    public void onItemclick(int id) {
        enterTranslation.clearFocus();
        addedTranslation = ((TranslationAdapter) translationsRecycler.getAdapter()).getSelectedTranslation();
        enterTranslation.setTextColor(getResources().getColor(R.color.black));
        dismisKeyboard();
        checkTrranslation();
    }

    //schova klavesnici
    public void dismisKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (NullPointerException e) {
                Log.d("debug", e.getMessage());
            }
        }
    }

    //hleda preklady v databazi
    class FindTranslation extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {

            DictionaryDatabase database = DictionaryDatabase.getInstance(AddWordFromText.this);

            return ((AutoCompleteAdapter) autoCompleteTextView.getAdapter()).getSearchedLanguage() == Language.EN ?
                    database.enCzJoinDao().translateToCz(database.enWordDao().findIdByWord(strings[0])) :
                    database.enCzJoinDao().translateToEn(database.czWordDao().findIdByWord(strings[0]));
        }

        @Override
        protected void onPostExecute(List<String> strings) {

            if (strings.size() == 0) {
                //pokud zadne preklady nejsou, recycler se schova a zobrazi se text s oznamenim
                translationsRecycler.setVisibility(View.GONE);
                notFoundTV.setVisibility(View.VISIBLE);
            } else {
                ((TranslationAdapter) translationsRecycler.getAdapter()).setData(strings);
                translationsRecycler.getAdapter().notifyDataSetChanged();
                translationsRecycler.setVisibility(View.VISIBLE);
                notFoundTV.setVisibility(View.GONE);
            }
        }
    }

    //kontroluje duplikaty
    class WordsExist extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            List<Word> listA = AppDatabase.getInstance(getApplicationContext()).wordDao().loadByWord(addedWord);
            List<Word> listB = AppDatabase.getInstance(getApplicationContext()).wordDao().loadByTranslation(addedWord);

            for (Word w : listA) {
                if (w.getTranslation().equals(strings[0])) {
                    return true;
                }
            }
            for (Word t : listB) {
                if (t.getTranslation().equals(strings[0])) {
                    return true;
                }
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                if (intentWord == null) {
                    verticalStepperForm.setActiveStepAsUncompleted(getString(R.string.add_word_translation_exists));
                } else {
                    verticalStepperForm.setActiveStepAsCompleted();
                }
            } else {
                verticalStepperForm.setActiveStepAsCompleted();
            }
        }
    }
}