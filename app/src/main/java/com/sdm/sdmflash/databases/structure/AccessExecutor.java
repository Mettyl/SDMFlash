package com.sdm.sdmflash.databases.structure;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Třída která obstarává vytvoření nového vlákna. Všechny přístupu k databázi musejí být vedeny
 * skrze instanci této třídy.
 * Created by Dominik on 06.12.2017.
 */

public class AccessExecutor implements Executor {

    /**
     * Spustí runnable objekt v novém vlákně.
     * Všechny přístupu k databázi musejí být vedeny skrze tuto metodu!
     * @param runnable vniřní runnable třída obsahující kód pracující s databází
     */
    @Override
    public void execute(@NonNull Runnable runnable) {
        new Thread(runnable).start();
    }
}
