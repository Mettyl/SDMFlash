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
public interface EnCzJoinDao {

    /**
     * Vrací všechny relace v tabulce
     *
     * @return List EnCzJoin
     */
    @Query("SELECT * FROM enWords_czWords_join")
    List<EnCzJoin> getAll();

    /**
     * Vrací slova z CzWord tabulky které jsou v EnCzJoin
     * propjeny s id slova v EnWord tabulce
     *
     * @param enWordId int id slova z EnWord k přeložení
     * @return List CzWord vrátí překlad a jeho synonyma
     */
    @Query("SELECT word FROM czWords INNER JOIN enWords_czWords_join ON czWords.id = enWords_czWords_join.czWordId \n" +
            "WHERE enWords_czWords_join.enWordId LIKE :enWordId ORDER BY LENGTH(word), word")
    List<String> translateToCz(int enWordId);

    /**
     * Vrací slova z EnWord tabulky které jsou v EnCzJoin
     * propjeny s id slova v CzWord tabulce
     *
     * @param czWordId int id slova z CzWord k přeložení
     * @return List EnWord vrátí překlad a jeho synonyma
     */
    @Query("SELECT word FROM enWords INNER JOIN enWords_czWords_join ON enWords.id = enWords_czWords_join.enWordId \n" +
            "WHERE enWords_czWords_join.czWordId LIKE :czWordId ORDER BY LENGTH(word), word")
    List<String> translateToEn(int czWordId);

    /**
     * Vloží zadané relace
     * @param joins objekt EnCzJoin jako relace
     */
    @Insert
    void insert(EnCzJoin... joins);

    /**
     * Vymaže zadané relace
     * @param joins objekt EnCzJoin jako relace
     */
    @Delete
    void delete(EnCzJoin... joins);


}
