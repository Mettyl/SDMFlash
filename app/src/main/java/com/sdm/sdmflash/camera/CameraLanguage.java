package com.sdm.sdmflash.camera;

import com.sdm.sdmflash.databases.dataTypes.Language;

public enum CameraLanguage {
    CZ(0), EN(1);

    private Short id;

    CameraLanguage(int id) {
        this.id = (short) id;
    }

    public static CameraLanguage findById(int id){
        switch (id){
            case 0:
                return CZ;
            case 1:
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

    public CameraLanguage nextLanguage(){
        return CameraLanguage.findById((id+1)%CameraLanguage.values().length);
    }
}
