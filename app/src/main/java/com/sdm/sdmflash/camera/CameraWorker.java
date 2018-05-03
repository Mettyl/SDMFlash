package com.sdm.sdmflash.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import static com.sdm.sdmflash.MainActivity.TAG;

/**
 * Created by Dominik on 07.04.2018.
 * Třída, starající se o komunikaci s UIThread, správu OCR objektu a úpravu obrázků pro OCR
 */

public class CameraWorker {

    //Handlerer pracovního vlákna
    private Handler handler;
    //pracovní vlákno
    private HandlerThread workerThread;

    private Handler UIHandler;
    private OCR ocr;
    private Bitmap currentImage;
    //rozpoznávaný jazyk (pro možné budoucí použití)
    private CameraLanguage currentLanguage;

    //sada označení zpráv pro komunikaci mezi pracovním vláknem a UI vláknem
    public static final int CAPTURE_REQUEST = 1;
    public static final int IMAGE_SANDED = 2;
    public static final int BOUNDING_RECT = 3;
    public static final int STRING_OUTPUT = 4;

    /**
     * vytváří instatnci OCR (nutné)
     */
    public CameraWorker(String name, int priority) {
        ocr = new OCR();
        currentLanguage = CameraLanguage.EN;
    }

    public void initUIHandler(Handler UIHandler){
        this.UIHandler = UIHandler;
    }

    /**
     * voláno při onPause() aplikace
     * inicializuje vlákno, a komunikaci s UIThread
     */
    public synchronized void start() {
        //inicializace pracovního vlákna
        if (workerThread == null){
            workerThread = new HandlerThread("CameraBackground");
            workerThread.start();

            //komunikátor s UIThread
            handler = new Handler(workerThread.getLooper()){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        //pokud přišla vyfocená Bitmapa
                        case IMAGE_SANDED:
                            if (msg.obj != null) {
                                currentImage = (Bitmap) msg.obj;
                                Rect rect = ocr.getBoundingRect();
                                if (rect != null)
                                    try {
                                        //otočení pokud je na šířku a oříznutí podle šedého obdélníku v obrazu
                                        if (currentImage.getWidth() > currentImage.getHeight()){
                                            currentImage = rotateBitmap(currentImage, 90);
                                            currentImage = cropBitmap(currentImage, rect);
                                        } else
                                            currentImage = cropBitmap(currentImage, rect);
                                        //OCR obrázku
                                        String text = ocr.getText(currentImage);
                                        Log.d("debug", "handleMessage: " + text);
                                        Message msg1 = Message.obtain();
                                        msg1.what = STRING_OUTPUT;
                                        msg1.obj = text;
                                        //odeslání textu na UIThread
                                        UIHandler.sendMessage(msg1);
                                    }catch (IllegalArgumentException e){
                                        Log.d(TAG, "handleMessage: bounding rect out of bitmap");
                                    }
                            }
                            break;
                        //obdržení šedého rámečku
                        case BOUNDING_RECT:
                            if (msg.obj != null)
                                ocr.setBoundingRect((Rect)msg.obj);
                    }
                }
            };
        }
        ocr.start();
    }

    /**
     *
     * @return otočený obrázek
     */
    private static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     *
     * @return oříznutý obrázek
     */
    private static Bitmap cropBitmap(Bitmap source, Rect boundingRect){
        return Bitmap.createBitmap(source, boundingRect.left, boundingRect.top, boundingRect.width(), boundingRect.height());
    }

    /**
     * voláno při onPause() aplikace zastaví pracovní vlákno
     */
    public synchronized void pause(){
        try {
            ocr.pause();

            workerThread.quit();
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
