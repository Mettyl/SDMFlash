package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by mety on 10.2.18.
 */

@Dao
public interface CzWordDao {

    @Query("SELECT * FROM czWords")
    List<CzWord> getAll();


    @Query("SELECT * FROM czWords WHERE word LIKE :param")
    List<CzWord> findWord(String param);


    @Query("SELECT id FROM czWords WHERE word = :word")
    int findByWord(String word);

    /**
     * vloží slovíčka
     *
     * @param words Objekty string
     */
    @Insert
    void insertAll(CzWord... words);

    /**
     * maže slovíčka
     *
     * @param word Slovíčko ke smazání
     */
    @Delete
    void delete(CzWord word);

}
