package com.sdm.sdmflash.databases.structure.appDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Dominik on 13.01.2018.
 */

@Dao
public interface SourceDao {
    /**
     * Vrací celý obsah tabulky
     * @return List objektů {@link Source}
     */
    @Query("SELECT * FROM sources")
    List<Source> getAll();

    /**
     * Vrací zdroje podle id
     * @param sourceIds pole id
     * @return List objektů {@link Source}
     */
    @Query("SELECT * FROM sources WHERE id IN (:sourceIds)")
    List<Source> loadAllByIds(int[] sourceIds);

    /**
     * vloží zdroje
     * @param sources Objekty {@link Source}
     */
    @Insert
    void insertAll(Source... sources);

    /**
     * maže zdroje
     * @param source Zdroj ke smazání
     */
    @Delete
    void delete(Source source);

    /**
     * Vymaže obsah databáze (inkrementace id pokračuje od poslední hodnoty)
     */
    @Query("DELETE FROM sources")
    void deleteAll();

    /**
     *
     * @return všechny zdroje v textové podobě
     */
    @Query("SELECT source FROM sources")
    List<String> loadAllStringSources();


    @Query("SELECT id FROM sources WHERE source = :source")
    int findIdBySource(String source);
}
