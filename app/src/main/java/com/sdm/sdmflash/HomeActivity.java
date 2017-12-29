package com.sdm.sdmflash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.structure.AppDatabase;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //spustí test
        new DbTest().test(getApplicationContext());

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(HomeActivity.this, MainActivity.class)
                );
            }
        });
    }

}
