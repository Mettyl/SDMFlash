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
import com.sdm.sdmflash.db.dataTypes.Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //spustí test
        new DbTest().test(getApplicationContext());

        File database = getDatabasePath("SDMdatabase.db");
        Log.d("debug", database.getAbsolutePath());
        Log.d("debug", getExternalFilesDir(null).getAbsolutePath());

        try {
            File out = new File(getExternalFilesDir(null), "SDMdatabase.db");
            copyFileUsingFileStreams(database, out);
        } catch (IOException e) {
            Log.e("AndroidRuntime", Log.getStackTraceString(e));
        }

        /*try {
            copyFileUsingFileStreams(database, getExternalFilesDir(null));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (Exception e){
            Log.e("AndroidRuntime", Log.getStackTraceString(e));
        }
        finally {
            input.close();
            output.close();
        }
    }
}
