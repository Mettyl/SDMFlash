package com.sdm.sdmflash.databases.dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * enumerátor jazyků
 * Created by Dominik on 02.12.2017.
 */

public enum Language {
    CZ(1), EN(2);

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 2;

    private Short id;

    Language(int id) {
        this.id = (short) id;
    }

    public static Language findById(int id){
        switch (id){
            case 1:
                return CZ;
            case 2:
                return EN;
            default:
                return null;
        }
    }

    public static List<Language> getLanguagesList(){
        List<Language> list = new ArrayList<>();
        for (int i = MIN_VALUE; i <= MAX_VALUE; i++) {
            list.add(findById(i));
        }
        return list;
    }

    public Short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }
}
