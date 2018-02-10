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
public interface EnCzJoinDao {
    @Query("SELECT * FROM enWords_czWords_join")
    List<EnCzJoin> getAll();

    @Query("SELECT * FROM czWords INNER JOIN enWords_czWords_join ON czWords.id = enWords_czWords_join.czWordId WHERE enWords_czWords_join.enWordId = :enWordId")
    List<CzWord> translateToCz(int enWordId);

    @Query("SELECT * FROM enWords INNER JOIN enWords_czWords_join ON enWords.id = enWords_czWords_join.enWordId WHERE enWords_czWords_join.czWordId = :czWordId")
    List<EnWord> translateToEn(int czWordId);

    @Insert
    void insert(EnCzJoin... joins);

    @Delete
    void delete(EnCzJoin... joins);

}
