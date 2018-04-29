package com.sdm.sdmflash.fragmentTests.writing_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.app.App;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.fragmentFlashcards.FlashCards;
import com.sdm.sdmflash.fragmentTests.TestsFragment;
import com.sdm.sdmflash.fragmentTests.writing_test.FragmentComplete;
import com.sdm.sdmflash.fragmentTests.writing_test.StepperAdapter;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

public class WritingTestActivity extends AppCompatActivity {

    private StepperLayout mStepperLayout;
    private StepperAdapter mStepperAdapter;
    private List<WordsTuple> words;
    private List<String> answers;

    public static final int WORDS_COUNT = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_test);
        mStepperAdapter = new StepperAdapter(getSupportFragmentManager(), this);
        mStepperLayout = findViewById(R.id.stepperLayout);

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int progress = getIntent().getIntExtra(TestsFragment.TIME_KEY, 0);
                String source = getIntent().getStringExtra(TestsFragment.SOURCE_KEY);
                String all = getString(R.string.all);
                if (source.equals(all))
                    source = null;
                FlashCards flashCards = FlashCards.getInstance(AppDatabase.getInstance(getApplicationContext()));
                switch (progress) {
                    case 0:
                        words = new ArrayList<>(flashCards.getDailyWordsBySource(WORDS_COUNT, source));
                        break;
                    case 1:
                        words = new ArrayList<>(flashCards.getWeeklyWordsBySource(WORDS_COUNT, source));
                        break;
                    case 2:
                        words = new ArrayList<>(flashCards.getMonthlyWordsBySource(WORDS_COUNT, source));
                        break;
                    case 3:
                        words = new ArrayList<>(flashCards.getYearsWordsBySource(WORDS_COUNT, source));
                        break;
                    case 4:
                        words = new ArrayList<>(flashCards.getAllWordsBySource(WORDS_COUNT, source));
                }

                final int wordsSize = words.size();
                answers = new ArrayList<>();
                mStepperAdapter.setWordsCount(wordsSize);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStepperLayout.setAdapter(mStepperAdapter);
                        //pokud nejsou nalezena žádná slova
                        FragmentComplete fragmentComplete = new FragmentComplete();
                        fragmentComplete.setMessage(getString(R.string.no_words_in_this_category));
                        if (wordsSize == 0){
                            mStepperLayout.setNextButtonEnabled(false);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.activity_writing_test_container, fragmentComplete)
                                    .commit();
                        }
                    }
                });
            }
        });

        mStepperLayout.setListener(new StepperLayout.StepperListener() {
            @Override
            public void onCompleted(View completeButton) {
                mStepperLayout.setBackButtonEnabled(false);
                mStepperLayout.setCompleteButtonEnabled(false);

                FragmentComplete fragmentComplete = new FragmentComplete();
                fragmentComplete.setPoints(answers.size());
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_writing_test_container, fragmentComplete)
                        .commit();

                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
                        for(String w : answers){
                            WordFile file = database.wordDao().getWordFile(w);
                            database.wordDao().changeWordFile(w, file.increase());
                        }
                    }
                });
            }

            @Override
            public void onError(VerificationError verificationError) {

            }

            @Override
            public void onStepSelected(int newStepPosition) {

            }

            @Override
            public void onReturn() {

            }
        });
    }

    //TODO: pokud platí že může existovat více slov, je třeba předělat tento systém
    public void setRightAnswer(String word){
        if (!answers.contains(word))
            answers.add(word);
    }

    public List<WordsTuple> getWords() {
        return words;
    }
}
