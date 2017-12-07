package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.sdm.sdmflash.db.dataTypes.File;
import com.sdm.sdmflash.db.dataTypes.Language;

import java.util.Date;

/**
 * Třída reprezentuje tabulku "words"
 * Každá proměná značí jeden sloupec v tabulce
 * Created by Dominik on 02.12.2017.
 */

@Entity(tableName = "words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "language")
    private Language language;

    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "source")
    private String source;

    @ColumnInfo(name = "add_date")
    private Date add_date;

    @ColumnInfo(name = "change_date")
    private Date change_date;

    //Kartotéka
    @ColumnInfo(name = "file")
    private File file;

    //Constructor
    public Word(Language language, String word, String translation,
                String source, Date add_date, Date change_date,
                File file) {
        this.language = language;
        this.word = word;
        this.translation = translation;
        this.source = source;
        this.add_date = add_date;
        this.change_date = change_date;
        this.file = file;
    }

    //getters and setters (nutné pro správné fungování ROOM!!!)
    public int getId() {
        return id;
    }

    public Language getLanguage() {
        return language;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public String getSource() {
        return source;
    }

    public Date getAdd_date() {
        return add_date;
    }

    public Date getChange_date() {
        return change_date;
    }

    public File getFile() {
        return file;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setAdd_date(Date add_date) {
        this.add_date = add_date;
    }

    public void setChange_date(Date change_date) {
        this.change_date = change_date;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString(){
        return id + ", " + language + ", " +word + ", " +translation + ", " +source + ", " +add_date + ", " +change_date + ", " +file;
    }
}
