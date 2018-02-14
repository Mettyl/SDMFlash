package com.sdm.sdmflash.databases.dataTypes;

/**
 * enumerátor kartorék
 * Created by Dominik on 02.12.2017.
 */

public enum WordFile {
    FILE_1(1), FILE_2(2), FILE_3(3), FILE_4(4), FILE_5(5);

    private byte id;
    public static final int NUM_OF_FILES = 5;

    WordFile(int id) {
        this.id = (byte)id;
    }

    public static WordFile findById(int id){
        switch (id){
            case 1:
                return FILE_1;
            case 2:
                return FILE_2;
            case 3:
                return FILE_3;
            case 4:
                return FILE_4;
            case 5:
                return FILE_5;
            default:
                return null;
        }
    }

    public Byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }
}
