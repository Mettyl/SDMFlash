package com.sdm.sdmflash.camera;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdm.sdmflash.MainActivity;
import com.sdm.sdmflash.R;
import com.sdm.sdmflash.camera.activities.CameraActivity;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

import io.reactivex.annotations.Nullable;

/**
 * Created by Dominik on 07.04.2018.
 * Spodní panel CameraView
 * Zdroj: https://github.com/CameraKit/camerakit-android
 */

public class BottomPanelLive extends LinearLayout {

    private ImageView captureButton;
    private ImageView checkButton;
    private ImageView flashButton;
    private CameraView cameraView;
    CameraActivity cameraActivity;

    public BottomPanelLive(Context context) {
        this(context, null);
    }

    public BottomPanelLive(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomPanelLive(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.bottom_panel_live, this);

        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchCapture(v, event);
            }
        });

        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchCheckButton(v, event);
            }
        });

        flashButton = findViewById(R.id.flashButton);
        flashButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchFlashButton(v, event);
            }
        });
    }

    /**
     * získání mateřské aplikace a cameraView
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        cameraActivity = (CameraActivity)getContext();
        View view = getRootView().findViewById(R.id.camera_view);
        if (view instanceof CameraView) {
            cameraView = (CameraView) view;
        }
    }

    /**
     * změna nastavení blesku
     * @param view
     * @param motionEvent
     * @return
     */
    boolean onTouchFlashButton(View view, MotionEvent motionEvent){
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                    changeViewImageResource((ImageView) view, R.drawable.ic_flash_on);
                } else {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    changeViewImageResource((ImageView) view, R.drawable.ic_flash_off);
                }

                break;
            }
        }
        return true;
    }

    /**
     * potvrzení textu
     * @param view
     * @param motionEvent
     * @return
     */
    boolean onTouchCheckButton(View view, MotionEvent motionEvent){
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_UP: {
                handleViewTouchFeedback(view, motionEvent);
                Intent intent = new Intent(cameraActivity, MainActivity.class);
                intent.putExtra(CameraActivity.CAMERA_OUTPUT, cameraActivity.getOcrOutput().getText());
                //start nové aktivity s předáním textu
                cameraActivity.startActivity(intent);
                Log.d("debug", "onTouchCheckButton");
                break;
            }
        }
        return true;
    }

    /**
     * požadavek na vyfocení odeslaný na UIThread
     * @param view
     * @param motionEvent
     * @return
     */
    boolean onTouchCapture(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_UP: {
                CameraActivity.getUIHandler().sendEmptyMessage(CameraWorker.CAPTURE_REQUEST);
                Log.d("debug", "onTouchCapture: capture");
                break;
            }
        }
        return true;
    }

    /**
     * spuštění animací stisknutí
     * @param view
     * @param motionEvent
     * @return
     */
    boolean handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    /**
     * animace stisknutí
     * @param view view které má být animováno
     */
    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /**
     * animace puštění
     * @param view view které má být animováno
     */
    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /**
     * animace rotace
     * @param view view které má být animováno
     */
    void rotation(View view) {
        view.setRotation(0);
        view.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    /**
     * animace se změnou ikony
     * @param imageView
     * @param resId
     */
    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }
}
