package com.sdm.sdmflash.databases.structure.appDatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "test_chart_entries")
public class TestChartEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "words_down")
    private int wordsDown;

    @ColumnInfo(name = "words_stayed")
    private int wordsStayed;

    @ColumnInfo(name = "words_up")
    private int wordsUp;

    @ColumnInfo(name = "start_test_date")
    private Date startTest;

    @ColumnInfo(name = "end_test_date")
    private Date endTest;


    public TestChartEntry(int wordsDown, int wordsStayed, int wordsUp, Date startTest, Date endTest) {
        this.wordsDown = wordsDown;
        this.wordsStayed = wordsStayed;
        this.wordsUp = wordsUp;
        this.startTest = startTest;
        this.endTest = endTest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWordsDown() {
        return wordsDown;
    }

    public void setWordsDown(int wordsDown) {
        this.wordsDown = wordsDown;
    }

    public int getWordsStayed() {
        return wordsStayed;
    }

    public void setWordsStayed(int wordsStayed) {
        this.wordsStayed = wordsStayed;
    }

    public int getWordsUp() {
        return wordsUp;
    }

    public void setWordsUp(int wordsUp) {
        this.wordsUp = wordsUp;
    }

    public Date getStartTest() {
        return startTest;
    }

    public void setStartTest(Date startTest) {
        this.startTest = startTest;
    }

    public Date getEndTest() {
        return endTest;
    }

    public void setEndTest(Date endTest) {
        this.endTest = endTest;
    }

}
