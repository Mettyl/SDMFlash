package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sdm.sdmflash.db.dataTypes.DatesTuple;
import com.sdm.sdmflash.db.dataTypes.Language;

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
    @Query("SELECT * FROM words")
    List<Word> getAll();

    /**
     * Vrací slovíčka podle id
     * @param wordIds pole id
     * @return List objektů Word
     */
    @Query("SELECT * FROM words WHERE id IN (:wordIds)")
    List<Word> loadAllByIds(int[] wordIds);

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
    String[] loadWordColumnByLanguage(Language language);

    /**
     * Vrací sloupec translation z tabulky
     * @param language jazyk filtru
     * @return Pole překladů
     */
    @Query("SELECT translation FROM words WHERE language = :language")
    String[] loadTranslationColumn(Language language);

    /**
     * Vrací objekt DatesTuple, který obsahuje datum přidání a poslední změny
     * @param word hledané slovo
     * @return DatesTuple(add_date, change_date)
     */
    @Query("SELECT add_date, change_date FROM words WHERE word = :word LIMIT 1")
    DatesTuple loadDatesByWord(String word);
}
