package com.sdm.sdmflash.db.dataTypes;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by Dominik on 17.12.2017.
 */

public class WordsTuple{

    public WordsTuple(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    @ColumnInfo(name = "word")
    public String word;

    @ColumnInfo(name = "translation")
    public String translation;

}