package com.sdm.sdmflash.fragmentStudy;

import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;
import com.sdm.sdmflash.databases.structure.AppDatabase;

import java.util.Calendar;
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
     * @param db AppDatabase
     */
    private FlashCards(AppDatabase db) {
        FlashCards.db = db;
    }

    /**
     * Vrací instanci {@link FlashCards}, pokud neexituje vytvoří novou
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
     * Vytvoří skupinu slovíček připravených pro denní opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getDailyWords(final int NUMBER_OF_WORDS){

        return doFinalGroup(NUMBER_OF_WORDS, loadDailyFiles(NUMBER_OF_WORDS));

    }

    /**
     * Vytvoří skupinu slovíček připravených pro týdenní opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getWeeklyWords(final int NUMBER_OF_WORDS){

        return doFinalGroup(NUMBER_OF_WORDS, loadWeeklyFiles(NUMBER_OF_WORDS));

    }

    /**
     * Vytvoří skupinu slovíček připravených pro měsíční opakování
     * @return Fronta dvojic slov k zobrazení
     */
    public Queue<WordsTuple> getMonthlyWords(final int NUMBER_OF_WORDS){

        return doFinalGroup(NUMBER_OF_WORDS, loadMonthlyFiles(NUMBER_OF_WORDS));

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

    /**
     * Načte z databáze z každé kartotéky n slovíček pro dnešní den a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadDailyFiles(final int NUMBER_OF_WORDS){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z každé kartotéky n slovíček pro tento týden a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadWeeklyFiles(final int NUMBER_OF_WORDS){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }

    /**
     * Načte z databáze z každé kartotéky n slovíček pro tento měsíc a vloží do polí
     * @param NUMBER_OF_WORDS n počet slovíček pro opakování
     * @return pole slvíček pro každou kartotéku
     */
    private Queue<WordsTuple>[] loadMonthlyFiles(final int NUMBER_OF_WORDS){
        Queue<WordsTuple>[] files = new LinkedList[WordFile.NUM_OF_FILES+1];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        for (int i = 1; i <= WordFile.NUM_OF_FILES; i++) {
            files[i] = new LinkedList<>();
            files[i].addAll(db.wordDao().loadWordPairsByFile(WordFile.findById(i), calendar.getTime(), NUMBER_OF_WORDS));
        }
        return files;
    }
}
