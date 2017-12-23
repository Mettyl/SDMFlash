package com.sdm.sdmflash.db.dataTypes;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Třída pro převádění neprimitivních typů pro SQLite databázi
 * Created by Dominik on 02.12.2017.
 */

public class Converters {
    //Datum
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    //Language
    @TypeConverter
    public static Language fromLanguage(Short id) {
        return id == 0 ? null : Language.findById(id);
    }

    @TypeConverter
    public static Short languageToShort(Language language) {
        return language == null ? null : language.getId();
    }

    //File
    @TypeConverter
    public static WordFile fromFile(Byte id) {
        return id == 0 ? null : WordFile.findById(id);
    }

    @TypeConverter
    public static Byte fileToByte(WordFile file) {
        return file == null ? null : file.getId();
    }
}
