package com.sdm.sdmflash.camera.activities;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.sdm.sdmflash.R;
import com.sdm.sdmflash.app.App;
import com.sdm.sdmflash.camera.CameraLanguage;
import com.sdm.sdmflash.camera.CameraWorker;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

public class CameraActivity extends AppCompatActivity {

    private CameraView cameraView;
    private TextView ocrOutput;
    private Bitmap currentImage;

    private static CameraWorker worker;
    private static Handler UIHandler;

    public static final String CAMERA_OUTPUT = "CAMERA_OUTPUT";

    private CameraKitEventCallback<CameraKitImage> captureEvent = new CameraKitEventCallback<CameraKitImage>() {
        @Override
        public void callback(CameraKitImage cameraKitImage) {
            currentImage = cameraKitImage.getBitmap();
            Message message = worker.getHandler().obtainMessage();
            message.what = CameraWorker.IMAGE_SANDED;
            message.obj = currentImage;
            if (worker.getWorkerThread().isAlive())
                worker.getHandler().sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera_view);
        ocrOutput = findViewById(R.id.ocrOutput);

        UIHandler = new Handler(App.getContext().getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case CameraWorker.CAPTURE_REQUEST:
                        if (cameraView.isStarted()){
                            ocrOutput.setText("...");
                            cameraView.captureImage(captureEvent);
                        }
                        break;
                    case CameraWorker.STRING_OUTPUT:
                        ocrOutput.setText((String)msg.obj);
                }
            }
        };
        worker.initUIHandler(UIHandler);

        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        View rectangle = findViewById(R.id.textRestrict);
                        Rect rect = new Rect();
                        rectangle.getGlobalVisibleRect(rect);
                        Message msg = Message.obtain();
                        msg.what = CameraWorker.BOUNDING_RECT;
                        msg.obj = rect;
                        if (worker.getHandler() != null)
                            worker.getHandler().sendMessage(msg);

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        worker.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        worker.pause();
        super.onPause();
    }

    public static void setWorkerThread(CameraWorker worker) {
        CameraActivity.worker = worker;
    }

    public static CameraWorker getWorker() {
        return worker;
    }

    public static Handler getUIHandler() {
        return UIHandler;
    }

    public TextView getOcrOutput() {
        return ocrOutput;
    }
}
