package com.sdm.sdmflash.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.sdm.sdmflash.databases.dataTypes.Language;

import static com.sdm.sdmflash.MainActivity.TAG;

/**
 * Created by Dominik on 07.04.2018.
 */

public class CameraWorker {

    private Handler handler;
    private HandlerThread workerThread;

    private Handler UIHandler;
    private OCR ocr;
    private Bitmap currentImage;
    private CameraLanguage currentLanguage;

    public static final int CAPTURE_REQUEST = 1;
    public static final int IMAGE_SANDED = 2;
    public static final int BOUNDING_RECT = 3;
    public static final int STRING_OUTPUT = 4;

    /*private final Runnable ASKER_TASK = new Runnable() {
        @Override
        public void run() {
            while (askerRuning){
                Log.d(TAG, "run: AskerLoop");
                if (true){
                    ocrBusy = true;
                    UIHandler.sendEmptyMessage(CAPTURE_REQUEST);
                }
                synchronized (asker){
                    try {
                        asker.wait();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };*/

    public CameraWorker(String name, int priority) {
        ocr = new OCR();
        currentLanguage = CameraLanguage.EN;
        /*workerThread = new HandlerThread(name, priority){
            @Override
            protected void onLooperPrepared() {

                super.onLooperPrepared();

                if (!asker.isAlive() && UIHandler != null) {
                    askerRuning = true;
                    asker.start();
                }

            }
        }
        workerThread.start();*/
    }

    public void initUIHandler(Handler UIHandler){
        this.UIHandler = UIHandler;
        //askerRuning = true;

        /*if (!asker.isAlive()){
            askerRuning = true;
            asker.start();
        }*/
    }

    public synchronized void start() {
        if (workerThread == null){
            workerThread = new HandlerThread("CameraBackground");
            workerThread.start();

            handler = new Handler(workerThread.getLooper()){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case IMAGE_SANDED:
                            if (msg.obj != null) {
                                currentImage = (Bitmap) msg.obj;
                                Rect rect = ocr.getBoundingRect();
                                if (rect != null)
                                    try {
                                        if (currentImage.getWidth() > currentImage.getHeight()){
                                            currentImage = rotateBitmap(currentImage, 90);
                                            currentImage = cropBitmap(currentImage, rect);
                                        } else
                                            currentImage = cropBitmap(currentImage, rect);
                                        String text = ocr.getText(currentImage);
                                        Log.d("debug", "handleMessage: " + text);
                                        Message msg1 = Message.obtain();
                                        msg1.what = STRING_OUTPUT;
                                        msg1.obj = text;
                                        UIHandler.sendMessage(msg1);
//                                        synchronized (asker){
//                                            ocrBusy = false;
//                                            asker.notify();
//                                        }
                                    }catch (IllegalArgumentException e){
                                        Log.d(TAG, "handleMessage: bounding rect out of bitmap");
                                    }
                            }
                            break;
                        case BOUNDING_RECT:
                            if (msg.obj != null)
                                ocr.setBoundingRect((Rect)msg.obj);
                    }
                }
            };
        }
//        askerRuning = true;
//        asker = new Thread(ASKER_TASK, "asker_thread");
//        if (!asker.isAlive() && UIHandler != null)
//        asker.start();
        ocr.start();

        /*askerRuning = true;
        asker = new Thread(ASKER_TASK);
        asker.start();*/
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap cropBitmap(Bitmap source, Rect boundingRect){
        return Bitmap.createBitmap(source, boundingRect.left, boundingRect.top, boundingRect.width(), boundingRect.height());
    }

    public synchronized void pause(){
//        askerRuning = false;
//        asker.interrupt();
//        asker = null;
        try {
            /*askerRuning = false;
            asker.join();
            asker = null;*/
            ocr.pause();

            workerThread.quit();
            //workerThread.join();
            workerThread = null;
            handler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentLanguage(CameraLanguage currentLanguage) {
        this.currentLanguage = currentLanguage;

    }

    public CameraLanguage getCurrentLanguage() {
        return currentLanguage;
    }

    public HandlerThread getWorkerThread() {
        return workerThread;
    }

    public Handler getHandler() {
        return handler;

    }
}
