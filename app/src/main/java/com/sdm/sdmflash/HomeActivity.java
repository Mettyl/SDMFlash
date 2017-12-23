package com.sdm.sdmflash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.structure.AppDatabase;

public class HomeActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //spustí test
        new DbTest().test(getApplicationContext());
    }

}
