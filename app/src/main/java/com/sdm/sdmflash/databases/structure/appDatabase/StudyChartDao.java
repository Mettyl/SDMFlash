package com.sdm.sdmflash.databases.structure.appDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StudyChartDao {

    @Query("SELECT * FROM study_chart_entries WHERE end_study_date > :time")
    List<StudyChartEntry> getFromWeek(long time);

    @Insert
    void insertAll(StudyChartEntry... entries);


    @Delete
    void delete(StudyChartEntry chartEntry);


    @Query("DELETE FROM study_chart_entries")
    void deleteAll();
}
