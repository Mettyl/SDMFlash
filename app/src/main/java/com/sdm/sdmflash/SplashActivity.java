package com.sdm.sdmflash;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;

import com.sdm.sdmflash.camera.CameraWorker;
import com.sdm.sdmflash.camera.activities.CameraActivity;

/**
 * Aktivita, která se vytvoří jako první, načte potřebná data a spustí hlavní aktivitu
 * zdroj: https://android.jlelse.eu/right-way-to-create-splash-screen-on-android-e7f1709ba154
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //zde lze vkládat kód, který se načte před spuštěním aplikace
        ocrInit();
        // Start home activity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        // close splash activity
        finish();

    }

    /**
     * Vytváří a inicializuje OCR engine
     */
    private void ocrInit() {
        CameraActivity.setWorkerThread(new CameraWorker(
                "Camera_worker_thread",
                HandlerThread.NORM_PRIORITY));
    }

}