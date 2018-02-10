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
public interface EnWordDao {

    @Query("SELECT * FROM enWords")
    List<EnWord> getAll();

    @Query("SELECT * FROM enWords WHERE word LIKE :param")
    List<EnWord> findWord(String param);

    @Query("SELECT id FROM enWords WHERE word = :word")
    int findByWord(String word);

    /**
     * vloží slovíčka
     *
     * @param words Objekty string
     */
    @Insert
    void insertAll(EnWord... words);

    /**
     * maže slovíčka
     *
     * @param word Slovíčko ke smazání
     */
    @Delete
    void delete(EnWord word);


}
