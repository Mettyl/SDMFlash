package com.sdm.sdmflash.db.structure;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by mety on 10.2.18.
 */

@Entity(
        tableName = "enWords_czWords_join",
        primaryKeys = {"enWordId", "czWordId"},
        foreignKeys = {
                @ForeignKey(
                        entity = EnWord.class,
                        parentColumns = "id",
                        childColumns = "enWordId",
                        onDelete = CASCADE),
                @ForeignKey(
                        entity = CzWord.class,
                        parentColumns = "id",
                        childColumns = "czWordId",
                        onDelete = CASCADE)}
)
public class EnCzJoin {

    private final int enWordId;
    private final int czWordId;

    public EnCzJoin(int enWordId, int czWordId) {
        this.enWordId = enWordId;
        this.czWordId = czWordId;
    }

    public int getEnWordId() {
        return enWordId;
    }

    public int getCzWordId() {
        return czWordId;
    }

}
