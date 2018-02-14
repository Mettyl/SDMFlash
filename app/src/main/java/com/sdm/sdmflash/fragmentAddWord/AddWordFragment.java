package com.sdm.sdmflash.fragmentAddWord;



import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.AppDatabase;
import com.sdm.sdmflash.databases.structure.DictionaryDatabase;
import com.sdm.sdmflash.databases.structure.Source;
import com.sdm.sdmflash.databases.structure.Word;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWordFragment extends Fragment implements TranslationsDialog.TranslationsDialogListener {


    public AddWordFragment() {
        // Required empty public constructor
    }

    //    private EditText word;
//    private EditText translation;
    private Button button;
    private Spinner spinner;
    private RelativeLayout dialog;
    private RelativeLayout background;
    private EditText sourceText;
    TextView translation;
    private Language wordLanguage;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_word, container, false);

        dialog = view.findViewById(R.id.add_source_layout);
        dialog.setVisibility(View.INVISIBLE);

        background = view.findViewById(R.id.add_word_main);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setAlpha(0.2f);
                for (int i = 0; i < background.getChildCount(); i++) {
                    View child = background.getChildAt(i);
                    child.setEnabled(false);
                }
                dialog.setVisibility(View.VISIBLE);
            }
        });

        sourceText = view.findViewById(R.id.add_source_text);

        view.findViewById(R.id.add_source_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase.getInstance(getContext()).sourceDao().insertAll(new Source(sourceText.getText().toString()));
                    }
                });

                dialog.setVisibility(View.INVISIBLE);
                for (int i = 0; i < background.getChildCount(); i++) {
                    View child = background.getChildAt(i);
                    child.setEnabled(true);
                }
                background.setAlpha(1f);
                updateSourceList();
            }
        });
        final String[] pole = {"ahoj", "abeceda", "auto", "anotace"};
        final AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoEditText_add_word_fragment);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DictionaryDatabase database = DictionaryDatabase.getInstance(getContext());

                List<String> list = database.enWordDao().findWord(autoCompleteTextView.getText().toString() + "%", 5);
                if (list.size() == 0) {
                    list = database.czWordDao().findWord(autoCompleteTextView.getText().toString() + "%", 5);
                    wordLanguage = Language.CZ;
                } else {
                    wordLanguage = Language.EN;
                }
                String[] slovo = new String[list.size()];
                if (slovo.length != 0) {
                    slovo = list.toArray(slovo);
                } else {
                    slovo = new String[1];
                    slovo[0] = "Unknown word";
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, slovo);
                autoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        translation = view.findViewById(R.id.translation_field);
        button = view.findViewById(R.id.add_button);
        spinner = view.findViewById(R.id.add_word_spinner);

        final TranslationsDialog dialog = new TranslationsDialog();
        dialog.setTargetFragment(this, 0);

        Button translateButton = view.findViewById(R.id.translate_button);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DictionaryDatabase database = DictionaryDatabase.getInstance(getContext());
                Bundle bundle = new Bundle();
                List<String> list;
                if (wordLanguage == Language.CZ) {
                    list = database.enCzJoinDao().translateToEn(database.czWordDao().findIdByWord(autoCompleteTextView.getText().toString()));
                } else {
                    list = database.enCzJoinDao().translateToCz(database.enWordDao().findIdByWord(autoCompleteTextView.getText().toString()));
                }

                String[] pole = new String[list.size()];
                pole = list.toArray(pole);
                bundle.putStringArray("translations", pole);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "showDialog");

            }
        });

        if (getArguments() != null) {
            final int id = getArguments().getInt("id");

            new AccessExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Word slovo = AppDatabase.getInstance(getContext()).wordDao().loadById(id);
                    autoCompleteTextView.setText(slovo.getWord());
                    translation.setText(slovo.getTranslation());
                }
            });


        }

        updateSourceList();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!autoCompleteTextView.getText().toString().equals("") && !translation.getText().toString().equals("")) {
                            AppDatabase.getInstance(getContext()).wordDao().insertAll(new Word(
                                    Language.CZ,
                                    autoCompleteTextView.getText().toString(),
                                    translation.getText().toString(),
                                    (String) spinner.getSelectedItem(),
                                    new Date(),
                                    new Date(),
                                    WordFile.FILE_1));
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    autoCompleteTextView.setText("");
                                    translation.setText("");
                                    Snackbar.make(view, "Word added", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                            });
                        } else {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(view, "Add autoCompleteTextView", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }

    public void updateSourceList() {
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<String> sourceList = AppDatabase.getInstance(getContext()).sourceDao().loadAllStringSources();
                spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, sourceList));
            }
        });
    }

    @Override
    public void onClick(String word) {
        translation.setText(word);
    }
}
