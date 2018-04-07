package com.sdm.sdmflash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sdm.sdmflash.cameraFragment.CameraFragment;

public class Camera2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }

}
