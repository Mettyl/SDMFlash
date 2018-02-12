package com.sdm.sdmflash.db;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;


import com.sdm.sdmflash.db.dataTypes.File;
import com.sdm.sdmflash.db.dataTypes.Language;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;

import java.util.Date;

/**
 * testovací třída
 * Created by Dominik on 07.12.2017.
 */

public class DbTest {

    public void test(final Context context){

        final AccessExecutor accessExecutor = new AccessExecutor();
        accessExecutor.execute(new Runnable() {
            @Override
            public void run() {

                AppDatabase db = AppDatabase.getInstance(context);

                //zkušební vkládání
                //přístup k metodám přes db.wordDao().
                /*db.wordDao().deleteAll();*/
//                db.wordDao().insertAll(
//                        new Word(Language.CZ, "Praha", "Prague", "book", new Date(), new Date(), File.FILE_1),
//                        new Word(Language.CZ, "Pes", "Dog", "book", new Date(), new Date(), File.FILE_2),
//                        new Word(Language.EN, "work", "práce", "book", new Date(), new Date(), File.FILE_1),
//                        new Word(Language.EN, "jump", "skočit", "book", new Date(), new Date(), File.FILE_1),
//                        new Word(Language.CZ, "slyšet", "hear", "book", new Date(), new Date(), File.FILE_1)
//                );

                //zkouška čtení, vypíše obsah
                for (Word word : db.wordDao().getAll()) Log.d("debug", word.toString());

            }
        });
    }

}
