package com.sdm.sdmflash.databases.structure;

/**
 * Created by mety on 11.2.18.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by mety on 11.2.18.
 */

@Database(entities = {EnWord.class, EnCzJoin.class, CzWord.class}, version = 1)

public abstract class DictionaryDatabase extends RoomDatabase {

    private static volatile DictionaryDatabase DICINSTANCE;

    /**
     * Vrací instaci databáze, pokud neexistuje, vytvoří novou.
     *
     * @param context context aplikace (getApplicationContext())
     * @return instance databáze
     */
    public static DictionaryDatabase getInstance(Context context) {
        if (DICINSTANCE == null) {
            synchronized (DictionaryDatabase.class) {
                if (DICINSTANCE == null) {
                    DICINSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DictionaryDatabase.class, "SDMdictionarydatabase")
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

    public abstract EnWordDao enWordDao();

    public abstract CzWordDao czWordDao();

    public abstract EnCzJoinDao enCzJoinDao();
}

