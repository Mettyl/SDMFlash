package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Dominik on 13.01.2018.
 */

@Entity(tableName = "sources")
public class Source {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "source")
    private String source;

    public Source(int id, String source) {
        this.id = id;
        this.source = source;
    }

    @Ignore
    public Source(String source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return source;
    }
}
