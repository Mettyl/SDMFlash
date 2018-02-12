package com.sdm.sdmflash;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sdm.sdmflash.db.DbTest;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.dataTypes.DatesTuple;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            }
        });

        //spustí test
        new DbTest().test(getApplicationContext());
    }
}
