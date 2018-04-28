package com.sdm.sdmflash.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by Dominik on 02.04.2018.
 */

public class App extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}