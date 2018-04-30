package com.sdm.sdmflash.fragmentTests;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.fragmentTests.writing_test.WritingTestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestsFragment extends Fragment {

    public static final String TIME_KEY = "TIME_KEY";
    public static final String SOURCE_KEY = "SOURCE_KEY";
    public static final String LANGUAGE_KEY = "LANGUAGE_KEY";

    public TestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tests, container, false);

        //inicializace toolbaru
        final Toolbar toolbar = view.findViewById(R.id.fragment_tests_toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.tests);
        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        final SeekBar seekBar = view.findViewById(R.id.fragment_tests_seekbar_selection);
        final TextView timeSign = view.findViewById(R.id.fragment_tests_timesign);
        final Spinner sourceSpinner = view.findViewById(R.id.fragment_tests_source_select);
        final Spinner languageSpinner = view.findViewById(R.id.fragment_tests_language_select);

        view.findViewById(R.id.fragment_tests_writing_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WritingTestActivity.class);
                intent.putExtra(TIME_KEY, seekBar.getProgress());
                //zdroje
                if (sourceSpinner.getSelectedItem() != null)
                    intent.putExtra(SOURCE_KEY, sourceSpinner.getSelectedItem().toString());
                //jazyky
                if (languageSpinner.getSelectedItem() != null)
                    intent.putExtra(LANGUAGE_KEY, languageSpinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });


        //změna textu časových období
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress){
                    case 0:
                        timeSign.setText(R.string.todays_words);
                        break;
                    case 1:
                        timeSign.setText(R.string.this_weeks_words);
                        break;
                    case 2:
                        timeSign.setText(R.string.this_months_words);
                        break;
                    case 3:
                        timeSign.setText(R.string.this_years_words);
                        break;
                    case 4:
                        timeSign.setText(R.string.all_words);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //inicialice spinneru se zdroji
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<String> sources = AppDatabase.getInstance(getContext()).sourceDao().loadAllStringSources();
                sources.add(0, getString(R.string.all));
                final ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, sources);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sourceSpinner.setAdapter(sourceAdapter);
                    }
                });
            }
        });

        //inicialice spinneru s jazyky
        final List<Language> languages = Language.getLanguagesList();
        List<String> strings = new ArrayList<>(languages.size());
        for (Object object : languages) {
            strings.add(object.toString());
        }
        strings.add(0, getString(R.string.all));
        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, strings);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                languageSpinner.setAdapter(languageAdapter);
            }
        });

        return view;
    }

}
