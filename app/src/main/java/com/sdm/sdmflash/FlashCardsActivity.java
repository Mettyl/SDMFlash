package com.sdm.sdmflash;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.sdm.sdmflash.db.DbTest;

public class FlashCardsActivity extends AppCompatActivity{

    private TextView text;
    private int i;
    private boolean toggle;
    private boolean touched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        i = 0;
        toggle = true;
        touched = false;
        text = (TextView) findViewById(R.id.word);
        text.setText(DbTest.words[i]);

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i<DbTest.words.length-1)i++;
                else i = 0;

                text.setText(DbTest.words[i]);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!touched){
                    if (toggle)text.setText(DbTest.translations[i]);
                    else text.setText(DbTest.words[i]);
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
