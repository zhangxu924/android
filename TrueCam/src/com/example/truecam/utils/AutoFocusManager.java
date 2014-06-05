package com.example.truecam.utils;

import com.example.truecam.TrueCamActivity;

import android.hardware.Camera;
import android.util.Log;


public class AutoFocusManager extends Thread implements Camera.AutoFocusCallback {
    private static final String TAG = TrueCamActivity.TAG;
    private static final long AUTO_FOCUS_INTERVAL_MS = 5000L;
    private Camera camera;
    private boolean isActive = false;

    public AutoFocusManager(Camera camera) {
        this.camera = camera;
    }
 
    public synchronized void startAutoFocus() {
        if (!isActive) {
            isActive = true;
            start();

         //   Log.d(TAG, "start auto focus");
        }
    }

    public synchronized void stopAutoFocus() {
        if (null != camera) {
            camera.cancelAutoFocus();
        }
        isActive = false;
       // Log.d(TAG, "stop auto focus");
    }

    public void autoFocus() {
        camera.autoFocus(this);
    }

    @Override
    public void run() {
        if (null == camera) {
            return;
        }

        while (isActive) {
            try {
                autoFocus();
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        if (b) {
           // Log.i(TAG, "auto focus successfully");
        } else {
           // Log.i(TAG, "auto focus failed");
        }
    }
}
