package com.sdm.sdmflash.databases.dataTypes;

/**
 * enumerátor kartorék
 * Created by Dominik on 02.12.2017.
 */

public enum WordFile {
    file1(1), file2(2), file3(3), file4(4), file5(5);

    public static final int NUM_OF_FILES = 5;
    private byte id;

    WordFile(int id) {
        this.id = (byte)id;
    }

    public static WordFile findById(int id){
        switch (id){
            case 1:
                return file1;
            case 2:
                return file2;
            case 3:
                return file3;
            case 4:
                return file4;
            case 5:
                return file5;
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
