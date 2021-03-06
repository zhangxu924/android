package com.example.certifoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.example.certifoto.utils.ChecksumUtils;
import com.example.certifoto.utils.HttpsUtils;


public class CameraManager {
    private static final String TAG = CertifotoActivity.TAG;

    private Camera camera;


    private int facing;
	private CertifotoActivity mCertifotoActivity;
    private CameraConfiguration configuration;
    private CameraPreview cameraPreview;
    private MyOrientationEventListener orientationEventListener;
    private AutoFocusManager autoFocusManager;


    private int lastAutofocusOrientation = 0;


    private int lastBtOrientation = 0;

    private MyCameraButtonAnimation buttonAnimation;

    private SurfaceView surfaceView;
    
    private String pictureFileName;

    public CameraManager(CertifotoActivity cameraActivity, SurfaceView surfaceView, MyCameraButtonAnimation buttonAnimation) {
		mCertifotoActivity = cameraActivity;
        configuration = new CameraConfiguration(cameraActivity);
        facing = Camera.CameraInfo.CAMERA_FACING_BACK;
        this.surfaceView = surfaceView;
        cameraPreview = new CameraPreview();
        orientationEventListener = new MyOrientationEventListener(cameraActivity);
        this.buttonAnimation = buttonAnimation;
    }

    public Camera openCamera() {
        releaseCamera();
        Log.d(TAG, "open the " + getFacingDesc(facing) + " camera");

        if (facing != Camera.CameraInfo.CAMERA_FACING_BACK && facing != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Log.w(TAG, "invalid facing " + facing + ", use default CAMERA_FACING_BACK");
            facing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == facing) {
                break;
            }
            ++index;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
        } else {
            camera = Camera.open(0);
        }

        configuration.config(camera);
        cameraPreview.startPreview();
		orientationEventListener.enable();

