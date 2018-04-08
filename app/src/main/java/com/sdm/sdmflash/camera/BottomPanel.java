package com.sdm.sdmflash.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sdm.sdmflash.R;

import io.reactivex.annotations.Nullable;

/**
 * Created by Dominik on 07.04.2018.
 */

public class BottomPanel extends LinearLayout {

    private ImageView captureButton;

    public BottomPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.bottom_panel, this);

        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchCapture(v, event);
            }
        });

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BottomPanel,
                    0, 0);

            try {
                /*cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1);
                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1);*/
            } finally {
                a.recycle();
            }
        }
    }

    boolean onTouchCapture(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_UP: {
//                cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
//                    @Override
//                    public void callback(CameraKitImage event) {
//                        imageCaptured(event);
//                    }
//                });
                Log.d("debug", "onTouchCapture: capture");
                break;
            }
        }
        return true;
    }

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

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }
}
