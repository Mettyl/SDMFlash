package com.sdm.sdmflash.db.dataTypes;

/**
 * enumerátor jazyků
 * Created by Dominik on 02.12.2017.
 */

public enum Language {
    CZ(1), EN(2);

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

    public Short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }
}
