package com.sdm.sdmflash.databases.structure;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by mety on 10.2.18.
 */

@Dao
public interface EnWordDao {

    /**
     * Vrací všechny slova z databáze
     *
     * @return List EnWord
     */
    @Query("SELECT * FROM enWords")
    List<EnWord> getAll();

    /**
     * Vrací všechny slova shodná nebo podobná
     * parametru
     * @param param lze zadat s %, aby byla naleze slova podobná
     * @return List EnWord
     */
    @Query("SELECT word FROM enWords WHERE word LIKE :param ORDER BY LENGTH(word), word ASC LIMIT 0,:numberOfResults")
    List<String> findWord(String param, int numberOfResults);

    /**
     * Vrací id slova shodného s atributem
     * @param word hledané slovo
     * @return int id
     */
    @Query("SELECT id FROM enWords WHERE word = :word")
    int findIdByWord(String word);

    /**
     * vloží slovíčka
     * @param words Objekty EnWord
     */
    @Insert
    void insertAll(EnWord... words);

    /**
     * maže slovíčka
     * @param word Slovíčko ke smazání
     */
    @Delete
    void delete(EnWord word);


}
