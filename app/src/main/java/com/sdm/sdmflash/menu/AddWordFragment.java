package com.sdm.sdmflash.menu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.dataTypes.Language;
import com.sdm.sdmflash.db.dataTypes.WordFile;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddWordFragment extends Fragment {


    public AddWordFragment() {
        // Required empty public constructor
    }

    EditText word;
    EditText translation;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_word, container, false);

        word = view.findViewById(R.id.word_field);
        translation = view.findViewById(R.id.translation_field);
        button = view.findViewById(R.id.add_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase.getInstance(getContext()).wordDao().insertAll(new Word(Language.CZ, word.getText().toString(), translation.getText().toString(), "", new Date(), new Date(), WordFile.FILE_1));
                        Log.d("debug", "added");
                        DbTest.update();
                        Log.d("debug", "updated");
                    }
                });
            }
        });

        return view;
    }

}
