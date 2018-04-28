package com.sdm.sdmflash.databases.structure.dictionaryDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sdm.sdmflash.databases.structure.appDatabase.Word;

import java.util.List;

/**
 * Created by mety on 10.2.18.
 */

@Dao
public interface CzWordDao {

    /**
     * Vrací všechny slova z databáze
     *
     * @return List CzWord
     */
    @Query("SELECT * FROM czWords")
    List<CzWord> getAll();

    /**
     * Vrací všechny slova shodná nebo podobná
     * parametru
     * @param param lze zadat s % aby byla naleze slova podobná
     * @return List CzWord
     */
    @Query("SELECT word FROM czWords WHERE word LIKE :param ORDER BY LENGTH(word), word ASC LIMIT 0,:numberOfResults")
    List<String> findWord(String param, int numberOfResults);

    /**
     * Vrací id slova shodného s atributem
     * @param word hledané slovo
     * @return int id
     */
    @Query("SELECT id FROM czWords WHERE word = :word")
    int findIdByWord(String word);

    /**
     * Vrací id slova shodného s atributem
     *
     * @param id hledané id
     * @return CzWord slovo
     */
    @Query("SELECT * FROM czWords WHERE id = :id")
    CzWord findWordById(int id);


    /**
     * vloží slovíčka
     * @param words Objekty CzWord
     */
    @Insert
    void insertAll(CzWord... words);

    /**
     * maže slovíčka
     * @param word Slovíčko ke smazání
     */
    @Delete
    void delete(CzWord word);

}
