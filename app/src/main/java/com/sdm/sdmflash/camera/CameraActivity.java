package com.sdm.sdmflash.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Contacts;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;
    private CameraWorkerThread workerThread;
    private Bitmap currentImage;
    private TextView ocrOutput;

    private Handler UIHandler;

    private CameraKitEventCallback<CameraKitImage> captureEvent = new CameraKitEventCallback<CameraKitImage>() {
        @Override
        public void callback(CameraKitImage cameraKitImage) {
            currentImage = cameraKitImage.getBitmap();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera_view);
        ocrOutput = findViewById(R.id.ocrOutput);

        UIHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case CameraWorkerThread.CAPTURE_REQUEST:
                        if (cameraView.isStarted())cameraView.captureImage(captureEvent);

                        Message message = obtainMessage();
                        message.what = CameraWorkerThread.IMAGE_SANDED;
                        message.obj = currentImage;
                        if (workerThread.isAlive())
                            workerThread.getHandler().sendMessage(message);
                        break;
                    case CameraWorkerThread.STRING_OUTPUT:
                        ocrOutput.setText((String)msg.obj);
                        break;
                }
            }
        };
        workerThread = new CameraWorkerThread("Camera_worker_thread", CameraWorkerThread.NORM_PRIORITY, UIHandler);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        View rectangle = findViewById(R.id.textRestrict);
                        Rect rect = new Rect();
                        rectangle.getGlobalVisibleRect(rect);
                        Message msg = Message.obtain();
                        msg.what = CameraWorkerThread.BOUNDING_RECT;
                        msg.obj = rect;
                        workerThread.getHandler().sendMessage(msg);

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        workerThread.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        //mělo by být quit safely
        workerThread.pause();
        super.onPause();
    }
}
