package com.sdm.sdmflash.camera;
/**
 * Created by Dominik on 30.03.2018.
 */

        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Rect;
        import android.media.Image;
        import android.media.ImageReader;
        import android.os.Handler;
        import android.os.HandlerThread;
        import android.os.Message;
        import android.util.Log;

        import com.googlecode.tesseract.android.TessBaseAPI;
        import com.sdm.sdmflash.app.App;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.nio.ByteBuffer;

public class OCR {

    private TessBaseAPI tessBaseAPI;
    private final String TAG = "debug";
    private String dataPath = "";

    private Rect boundingRect;

    public OCR() {
        dataPath = App.getContext().getFilesDir() + "/tesseract/";
        //make sure training data has been copied
        checkFile(new File(dataPath + "tessdata/"));
    }



//    public synchronized Bitmap askForBitmap(){

    //    }
    public void start(){
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.init(dataPath, "eng");
    }

    //        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    public void pause(){
        tessBaseAPI.end();
    }

    //        buffer.get(bytes);
    public String getText(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try {
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return retStr;
    }

    //        byte[] bytes = new byte[buffer.capacity()];
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

    //        ByteBuffer buffer = currentImage.getPlanes()[0].getBuffer();
    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = dataPath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    public Rect getBoundingRect() {
        return boundingRect;
    }

    public void setBoundingRect(Rect boundingRect) {
        this.boundingRect = boundingRect;
    }
}