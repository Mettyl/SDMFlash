package com.sdm.sdmflash.fragmentFlashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;

import java.util.List;

public class FlashcardsFragment extends Fragment {

    private SeekBar seekBar;
    private TextView timeSign;
    private Spinner resourceSpinner;
    private Button startButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flashcards, container, false);

        //inicializace toolbaru
        final Toolbar toolbar = view.findViewById(R.id.fragment_flashcards_toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("Your words");
        final DrawerLayout drawerLayout = ((MainActivity) activity).getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //inicialice views
        seekBar = view.findViewById(R.id.fragment_tests_seekbar_selection);
        timeSign = view.findViewById(R.id.fragment_tests_timesign);
        resourceSpinner = view.findViewById(R.id.fragment_tests_source_select);
        startButton = view.findViewById(R.id.fragment_flashcards_start_button);

        //po potvrzení sestavení flashcards
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        //inicialice spinneru se zdroji
        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<String> sources = AppDatabase.getInstance(getContext()).sourceDao().loadAllStringSources();
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, sources);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resourceSpinner.setAdapter(adapter);
                    }
                });
            }
        });


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

        //zahajení deního testu
        view.findViewById(R.id.fragment_flashcards_daily_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FlashCardsActivity.class);
                intent.putExtra(FlashCardsActivity.TIME, 0);
                getActivity().startActivity(intent);
            }
        });

        //zahajení týdeního testu
        view.findViewById(R.id.fragment_flashcards_week_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FlashCardsActivity.class);
                intent.putExtra(FlashCardsActivity.TIME, 1);
                getActivity().startActivity(intent);
            }
        });

        //zahajení měsíčního testu
        view.findViewById(R.id.fragment_flashcards_month_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FlashCardsActivity.class);
                intent.putExtra(FlashCardsActivity.TIME, 2);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    public void onSubmit(){
        Intent intent = new Intent(getActivity(), FlashCardsActivity.class);
        intent.putExtra(FlashCardsActivity.TIME, seekBar.getProgress());
        if (resourceSpinner.getSelectedItem() != null)
            intent.putExtra(FlashCardsActivity.SOURCE, resourceSpinner.getSelectedItem().toString());
        getActivity().startActivity(intent);
    }

}
