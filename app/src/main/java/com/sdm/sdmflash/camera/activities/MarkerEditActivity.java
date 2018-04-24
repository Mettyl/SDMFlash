package com.sdm.sdmflash.camera.activities;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.CameraWorker;

public class MarkerEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_edit);
        ImageView imageView = findViewById(R.id.image_display);

        //imageView.setImageBitmap((Bitmap)getIntent().getParcelableExtra(CameraActivity.IMAGE_ID));
    }
}
