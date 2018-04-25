package com.sdm.sdmflash.fragmentStudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;

public class FlashCardsActivity extends AppCompatActivity{

    private TextView text;
    private int i;
    private boolean toggle;
    private boolean touched;

    private String[] words;
    private String [] translations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new AccessExecutor().execute(new Runnable() {
            @Override
            public void run() {
                words = AppDatabase.getInstance(getApplicationContext()).wordDao().loadWordColumn();
                translations = AppDatabase.getInstance(getApplicationContext()).wordDao().loadTranslationColumn();
                i = 0;
                toggle = true;
                touched = false;
                text = findViewById(R.id.word);
                text.setText(words[i]);

                findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (i<words.length-1)i++;
                        else i = 0;

                        text.setText(words[i]);
                    }
                });
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!touched){
                    if (toggle)text.setText(translations[i]);
                    else text.setText(words[i]);
                    toggle = !toggle;
                    touched = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                break;
        }
        return super.onTouchEvent(event);
    }
}
