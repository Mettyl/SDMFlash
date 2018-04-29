package com.sdm.sdmflash.fragmentFlashcards;

import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.Source;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Dominik on 17.12.2017.
 */

public class FlashCards {

    private static AppDatabase db;
    private static volatile FlashCards INSTANCE;

    /**
     * Nová instance app database
     * Veškeré přístupy pouze z AccessExecutoru!!
     * Výstupy na UI prostředí pouze přes runOnUIThread()
     * @param db AppDatabase
     */
    private FlashCards(AppDatabase db) {
        FlashCards.db = db;
    }

    /**
     * Vrací instanci lazy singletonu {@link FlashCards}, pokud neexituje vytvoří novou
     * Veškeré přístupy pouze z AccessExecutoru!!
     * @param db AppDatabase databáze aplikace
     * @return instance Flashcards
     */
    public static FlashCards getInstance(AppDatabase db) {
        if (INSTANCE == null) {
            synchronized (FlashCards.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlashCards(db);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Sestaví frontu slovíček která mohou být použita pro flashCards
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param files pole slvíček pro každou kartotéku
     * @return fronta slovíček, která mohou být použita pro flashCards
     */
    private Queue<WordsTuple> doFinalGroup(final int NUMBER_OF_WORDS, Queue<WordsTuple>[] files){
        Queue<WordsTuple> finalGroup = new LinkedList<>();

        int initWords = finalGroup.size();
        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            if (finalGroup.size() >= NUMBER_OF_WORDS)return finalGroup;

            //pokud máš co přidat, přidej
            if (files[i].peek() != null){
                finalGroup.add(files[i].poll());
            }

            if (i == 5){
                i = 0;
                //nic se nezměnilo
                if (initWords == finalGroup.size()) return finalGroup;
                initWords = finalGroup.size();
            }
        }
        return finalGroup;
    }

    //----------------------------------------------
    //custom FlashCard + source
    //---------------------------------------------

    /**
     * Vytvoří skupinu slovíček z jednoho zdroje připravených pro denní opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getDailyWordsBySource(final int NUMBER_OF_WORDS, String source){

        return doFinalGroup(NUMBER_OF_WORDS, loadDailyFilesBySource(NUMBER_OF_WORDS, source));

    }

    /**
     * Vytvoří skupinu slovíček z jednoho zdroje připravených pro týdenní opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getWeeklyWordsBySource(final int NUMBER_OF_WORDS, String source){

        return doFinalGroup(NUMBER_OF_WORDS, loadWeeklyFilesBySource(NUMBER_OF_WORDS, source));

    }

    /**
     * Vytvoří skupinu slovíček z jednoho zdroje připravených pro měsíční opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getMonthlyWordsBySource(final int NUMBER_OF_WORDS, String source){

        return doFinalGroup(NUMBER_OF_WORDS, loadMonthlyFilesBySource(NUMBER_OF_WORDS, source));

    }

    /**
     * Vytvoří skupinu slovíček z jednoho zdroje připravených pro měsíční opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getYearsWordsBySource(final int NUMBER_OF_WORDS, String source){

        return doFinalGroup(NUMBER_OF_WORDS, loadYearsFilesBySource(NUMBER_OF_WORDS, source));

    }

    /**
     * Vytvoří skupinu slovíček z jednoho zdroje připravených pro měsíční opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getAllWordsBySource(final int NUMBER_OF_WORDS, String source){

        return doFinalGroup(NUMBER_OF_WORDS, loadAllFilesBySource(NUMBER_OF_WORDS, source));

    }



    /**
     * Načte z databáze z daného zdroje z každé kartotéky n slovíček pro dnešní den a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param source {@link Source} ze kterého se slova vybírají (pokud null, je vybirano ze všech zdrojů)
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadDailyFilesBySource(final int NUMBER_OF_WORDS, String source){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            if (source != null)
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), source, NUMBER_OF_WORDS));
            else
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z daného zdroje z každé kartotéky n slovíček pro tento týden a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param source {@link Source} ze kterého se slova vybírají  (pokud null, je vybirano ze všech zdrojů)
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadWeeklyFilesBySource(final int NUMBER_OF_WORDS, String source){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            if (source != null)
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), source, NUMBER_OF_WORDS));
            else
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z daného zdroje z každé kartotéky n slovíček pro tento měsíc a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param source {@link Source} ze kterého se slova vybírají (pokud null, je vybirano ze všech zdrojů)
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadMonthlyFilesBySource(final int NUMBER_OF_WORDS, String source){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            if (source != null)
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), source, NUMBER_OF_WORDS));
            else
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z daného zdroje z každé kartotéky n slovíček pro tento rok a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param source {@link Source} ze kterého se slova vybírají (pokud null, je vybirano ze všech zdrojů)
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadYearsFilesBySource(final int NUMBER_OF_WORDS, String source){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            if (source != null)
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), source, NUMBER_OF_WORDS));
            else
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z daného zdroje z každé kartotéky n slovíček pro tento rok a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @param source {@link Source} ze kterého se slova vybírají (pokud null, je vybirano ze všech zdrojů)
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadAllFilesBySource(final int NUMBER_OF_WORDS, String source){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            if (source != null)
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), new Date(0), source, NUMBER_OF_WORDS));
            else
                files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), new Date(0), NUMBER_OF_WORDS));
        }
        return files;
    }
}
