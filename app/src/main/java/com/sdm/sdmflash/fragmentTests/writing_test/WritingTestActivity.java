package com.sdm.sdmflash.fragmentTests.writing_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.DateTools;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.TestChartEntry;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;
import com.sdm.sdmflash.fragmentFlashcards.FlashCards;
import com.sdm.sdmflash.fragmentTests.TestsFragment;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WritingTestActivity extends AppCompatActivity {

    private StepperLayout mStepperLayout;
    private StepperAdapter mStepperAdapter;
    private List<Word> words;
    private boolean[] correctAnswers;
    private boolean finished;

    public static final int WORDS_COUNT = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_test);
        mStepperAdapter = new StepperAdapter(getSupportFragmentManager(), this);
        mStepperLayout = findViewById(R.id.stepperLayout);

        //Mereni casu testu
        final Date start = new Date();

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int progress = getIntent().getIntExtra(TestsFragment.TIME_KEY, 0);
                String source = getIntent().getStringExtra(TestsFragment.SOURCE_KEY);
                String all = getString(R.string.all);
                if (source != null && source.equals(all))
                    source = null;
                FlashCards flashCards = FlashCards.getInstance(AppDatabase.getInstance(getApplicationContext()));
                String language = getIntent().getStringExtra(TestsFragment.LANGUAGE_KEY);

                if (language.equals(all)){
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
                } else {
                    //pokud byl zvolen jazyk
                    switch (progress) {
                        case 0:
                            words = new ArrayList<>(flashCards.getAllWordsBySourceAndLanguage(WORDS_COUNT, DateTools.getDayBack(new Date()), source, Language.valueOf(language)));
                            break;
                        case 1:
                            words = new ArrayList<>(flashCards.getAllWordsBySourceAndLanguage(WORDS_COUNT, DateTools.getMonthBack(new Date()), source, Language.valueOf(language)));
                            break;
                        case 2:
                            words = new ArrayList<>(flashCards.getAllWordsBySourceAndLanguage(WORDS_COUNT, DateTools.getWeekBack(new Date()), source, Language.valueOf(language)));
                            break;
                        case 3:
                            words = new ArrayList<>(flashCards.getAllWordsBySourceAndLanguage(WORDS_COUNT, DateTools.getYearBack(new Date()), source, Language.valueOf(language)));
                            break;
                        case 4:
                            words = new ArrayList<>(flashCards.getAllWordsBySourceAndLanguage(WORDS_COUNT, new Date(), source, Language.valueOf(language)));
                    }
                }

                final int wordsSize = words.size();
                correctAnswers = new boolean[wordsSize];
                mStepperAdapter.setWordsCount(wordsSize);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStepperLayout.setAdapter(mStepperAdapter);
                        //pokud nejsou nalezena žádná slova
                        FragmentComplete fragmentComplete = new FragmentComplete();
                        fragmentComplete.setMessage(getString(R.string.no_words_in_this_category));
                        if (wordsSize == 0) {
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
            // po skonční testu
            @Override
            public void onCompleted(View completeButton) {
                //mStepperLayout.setBackButtonEnabled(false);
                mStepperLayout.setCompleteButtonEnabled(false);
                finished = true;
                mStepperAdapter.findStep(mStepperAdapter.getCount()-1).onSelected();

                FragmentComplete fragmentComplete = new FragmentComplete();
                fragmentComplete.setPoints(countPoints(correctAnswers));
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_writing_test_container, fragmentComplete)
                        .commit();

                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase database = AppDatabase.getInstance(getApplicationContext());

                        //prida zaznam do databaze, jak dlouho test trval - by Mety :D
                        database.testChartDao().insertAll(new TestChartEntry(0, 0, 0, start, new Date()));

                        //zvýší nebo sníži slovu kartotéku na základě výsledku testu
                        for (int i = 0; i < correctAnswers.length; i++) {

                            String currentWord = words.get(i).getWord();
                            WordFile file = database.wordDao().getWordFile(currentWord);
                            //aktualizuje change date
                            database.wordDao().updateChangeDate(currentWord, new Date());
                            if (correctAnswers[i]){
                                database.wordDao().changeWordFile(currentWord, file.increase());
                            }else {
                                database.wordDao().changeWordFile(currentWord, file.decrease());
                            }
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

    // z pole správných odpovědí spočítá skore
    private int countPoints(boolean[] a){
        int points = 0;
        for (boolean answer : a){
            if (answer) points++;
        }
        return points;
    }

    public void setCorrectAnswer(int position) {
        correctAnswers[position] = true;
    }

    public void setFalseAnswer(int position){
        correctAnswers[position] = false;
    }

    public List<Word> getWords() {
        return words;
    }

    public boolean isFinished() {
        return finished;
    }
}
