package com.sdm.sdmflash.camera;
/**
 * Created by Dominik on 30.03.2018.
 * Třída starající se o správu OCR enginu a operace s ním
 */

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.sdm.sdmflash.app.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OCR {
    /**
     * OCR engine
     * zdroj funkce kopírující knihovnu: https://github.com/pethoalpar/AndroidTessTwoOCR
     */
    private TessBaseAPI tessBaseAPI;
    private final String TAG = "debug";
    private String dataPath = "";
    /**
     * jazyky načtené při startu aplikace
     */
    private final String[] LANGUAGES = {"ces", "eng"};

    /**
     * obdélník rozpoznávané části obrazu
     */
    private Rect boundingRect;

    /**
     * Je doporučeno vytvářet instanci OCR pouze jednou a to při startu aplikace,
     * jako součást CameraWorker objektu
     */
    public OCR() {
        //cesta k ocrApi uloženému v Assests folder
        dataPath = App.getContext().getFilesDir() + "/tesseract/";
        //make sure training data has been copied
        checkFile(new File(dataPath + "tessdata/"));
        //vytáření OCR
        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        //vytváření parametru pro inicializaci jazyků
        StringBuilder initLanguage = new StringBuilder();
        for (String language : LANGUAGES){
            initLanguage.append(language).append("+");
        }
        initLanguage.deleteCharAt(initLanguage.length()-1);
        //inicializace OCR
        tessBaseAPI.init(dataPath, initLanguage.toString());
    }

    public synchronized void start(){

    }

    public synchronized void pause(){

    }

    /**
     * Získává textový výstup z obrázku
     * @param bitmap obrázek ze kterého má být rozpoznán text
     * @return rozpoznaný text
     */
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

    /**
     * kopírování souboru s API z Assest složky do interní složky aplikace - tessdata
     * (podmínka pro spuštění API)
     * @param language
     */
    private void copyFiles(String language) {
        try {
            //cílová lokace
            String filepath = dataPath + "/tessdata/"+language+".traineddata";

            InputStream instream = App.getContext().getAssets().open("tessdata/"+language+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //kopírování souboru
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

    /**
     * kontrola zdali je API již překopírováno
     * @param dir
     */
    private void checkFile(File dir) {
        //složka neexistuje, vytvoříme a překopírujeme soubor pro oba jazyky
        if (!dir.exists() && dir.mkdirs()) {
            for (String language : LANGUAGES){
                copyFiles(language);
            }
        }
        //složka existuje ale je prázdná, překopírujeme soubory
        if (dir.exists()) {
            for (String language: LANGUAGES) {
                String datafilepath = dataPath + "/tessdata/"+language+".traineddata";
                File datafile = new File(datafilepath);
                if (!datafile.exists()) {
                    copyFiles(language);
                }
            }
        }
    }

    /**
     *
     * @return obdélník rozpoznávané části obrazu
     */
    public Rect getBoundingRect() {
        return boundingRect;
    }

    /**
     *
     * @param boundingRect obdélník rozpoznávané části obrazu
     */
    public void setBoundingRect(Rect boundingRect) {
        this.boundingRect = boundingRect;
    }
}