        return camera;
    }


    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }

    public void switchCamera() {
        if (Camera.CameraInfo.CAMERA_FACING_BACK == facing) {
            facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            facing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        releaseCamera();
        openCamera();
        cameraPreview.startPreview();
    }

    public void releaseCamera() {
        orientationEventListener.disable();

        if (null != camera) {
            cameraPreview.stopPreview();
            camera.release();
            camera = null;
        }
    }

	private class PicSaveTask extends AsyncTask<byte[], Void, HttpResponse> {
        @Override
		protected HttpResponse doInBackground(byte[]... datas) {
            byte[] data = datas[0];
            File pictureFile = null;
			File encrytedFile = null;
            try {
				pictureFile = configuration.getPictureStorageFile();
				encrytedFile = configuration.getOutputMediaFile();
            } catch (Exception e) {
                Log.e(TAG, "failed to create file: " + e.getMessage());
            }

            try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				// ChecksumUtils cksum = new ChecksumUtils();
				// String cks = cksum.create(data);
				// EncrypteUtils.encrypt(data, encrytedFile.getAbsolutePath());
				// EncrypteUtils.decrypt(encrytedFile.getAbsolutePath(),
				// pictureFile.getAbsolutePath());
//					Log.d(TAG, "checksum is: " + cks);
//					if (cksum.check(pictureFile.getAbsolutePath(), cks) == 1) {
//					Log.d(TAG, "file is still valid after decryption");
				// } else {
				// Log.d(TAG, "file is currupted");
				// }
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(pictureFile);
                mediaScanIntent.setData(contentUri);
				mCertifotoActivity.sendBroadcast(mediaScanIntent);
				pictureFileName = pictureFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
			} catch (Exception e) {
				Log.d(TAG, "Error encrypting file: " + e.getMessage());
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			ChecksumUtils cksum = new ChecksumUtils();
			String cks = cksum.create(data);
			HttpClient httpclient = HttpsUtils.getClient(mCertifotoActivity);
			HttpPost httpPost = new HttpPost("https://112.126.68.2:3001/record");
			httpPost.addHeader("Content-Type", "application/json");
			// List<NameValuePair> postParams = new
			// ArrayList<NameValuePair>();
			String jsonParamsStr = HttpsUtils.buildJsonParams(
					mCertifotoActivity, cks).toString();
			// postParams.add(new BasicNameValuePair("data",
			// jsonParamsStr));
			try {
				// UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
				// postParams);
				// entity.setContentEncoding(HTTP.UTF_8);
				httpPost.setEntity(new StringEntity(jsonParamsStr));
				Log.d(TAG, "ready to send https post request");
				httpResponse = httpclient.execute(httpPost);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return httpResponse;
        }


        @Override
		protected void onPostExecute(HttpResponse httpResponse) {
            super.onPostExecute(httpResponse);
			// releaseCamera();
			// mCertifotoActivity.findViewById(R.id.mc_progressbar).setVisibility(
			// View.INVISIBLE);
			Log.d(TAG, "the picture saved in " + pictureFileName);
			Log.d(TAG, "sending https post request finished");
			if (httpResponse == null) {
				Log.d("RESULT", "httpresponse is null");
			} else {
				try {
					InputStream inputStream = httpResponse.getEntity()
							.getContent();

					if (inputStream != null) {
						String mResponse = HttpsUtils
								.convertInputStreamToString(inputStream);
						Intent showResultIntent = new Intent(
								mCertifotoActivity, ResultPicActivity.class);
						Log.d(TAG, "httpresponse: " + mResponse);
						showResultIntent.putExtra(Constants.EXTRA_PIC_FILE,
								pictureFileName);
						showResultIntent.putExtra(Constants.EXTRA_RESPONSE,
								mResponse);
						showResultIntent
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						showResultIntent
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						mCertifotoActivity.startActivity(showResultIntent);
					} else {
						String mResponse = "Did not work!";
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
        }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mCertifotoActivity.findViewById(R.id.mc_progressbar).setVisibility(
			// View.VISIBLE);
		}
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (null == data || data.length == 0) {
				Toast.makeText(mCertifotoActivity, "failed taking picture",
						Toast.LENGTH_SHORT).show();

                Log.e(TAG, "No media data returned");
                return;
            }

            new PicSaveTask().execute(data);
			Toast.makeText(mCertifotoActivity, "processing picture",
					Toast.LENGTH_SHORT).show();
			camera.startPreview();

        }
    };

    private class MyOrientationEventListener extends OrientationEventListener {

        public MyOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;


            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(facing, info);
            int fixedOrientation = (orientation + 45) / 90 * 90;

            int rotation = 0;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - fixedOrientation + 360) % 360;
            } else {  // back-facing camera
                rotation = (info.orientation + fixedOrientation) % 360;
            }
            if (camera!=null){
            Camera.Parameters cameraParameters = camera.getParameters();
            cameraParameters.setRotation(rotation);
            camera.setParameters(cameraParameters);
            }


            if (orientation > 180 && lastAutofocusOrientation == 0) {
                lastAutofocusOrientation = 360;
            }
            if (Math.abs(orientation - lastAutofocusOrientation) > 30) {
                Log.d(TAG, "orientation=" + orientation + ", lastAutofocusOrientation=" + lastAutofocusOrientation);

                autoFocusManager.autoFocus();
                lastAutofocusOrientation = orientation;
            }



            int phoneRotation = 0;
            if (orientation > 315 && orientation <= 45) {
                phoneRotation = 0;
            } else if (orientation > 45 && orientation <= 135) {
                phoneRotation = 90;
            } else if (orientation > 135 && orientation <= 225) {
                phoneRotation = 180;
            } else if (orientation > 225 && orientation <= 315) {
                phoneRotation = 270;
            }


            if (phoneRotation == 0 && lastBtOrientation == 360) {
                lastBtOrientation = 0;
            }


            if ((phoneRotation == 0 || lastBtOrientation == 0) && (Math.abs(phoneRotation - lastBtOrientation) > 180)) {
                phoneRotation = phoneRotation == 0 ? 360 : phoneRotation;
                lastBtOrientation = lastBtOrientation == 0 ? 360 : lastBtOrientation;
            }

            if (phoneRotation != lastBtOrientation) {
                int fromDegress = 360 - lastBtOrientation;
                int toDegrees = 360 - phoneRotation;

                Log.i(TAG, "fromDegress=" + fromDegress + ", toDegrees=" + toDegrees);

                RotateAnimation animation = new RotateAnimation(fromDegress, toDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                buttonAnimation.executeAnimation(animation);
                lastBtOrientation = phoneRotation;
            }
        }
    }

    private static String getFacingDesc(int facing) {
        if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return "front";
        } else {
            return "back";
        }
    }


    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(facing, info);
		int rotation = mCertifotoActivity.getWindowManager()
				.getDefaultDisplay().getRotation();

        Log.d(TAG, "[1492]rotation=" + rotation);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Log.d(TAG, "[1492]info.orientation=" + info.orientation + ", degrees=" + degrees);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Log.d(TAG, "[1492]setCameraDisplayOrientation, result=" + result);

        camera.setDisplayOrientation(result);
    }


    private class CameraPreview implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        public CameraPreview() {
            mHolder = surfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void startPreview() {
            try {
                camera.setPreviewDisplay(mHolder);
                camera.setDisplayOrientation(90);
                camera.startPreview();

                autoFocusManager = new AutoFocusManager(camera);
                autoFocusManager.startAutoFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stopPreview() {
            try {
                camera.stopPreview();
                autoFocusManager.stopAutoFocus();
            } catch (Exception e) {

            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }

        @Override
		public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
				int width, int height) {
            if (surfaceHolder.getSurface() == null) {
                return;
            }
            stopPreview();
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    }


}
