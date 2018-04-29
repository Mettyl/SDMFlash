package com.sdm.sdmflash.fragmentFlashcards;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;

import java.io.File;
import java.util.Queue;

/**
 * zdroj animace: https://www.thedroidsonroids.com/blog/android/android-flipa-card-animation-exlpained/
 */
public class FlashCardsActivity extends AppCompatActivity {

    //private TextView text;
    private ViewGroup container;
//    private Button improveButton;
//    private int currentWordIndex;
//    private boolean toggle;
    private FlashCards flashCards;
    private Queue<WordsTuple> words;
    private WordsTuple currentWord;
    private final int WORDS_COUNT = 10;

    //animation properties
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    public static final String TIME = "time";
    public static final String SOURCE = "resource";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);

        //animation initialization
        findViews();
        loadAnimations();
        changeCameraDistance();

        container = findViewById(R.id.activity_flashcards_card_container);
//        improveButton = findViewById(R.id.activity_flashcards_improve_button);
//        text = findViewById(R.id.word);
        flashCards = FlashCards.getInstance(AppDatabase.getInstance(this));
//        currentWordIndex = 0;
//        toggle = true;

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
            //vybere slova
            String source = getIntent().getStringExtra(SOURCE);
            switch (getIntent().getIntExtra(TIME, 0)){
                case 0:
                    words = flashCards.getDailyWordsBySource(WORDS_COUNT, source);
                    break;
                case 1:
                    words = flashCards.getWeeklyWordsBySource(WORDS_COUNT, source);
                    break;
                case 2:
                    words = flashCards.getMonthlyWordsBySource(WORDS_COUNT, source);
                    break;
                case 3:
                    words = flashCards.getYearsWordsBySource(WORDS_COUNT, source);
                    break;
                case 4:
                    words = flashCards.getAllWordsBySource(WORDS_COUNT, source);
                    break;
            }
            //TODO: předělat tak, aby se zobrazovalo už na předchozím fragmentu výběru jako Toast
            //pokud databáze neobsahuje slova z tohoto zdroje
            if (words.size() == 0){
                WordsTuple word = new WordsTuple(getString(R.string.empty_source_error), getString(R.string.empty_source_error));
                words.add(word);
            }
            currentWord = words.poll();
            words.add(currentWord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpCard(currentWord.word, currentWord.translation);
                }
            });
            }
        });

        // po stisknutí talčítka next
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWord = words.poll();
                words.add(currentWord);

//                if (!improveButton.isShown())
//                    ((ViewGroup)findViewById(R.id.activity_flashcards_container_all)).addView(improveButton);

                mSetLeftIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                    }
                });

                //oprava pořadí karet
                TextView frontText = mCardFrontLayout.findViewById(R.id.activity_flashcards_card_text);
                if (mIsBackVisible){
                    frontText.setText(currentWord.word);
                    flipCard(view);
                }else{
                    setUpCard(currentWord.word, currentWord.translation);
                }
            }
        });

        //nastavit text text na zadní stranu karty až po jejím otočení
        mSetLeftIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!mIsBackVisible){
                    TextView backText = mCardBackLayout.findViewById(R.id.activity_flashcards_card_text);
                    backText.setText(currentWord.translation);
                }
            }
        });

        //po stisknutí tlačítak zlepšení
        /*findViewById(R.id.activity_flashcards_improve_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AccessExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        //posunutí v kartotéce
                        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
                        WordFile currentFile = database.wordDao().getWordFile(currentWord.word);
                        database.wordDao().changeWordFile(currentWord.word, currentFile.increase());
                    }
                });
                ((ViewGroup)findViewById(R.id.activity_flashcards_container_all)).removeView(v);
            }
        });*/

        // po kliknutí na kartu
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard(v);
            }
        });
    }

    private void setUpCard(String word, String translation){
        TextView frontText = mCardFrontLayout.findViewById(R.id.activity_flashcards_card_text);
        TextView backText = mCardBackLayout.findViewById(R.id.activity_flashcards_card_text);
        frontText.setText(word);
        backText.setText(translation);
    }

    //metody pro inicializaci animace
    private void findViews() {
        mCardBackLayout = findViewById(R.id.activity_flashcards_card_back);
        mCardFrontLayout = findViewById(R.id.activity_flashcards_card_front);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    public void flipCard(View view) {
        if (!mIsBackVisible) {
            mIsBackVisible = true;
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
        } else {
            mIsBackVisible = false;
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
        }
    }

}