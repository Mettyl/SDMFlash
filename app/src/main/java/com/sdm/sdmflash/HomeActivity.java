package com.sdm.sdmflash;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.dataTypes.DatesTuple;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;
import com.sdm.sdmflash.db.dataTypes.File;
import com.sdm.sdmflash.db.dataTypes.Language;

import java.util.Date;

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
