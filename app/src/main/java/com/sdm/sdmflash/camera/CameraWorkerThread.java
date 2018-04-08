package com.sdm.sdmflash.camera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import static com.sdm.sdmflash.MainActivity.TAG;

/**
 * Created by Dominik on 07.04.2018.
 */

public class CameraWorkerThread extends HandlerThread{

    private Handler handler;
    private Handler UIHandler;
    private OCR ocr;
    private Thread asker;
    private boolean askerRuning;
    private Bitmap currentImage;
    private Bitmap currentCroppedImage;

    public static final int CAPTURE_REQUEST = 1;
    public static final int IMAGE_SANDED = 2;
    public static final int BOUNDING_RECT = 3;
    public static final int STRING_OUTPUT = 4;

    public CameraWorkerThread(String name, int priority, final Handler UIHandler) {
        super(name, priority);
        this.UIHandler = UIHandler;
        ocr = new OCR();
        asker = new Thread("asker_thread"){
            @Override
            public void run() {
                super.run();
                while (askerRuning){
                    UIHandler.sendEmptyMessage(CAPTURE_REQUEST);
                    if (currentCroppedImage != null){
                        String text = ocr.getText(currentCroppedImage);
                        Log.d("debug", "handleMessage: " + text);
                        Message msg = Message.obtain();
                        msg.what = STRING_OUTPUT;
                        msg.obj = text;
                        UIHandler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case IMAGE_SANDED:
                        if (msg.obj != null) {
                            currentImage = (Bitmap) msg.obj;
                            Rect rect = ocr.getBoundingRect();
                            if (rect != null)
                                try {
                                currentCroppedImage = Bitmap.createBitmap(currentImage, rect.left, rect.top, rect.width(), rect.height());
                                }catch (IllegalArgumentException e){
                                    Log.e(TAG, "handleMessage: bounding rect out of bitmap");
                                }
                        }
                        break;
                    case BOUNDING_RECT:
                        if (msg.obj != null)
                            ocr.setBoundingRect((Rect)msg.obj);
                }
            }
        };
        if (!asker.isAlive()) {
            askerRuning = true;
            asker.start();
        }
        //handler.sendEmptyMessage(CAMERA_READY);
    }

    @Override
    public synchronized void start() {
        if (!isAlive())
            super.start();
        if (!asker.isAlive()) {
            askerRuning = true;
            asker.start();
        }
        ocr.start();
    }

    public void pause(){
        quit();
        ocr.pause();
        askerRuning = false;
    }

    public Handler getHandler() {
        return handler;
    }
}
