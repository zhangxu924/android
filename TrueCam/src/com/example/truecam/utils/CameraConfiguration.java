package com.example.truecam.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.example.truecam.TrueCamActivity;


public class CameraConfiguration {

    private static final String TAG = TrueCamActivity.TAG;

    private Camera.Parameters cameraParameters;

    private Camera camera;

    private DisplayMetrics mDisplayMetrics;

    private Context context;


    private static final int MIN_PREVIEW_PIXELS = 480 * 320;


    private static final double MAX_ASPECT_DISTORTION = 0.15;


    public CameraConfiguration(Context context) {
        this.context = context;
    }

    public void config(Camera camera) {
        this.camera = camera;
        this.cameraParameters = camera.getParameters();

        mDisplayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(mDisplayMetrics);
        Log.d(TAG, "screen resolution " + mDisplayMetrics.widthPixels + "*" + mDisplayMetrics.heightPixels);

        initCameraResolution();
        initFocusMode();
        initPictureResolution();
    }

   
    public void initCameraResolution() {
        Point cameraResolution = findBestPreviewResolution();

        Log.d(TAG, "set the preview size=" + cameraResolution);
		cameraParameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        camera.setParameters(cameraParameters);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterResolutions = afterParameters.getPreviewSize();
        if (afterResolutions != null && (cameraResolution.x != afterResolutions.width || cameraResolution.y != afterResolutions.height)) {
            Log.w(TAG, "Camera said it supported preview resolution " + cameraResolution.x + 'x' + cameraResolution.y
                    + ", but after setting it, preview resolution is " + afterResolutions.width + 'x' + afterResolutions.height);
        }
    }

    
    public void initFocusMode() {
        List<String> supportedFocusModes = cameraParameters.getSupportedFocusModes();
        String focusMode = findSettableValue(supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO);

        if (null == focusMode) {
            focusMode = findSettableValue(cameraParameters.getSupportedFocusModes(),
                    Camera.Parameters.FOCUS_MODE_MACRO,
                    Camera.Parameters.FOCUS_MODE_EDOF);
        }

        if (null != focusMode) {
            cameraParameters.setFocusMode(focusMode);
        } else {
            cameraParameters.setFocusMode(supportedFocusModes.get(0));
        }
        camera.setParameters(cameraParameters);
    }

    

    public void initPictureResolution() {
        Point bestPictureResolution = findBestPictureResolution();
        cameraParameters.setPictureSize(bestPictureResolution.x, bestPictureResolution.y);
        Log.d(TAG, "set the picture resolution " + bestPictureResolution.x + "x" + bestPictureResolution.y);
        camera.setParameters(cameraParameters);

        Camera.Size afterPicResolution = camera.getParameters().getPictureSize();
        if (null != afterPicResolution &&
                (afterPicResolution.width != bestPictureResolution.x &&
                 afterPicResolution.height != bestPictureResolution.y)) {
            Log.w(TAG, "camera support the picture resolution " + bestPictureResolution.x
             + "x" + bestPictureResolution.y + ", but after set, the picture resolution is " +
            bestPictureResolution.x + "x" + bestPictureResolution.y);
        }
    }



    public File getPictureStorageFile() throws Exception {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            throw new Exception("no sdcard found or can't write in the sdcard!");
        }

        File pictureStoreDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        if (!pictureStoreDir.exists()) {
            if (!pictureStoreDir.mkdirs()) {
                throw new Exception("failed to create directory: " + pictureStoreDir.getAbsolutePath());
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File picture = new File(pictureStoreDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return picture;
    }




	/** Create a File for saving an image or video */
	public File getOutputMediaFile() throws Exception {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			throw new Exception("no sdcard found or can't write in the sdcard!");
		}
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".encrypt");
		return mediaFile;
	}

    private Point findBestPreviewResolution() {
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();
        Log.d(TAG, "camera default resolution " + defaultPreviewResolution.width + "x" + defaultPreviewResolution.height);

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            return new Point(defaultPreviewResolution.width, defaultPreviewResolution.height);
        }

        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }
        Log.v(TAG, "Supported preview resolutions: " + previewResolutionSb);



        double screenAspectRatio = (double) mDisplayMetrics.widthPixels / (double) mDisplayMetrics.heightPixels;
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;


            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }


            if (maybeFlippedWidth == mDisplayMetrics.widthPixels  && maybeFlippedHeight == mDisplayMetrics.heightPixels) {
                Point exactPoint = new Point(width, height);
                Log.d(TAG, "found preview resolution exactly matching screen resolutions: " + exactPoint);

                return exactPoint;
            }
        }


        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.d(TAG, "using largest suitable preview resolution: " + largestSize);
            return largestSize;
        }


        Point defaultResolution = new Point(defaultPreviewResolution.width, defaultPreviewResolution.height);
        Log.d(TAG, "No suitable preview resolutions, using default: " + defaultResolution);

        return defaultResolution;
    }


    private Point findBestPictureResolution() {
		List<Camera.Size> supportedPicResolutions = cameraParameters
				.getSupportedPictureSizes();

        StringBuilder picResolutionSb = new StringBuilder();
        for (Camera.Size supportedPicResolution : supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x').append(supportedPicResolution.height).append(" ");
        }
        Log.d(TAG, "Supported picture resolutions: " + picResolutionSb);

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();
        Log.d(TAG, "default picture resolution " + defaultPictureResolution.width + "x" + defaultPictureResolution.height);


        List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(supportedPicResolutions);
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });


        double screenAspectRatio = (double) mDisplayMetrics.widthPixels / (double) mDisplayMetrics.heightPixels;
        Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
        }

 
        if (!sortedSupportedPicResolutions.isEmpty()) {
            Camera.Size largestPreview = sortedSupportedPicResolutions.get(0);
            Point largestSize = new Point(largestPreview.width, largestPreview.height);
            Log.d(TAG, "using largest suitable picture resolution: " + largestSize);
            return largestSize;
        }


        Point defaultResolution = new Point(defaultPictureResolution.width, defaultPictureResolution.height);
        Log.d(TAG, "No suitable picture resolutions, using default: " + defaultResolution);

        return defaultResolution;
    }

    private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
        String result = null;
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    result = desiredValue;
                    break;
                }
            }
        }
        return result;
    }
}
