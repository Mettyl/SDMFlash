package com.sdm.sdmflash.databases.structure;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sdm.sdmflash.databases.dataTypes.DatesTuple;
import com.sdm.sdmflash.databases.dataTypes.Language;
import com.sdm.sdmflash.databases.dataTypes.WordFile;
import com.sdm.sdmflash.databases.dataTypes.WordsTuple;

import java.util.Date;
import java.util.List;

/**
 * Jednotlivé query příkazy pro přístup k databázi.
 * Created by Dominik on 02.12.2017.
 */

@Dao
public interface WordDao {

    /**
     * Vrací celý obsah databáze
     * @return List objektů Word
     */
    @Query("SELECT * FROM words ORDER BY LOWER(words.word) ASC")
    LiveData<List<Word>> getAll();

    /**
     * Vrací slovíčka podle id
     * @param wordIds pole id
     * @return List objektů Word
     */
    @Query("SELECT * FROM words WHERE id IN (:wordIds)")
    List<Word> loadAllByIds(int[] wordIds);

    /**
     * Vrací slovíčko podle id
     * @param id id slovíčka
     * @return hledaný objekt {@link Word}
     */
    @Query("SELECT * FROM words WHERE id = :id")
    Word loadById(int id);

    /**
     * vloží slovíčka
     * @param words Objekty Word
     */
    @Insert
    void insertAll(Word... words);

    /**
     * maže slovíčka
     * @param word Slovíčko ke smazání
     */
    @Delete
    void delete(Word word);


    /**
     * Vymaže obsah databáze (inkrementace id pokračuje od poslední hodnoty)
     */
    @Query("DELETE FROM words")
    void deleteAll();

    /**
     * Vrací sloupec word z tabulky
     * @return Pole slov
     */
    @Query("SELECT word FROM words")
    String[] loadWordColumn();

    /**
     * Vrací sloupec translation z tabulky
     * @return Pole překladů
     */
    @Query("SELECT translation FROM words")
    String[] loadTranslationColumn();

    /**
     * Vrací sloupec word z tabulky
     * @param language jazyk filtru
     * @return pole slov
     */
    @Query("SELECT word FROM words WHERE language = :language")
    String[] loadWordColumn(Language language);

    /**
     * Vrací sloupec translation z tabulky
     * @param language jazyk filtru
     * @return Pole překladů
     */
    @Query("SELECT translation FROM words WHERE language = :language")
    String[] loadTranslationColumn(Language language);

    /**
     * Vrací objekt DatesTuple,how to find out if lis empty který obsahuje datum přidání a poslední změny
     * @param word hledané slovo
     * @return DatesTuple(add_date, change_date)
     */
    @Query("SELECT add_date, change_date FROM words WHERE word = :word LIMIT 1")
    DatesTuple loadDatesByWord(String word);

    /**
     *
     * @param word slovo pro změnu kartotéky
     * @param file nová kartotéka
     * @return pocet změněných řádků
     */
    @Query("UPDATE words SET file = :file WHERE word = :word")
    int changeWordFile(String word, WordFile file);

    /**
     *
     * @param id ID slova pro změnu kartotéky
     * @param file nová kartotéka
     * @return pocet změněných řádků
     */
    @Query("UPDATE words SET file = :file WHERE id = :id")
    int changeWordFile(int id, WordFile file);

    /**
     * Vybírá z celkové kartokéky
     * @param file kartoréka k zobrazení
     * @param limit počet zobrazených slov
     * @return dvojice slov z kartotéky
     */
    @Query("SELECT word, translation FROM words WHERE file = :file ORDER BY change_date ASC LIMIT :limit")
    List<WordsTuple> loadWordPairsByFile(WordFile file, int limit);

    /**
     * Vybírá od data
     * @param file kartoréka k zobrazení
     * @param limit počet zobrazených slov
     * @param fromDate od tohoto data
     * @return dvojice slov z kartotéky
     */
    @Query("SELECT word, translation FROM words WHERE file = :file AND add_date >= :fromDate ORDER BY change_date ASC LIMIT :limit")
    List<WordsTuple> loadWordPairsByFile(WordFile file, Date fromDate, int limit);

    /**
     * Vybírá od data do data
     * @param file kartoréka k zobrazení
     * @param limit počet zobrazených slov
     * @param fromDate od tohoto data
     * @param toDate do tohoto data
     * @return dvojice slov z kartotéky
     */
    @Query("SELECT word, translation FROM words WHERE file = :file AND add_date >= :fromDate AND add_date <= :toDate ORDER BY change_date ASC LIMIT :limit")
    List<WordsTuple> loadWordPairsByFile(WordFile file, Date fromDate, Date toDate, int limit);

    // TEMP!!!
    @Query("UPDATE words SET add_date = :date WHERE id = :id")
    int changeDate(int id, Date date);


    @Query("SELECT COUNT(*) FROM words WHERE change_date IS NULL")
    int notTestedWords();

    @Query("SELECT COUNT(*) FROM words")
    int countWords();


}
