package com.sdm.sdmflash.databases.structure.dictionaryDatabase;

/**
 * Created by mety on 11.2.18.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.huma.room_for_asset.RoomAsset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mety on 11.2.18.
 */

@Database(entities = {EnWord.class, EnCzJoin.class, CzWord.class}, version = 2)

public abstract class DictionaryDatabase extends RoomDatabase {

    public static String dictionaryDatabaseName = "SDMdictionarydatabase.db";

    private static volatile DictionaryDatabase DICINSTANCE;

    /**
     * Vrací instaci databáze, pokud neexistuje, vytvoří novou.
     *
     * @param context context aplikace (getApplicationContext())
     * @return instance databáze
     */
    public static DictionaryDatabase getInstance(final Context context) {
        if (DICINSTANCE == null) {
            synchronized (DictionaryDatabase.class) {
                if (DICINSTANCE == null) {

//                    boolean exists = checkDatabse(context);

                    DICINSTANCE = RoomAsset.databaseBuilder(context.getApplicationContext(),
                            DictionaryDatabase.class, dictionaryDatabaseName)
                            .build();
//                    try {
//                            Log.i("debug", context.getApplicationContext().getDatabasePath(dictionaryDatabaseName).exists() + "");
//                            copyFileUsingFileStreams(context.getApplicationContext().getAssets().open("SDMdictionarydatabase.db"), context.getApplicationContext().getDatabasePath(dictionaryDatabaseName));
//                        } catch (IOException e) {
//                            Log.i("debug", "Error copying database from assets!");
//                            e.printStackTrace();
//                        }
//                    }

                }
            }
        }
        return DICINSTANCE;
    }


    private static boolean checkDatabse(Context context) {

        File database = context.getApplicationContext().getDatabasePath(dictionaryDatabaseName);

        return database.exists();
    }

    private static void copyFileUsingFileStreams(InputStream source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = source;
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            Log.e("AndroidRuntime", Log.getStackTraceString(e));
        } finally {
            input.close();
            output.close();
        }
    }

    public abstract EnWordDao enWordDao();

    public abstract CzWordDao czWordDao();

    public abstract EnCzJoinDao enCzJoinDao();
}

