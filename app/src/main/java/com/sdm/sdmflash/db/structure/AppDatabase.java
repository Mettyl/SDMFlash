package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;
import com.sdm.sdmflash.db.dataTypes.Converters;

/**
 * Třída reprezentující SQLite databázi aplikace
 * Created by Dominik on 02.12.2017.
 */

@Database(entities = {Word.class},version = 1)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract WordDao wordDao();

    /**
     * Vrací instaci databáze, pokud neexistuje, vytvoří novou.
     * @param context context aplikace (getApplicationContext())
     * @return instance databáze
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    //! pouze v RAM
                    INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class,
                            "SDMdatabase")
                            .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                            .build();

                    /*INSTANCE = Room.inMemoryDatabaseBuilder(context,
                            AppDatabase.class)
                            .build();*/
                }
            }
        }
        return INSTANCE;
    }
}
