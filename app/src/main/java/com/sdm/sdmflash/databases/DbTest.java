package com.sdm.sdmflash.databases;

import android.content.Context;
import android.util.Log;

import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.AccessExecutor;
import com.sdm.sdmflash.databases.structure.AppDatabase;
import com.sdm.sdmflash.databases.structure.Source;
import com.sdm.sdmflash.databases.structure.Word;
import com.sdm.sdmflash.fragmentStudy.FlashCards;

import java.util.Date;
import java.util.Random;

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
                        new Word(Language.CZ, "araha", "Prague", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "bes", "Dog", "book", new Date(), new Date(), WordFile.FILE_2),
                        new Word(Language.EN, "cork", "práce", "book", new Date(), null, WordFile.FILE_1),
                        new Word(Language.EN, "dump", "skočit", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "asdf", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "dss", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "asdf", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "bstn", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "arhar", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "gdtza", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "eshn", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "ncbt", "hear", "book", new Date(), null, WordFile.FILE_1),
                        new Word(Language.CZ, "arhav", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "jzzfd", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "ehtcv", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "btyey", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "tjby", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "yeryvhy", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "ytytryr", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "awetyfd", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "bvxn", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "ltufiv", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "lztjkc", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "cgiciz", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "czckcz", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "cmhmz", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "xzuzs", "hear", "book", new Date(), null, WordFile.FILE_1),
                        new Word(Language.CZ, "xzmx", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "srtujsy", "hear", "book", new Date(), new Date(), WordFile.FILE_1),
                        new Word(Language.CZ, "nxtjs", "hear", "book", new Date(), new Date(), WordFile.FILE_1)

                );
                Random rnd = new Random();
                String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                chars = chars.toLowerCase();
                //just for testing
//                for (int i = 0; i < 1000; i++){
//                    char c = chars.charAt(rnd.nextInt(chars.length()));
//                    String a = "adawuias";
//                    a = a.replace('a', c);
//                    char d = chars.charAt(rnd.nextInt(chars.length()));
//                    String b = "oimiohuy";
//                    b = b.replace('o', d);
//                    db.wordDao().insertAll(new Word(Language.CZ, a, b,"random", new Date(), null, WordFile.FILE_1));
//                }
                //vytvoří instanci databáze (konečná)
                db.wordDao().changeWordFile("work", WordFile.FILE_5);
                //db.wordDao().changeDate(3, new Date(0));
                for (WordsTuple word : FlashCards.getInstance(db).getMonthlyWords(10)) Log.d("debug", word.word);

                //Log.d("debug", new SimpleDateFormat("dd. MM. yyyy HH:mm:ss.SSSZ").format(new Date(0)));


                //přidání zdrojů
                db.sourceDao().insertAll(new Source("Kniha"), new Source("seznam.cz"));
            }
        });
    }
}
