package com.sdm.sdmflash.databases.structure.appDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.sdm.sdmflash.databases.dataTypes.Converters;

/**
 * Lazy singleton třída reprezentující SQLite databázi aplikace
 * Created by Dominik on 02.12.2017.
 */

@Database(entities = {Word.class, Source.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /**
     * Vrací instaci databáze, pokud neexistuje, vytvoří novou.
     * @param context context aplikace (getApplicationContext())
     * @return instance databáze
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "SDMdatabase")
                            .build();
//                    INSTANCE = Room.inMemoryDatabaseBuilder(context,
//                            AppDatabase.class)
//                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract WordDao wordDao();

    public abstract SourceDao sourceDao();
}
