package com.sdm.sdmflash.menu;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.dataTypes.Language;
import com.sdm.sdmflash.db.dataTypes.WordFile;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Source;
import com.sdm.sdmflash.db.structure.Word;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWordFragment extends Fragment {


    public AddWordFragment() {
        // Required empty public constructor
    }

    private EditText word;
    private EditText translation;
    private Button button;
    private Spinner spinner;
    private RelativeLayout dialog;
    private RelativeLayout background;
    private EditText sourceText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_word, container, false);

        dialog = view.findViewById(R.id.add_source_layout);
        dialog.setVisibility(dialog.INVISIBLE);

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
                dialog.setVisibility(dialog.VISIBLE);
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

                dialog.setVisibility(dialog.INVISIBLE);
                for (int i = 0; i < background.getChildCount(); i++) {
                    View child = background.getChildAt(i);
                    child.setEnabled(true);
                }
                background.setAlpha(1f);
                updateSourceList();
            }
        });

        word = view.findViewById(R.id.word_field);
        translation = view.findViewById(R.id.translation_field);
        button = view.findViewById(R.id.add_button);
        spinner = (Spinner)view.findViewById(R.id.add_word_spinner);

        updateSourceList();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!word.getText().toString().equals("") && !translation.getText().toString().equals("")) {
                            AppDatabase.getInstance(getContext()).wordDao().insertAll(new Word(
                                    Language.CZ,
                                    word.getText().toString(),
                                    translation.getText().toString(),
                                    (String) spinner.getSelectedItem(),
                                    new Date(),
                                    new Date(),
                                    WordFile.FILE_1));
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    word.setText("");
                                    translation.setText("");
                                    Snackbar.make(view, "Word added", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                }
                            });
                        } else {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(view, "Add word", Snackbar.LENGTH_SHORT)
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

    public void updateSourceList(){
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<String> sourceList = AppDatabase.getInstance(getContext()).sourceDao().loadAllStringSources();
                spinner.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, sourceList));
            }
        });
    }

}
