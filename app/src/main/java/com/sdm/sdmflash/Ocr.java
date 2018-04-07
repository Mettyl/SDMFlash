package com.sdm.sdmflash;

/**
 * Created by Dominik on 30.03.2018.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.sdm.sdmflash.app.App;
import com.sdm.sdmflash.cameraFragment.CameraFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Ocr extends HandlerThread{

    private TessBaseAPI tessBaseAPI;
    private final String TAG = "debug";
    private String dataPath = "";

    private static Handler handler;

    public static final int IMAGE_REQUEST = 0;
    public static final int IMAGE_OBJ = 1;
    private boolean isRunning = false;

    private Image currentImage;
    private Thread asker;

    public Ocr(String name, ImageReader mPreviewImageReader) {
        super(name);
        //this.mHandler = new Handler(getLooper());
        /*mPreviewImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                isRunning = true;
            }
        }, null);*/

        /*dataPath = getFilesDir()+ "/tesseract/";

        //make sure training data has been copied
        checkFile(new File(dataPath + "tessdata/"));

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(image);

        //pokud zařízení nemá kameru
        if (!hasCamera()){

        }else
            launchCamera();

        //startOCR(image);*/


    }

    /*public void setUpImageReader(ImageReader previewImageReader){
        mPreviewImageReader = previewImageReader;
        mPreviewImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                if (!isRunning){
                    start();
                }
            }
        }, null);
    }*/

    @Override
    public void run() {
        super.run();

    }

    public synchronized Bitmap askForBitmap(){
        ByteBuffer buffer = currentImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        handler = new Handler(getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case IMAGE_OBJ:
                        currentImage = (Image) msg.obj;
                }
            }
        };

        asker = new Thread("askerThread"){
            @Override
            public void run() {
                super.run();
                /*while (true){
                    Log.v(TAG, "asker-run");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
//                while (isRunning && currentImage != null){
                while (true){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (currentImage != null)
                                Log.d(TAG, getText(askForBitmap()));
                        }
                    });
                    CameraFragment.UIHandler.sendEmptyMessage(IMAGE_REQUEST);
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asker.start();
    }

    public Handler getHandler() {
        return handler;
    }

    public void pause(){
        quitSafely();
        isRunning = false;
    }

    @Override
    public synchronized void start() {
        isRunning = true;
        //if (asker != null && !asker.isAlive())asker.start();
        super.start();
        /*while (!isInterrupted()){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getText(askForBitmap());
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }*/
    }

    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.init(dataPath,"eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = dataPath + "/tessdata/eng.traineddata";

            //get access to AssetManager

            //open byte streams for reading/writing
            InputStream instream = App.getContext().getAssets().open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = dataPath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

}

