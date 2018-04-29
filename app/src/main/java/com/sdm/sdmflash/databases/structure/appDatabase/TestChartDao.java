package com.sdm.sdmflash.databases.structure.appDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TestChartDao {


    @Query("SELECT * FROM test_chart_entries WHERE DATE(end_test_date) >= DATE('now', 'weekday 0', '-7 days')")
    List<TestChartEntry> getFromWeek();

    @Insert
    void insertAll(TestChartEntry... entries);


    @Delete
    void delete(TestChartEntry chartEntry);


    @Query("DELETE FROM test_chart_entries")
    void deleteAll();
}
