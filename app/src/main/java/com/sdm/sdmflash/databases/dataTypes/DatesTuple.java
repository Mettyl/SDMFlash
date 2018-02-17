package com.sdm.sdmflash.databases.dataTypes;

import android.arch.persistence.room.ColumnInfo;

import java.util.Date;

/**
 * Třída reprezentující dvojici add_date a change_date.
 * Created by Dominik on 07.12.2017.
 */

public class DatesTuple {

    @ColumnInfo(name = "add_date")
    public Date add_date;

    @ColumnInfo(name = "change_date")
    public Date change_date;

}
