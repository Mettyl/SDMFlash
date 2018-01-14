package com.sdm.sdmflash.db;

import android.content.Context;
import android.util.Log;

import com.sdm.sdmflash.db.dataTypes.WordFile;
import com.sdm.sdmflash.db.dataTypes.Language;
import com.sdm.sdmflash.db.dataTypes.WordsTuple;
import com.sdm.sdmflash.db.structure.AccessExecutor;
import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Source;
import com.sdm.sdmflash.db.structure.Word;
import com.sdm.sdmflash.study.flashcards.FlashCards;

import java.util.Date;

/**
 * testovací třída
 * Created by Dominik on 07.12.2017.
 */

public class DbTest {

    //pouze pro testovací databazi v mezipaměti
    private static AppDatabase db;

    public void test(final Context context){
        //vytvoří instanci databáze v mezipaměti (při každém spustění se vytváří nová testovací databáze)
        db = AppDatabase.getInstance(context);

        //vytvoří instanci databáze (konečná)
        //db = AppDatabase.getInstance(context);

        final AccessExecutor accessExecutor = new AccessExecutor();
        accessExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //vytvoří instanci databáze (konečná)
                //AppDatabase db = AppDatabase.getInstance(context);

                //zkušební vkládání
                //přístup k metodám přes db.wordDao();
                db.wordDao().deleteAll();
                db.wordDao().insertAll(
                        new Word(Language.CZ, "Praha", "Prague", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "Pes", "Dog", "book", new Date(), new Date(), WordFile.FILE_2),
                        new Word(Language.EN, "work", "práce", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.EN, "jump", "skočit", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "slyšet", "hear", "book", new Date(), new Date(), WordFile.FILE_1)
                );

                db.wordDao().changeWordFile("work", WordFile.FILE_5);
                //db.wordDao().changeDate(3, new Date(0));
                for (WordsTuple word : FlashCards.getInstance(db).getMonthlyWords(10)) Log.d("debug", word.word);

                //Log.d("debug", new SimpleDateFormat("dd. MM. yyyy HH:mm:ss.SSSZ").format(new Date(0)));

                //zkouška čtení, vypíše obsah
                for (Word word : db.wordDao().getAll()) Log.d("debug", word.toString());

                //přidání zdrojů
                db.sourceDao().insertAll(new Source("Kniha"), new Source("seznam.cz"));
            }
        });
    }
}
