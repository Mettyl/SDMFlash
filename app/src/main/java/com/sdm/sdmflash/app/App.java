package com.sdm.sdmflash.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Dominik on 02.04.2018.
 */

public class App extends Application{
    private static Context context;
    public static final String DEBUG_TAG = "debug";
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        handler = new Handler(context.getMainLooper());
    }

    public static Context getContext() {
        return context;
    }
}