package com.sdm.sdmflash.databases.structure.dictionaryDatabase;

/**
 * Created by mety on 11.2.18.
 */

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mety on 11.2.18.
 */

@Database(entities = {EnWord.class, EnCzJoin.class, CzWord.class}, exportSchema = false, version = 1)

public abstract class DictionaryDatabase extends RoomDatabase {

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

                    RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
                        public void onCreate(SupportSQLiteDatabase db) {
                            Log.i("debug", "Importing database from assets");
                            try {
                                File database = context.getApplicationContext().getDatabasePath("SDMdictionarydatabase");
                                copyFileUsingFileStreams(context.getApplicationContext().getAssets().open("SDMdictionarydatabase.db"), database);
                            } catch (IOException e) {
                                Log.e("AndroidRuntime", Log.getStackTraceString(e));
                            }
                        }

                        public void onOpen(SupportSQLiteDatabase db) {

                        }
                    };
                    DICINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DictionaryDatabase.class, "SDMdictionarydatabase").addCallback(rdc)
                            .build();
                    //! pouze v RAM
//                    DICINSTANCE = Room.inMemoryDatabaseBuilder(context,
//                            DictionaryDatabase.class).allowMainThreadQueries()
//                            .build();
                }
            }
        }
        return DICINSTANCE;
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

