package com.sdm.sdmflash.databases.structure.appDatabase;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "study_chart_entries")
public class StudyChartEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "start_study_date")
    private Date startStudy;

    @ColumnInfo(name = "end_study_date")
    private Date endStudy;


    public StudyChartEntry(Date startStudy, Date endStudy) {
        this.startStudy = startStudy;
        this.endStudy = endStudy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartStudy() {
        return startStudy;
    }

    public void setStartStudy(Date startStudy) {
        this.startStudy = startStudy;
    }

    public Date getEndStudy() {
        return endStudy;
    }

    public void setEndStudy(Date endStudy) {
        this.endStudy = endStudy;
    }

}
