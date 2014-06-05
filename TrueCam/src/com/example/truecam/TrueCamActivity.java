package com.example.truecam;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.truecam.utils.CameraManager;
import com.example.truecam.utils.MyCameraButtonAnimation;



public class TrueCamActivity extends Activity implements Button.OnClickListener {
    public static final String TAG = "mycamera";

    private ImageView ivShutter;
    private ImageView ivCameraSwitcher;
    private CameraManager cameraManager;
    private SurfaceView previewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.truecam_layout);

        ivShutter = (ImageView) findViewById(R.id.mc_shutter);
		// ivCameraSwitcher = (ImageView) findViewById(R.id.mc_facing_switcher);
        previewLayout = (SurfaceView) findViewById(R.id.mc_preview);

        ivShutter.setOnClickListener(this);
		// ivCameraSwitcher.setOnClickListener(this);

        cameraManager = new CameraManager(this, previewLayout, btAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //we need to reobtain the access of the camera hardware in on resume, in case the activity is killed by orientation change
        cameraManager.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //we need to release the camera hardware resource in on pause
        cameraManager.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mc_shutter:
                cameraManager.takePicture();

                break;
			// case R.id.mc_facing_switcher:
			// cameraManager.switchCamera();

		// break;
        }
    }



    private MyCameraButtonAnimation btAnimation = new MyCameraButtonAnimation() {
        @Override
        public void executeAnimation(Animation animation) {
            ivShutter.startAnimation(animation);
			// ivCameraSwitcher.startAnimation(animation);
        }
    };
    
     
}



//public class TrueCamActivity extends Activity {
//	public static final String TAG = "MyCameraApp";
//	private View mTrueCamView;
//	private View mControlView;
//	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	public static final int MEDIA_TYPE_IMAGE = 1;
//	public static final int MEDIA_TYPE_VIDEO = 2;
//	private File outFile;
//	private Uri fileUri;
//	private Camera mCamera;
//	private CameraManager cameraManager;
//	private SurfaceView previewLayout;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		Log.d(TAG, "Init truecam.... ");
//		super.onCreate(savedInstanceState);
//		mTrueCamView = LayoutInflater.from(this).inflate(
//				R.layout.truecam_layout, null);		
//		
//		previewLayout = (SurfaceView) findViewById(R.id.mc_preview);
//		
//		Button mTakePicBtn = (Button) mControlView
//				.findViewById(R.id.btn_take_pic);
//		mTakePicBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switch (v.getId()) {
//				case R.id.btn_take_pic: {
//					Log.d(TAG, "clicked button to launch camera");
//					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//					outFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//					fileUri = Uri.fromFile(outFile);
//					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//					startActivityForResult(intent,
//							CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//					break;
//				}
//				}
//			}
//
//		});
//		setContentView(mTrueCamView);
//		previewLayout = (SurfaceView) findViewById(R.id.mc_preview);
//
//	        // Create our Preview view and set it as the content of our activity.
//	        mPreview = new CameraPreview(this, mCamera);
//	        ((FrameLayout)mTrueCamView).addView(mPreview);
//	        ((FrameLayout)mTrueCamView).addView(mControlView);
//	}
//
//
//
//
//	/** Create a file Uri for saving an image or video */
//	private static Uri getOutputMediaFileUri(int type) {
//		return Uri.fromFile(getOutputMediaFile(type));
//	}
//
//	/** Create a File for saving an image or video */
//	private static File getOutputMediaFile(int type) {
//		// To be safe, you should check that the SDCard is mounted
//		// using Environment.getExternalStorageState() before doing this.
//
//		File mediaStorageDir = new File(
//				Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//				"MyCameraApp");
//		// This location works best if you want the created images to be shared
//		// between applications and persist after your app has been uninstalled.
//
//		// Create the storage directory if it does not exist
//		if (!mediaStorageDir.exists()) {
//			if (!mediaStorageDir.mkdirs()) {
//				Log.d(TAG, "failed to create directory");
//				return null;
//			}
//		}
//
//		// Create a media file name
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//				.format(new Date());
//		File mediaFile;
//		if (type == MEDIA_TYPE_IMAGE) {
//			mediaFile = new File(mediaStorageDir.getPath() + File.separator
//					+ "IMG_" + timeStamp + ".jpg");
//		} else if (type == MEDIA_TYPE_VIDEO) {
//			mediaFile = new File(mediaStorageDir.getPath() + File.separator
//					+ "VID_" + timeStamp + ".mp4");
//		} else {
//			return null;
//		}
//
//		return mediaFile;
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//			Log.d(TAG, "cam activity resulted");
//			if (resultCode == RESULT_OK) {
//				String filename = outFile.getAbsolutePath();
//				String fileEncryptName = filename + "-encrypt";
//				String fileDecryptName = filename + "-decrypt";
//				try {
//					EncrypteUtils.encrypt(filename, fileEncryptName);
//					EncrypteUtils.decrypt(fileEncryptName, fileDecryptName);
//				} catch (Exception e) {
//
//				}
//
//				Bitmap bitmap = BitmapFactory.decodeFile(fileDecryptName);
//				Checksum cksumObj = new Checksum();
//				String cks = cksumObj.create(filename);
//				Log.d(TAG, "checksum is: " + cks);
//				if (cksumObj.check(filename, cks) == 1) {
//					Matrix matrix = new Matrix();
//					matrix.postRotate(90);
//					Bitmap rotatedBitmap = Bitmap
//							.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//									bitmap.getHeight(), matrix, true);
//					mTrueCamView.setBackground(new BitmapDrawable(
//							getResources(), rotatedBitmap));
//					Toast.makeText(this, "Image saved to local directory",
//							Toast.LENGTH_LONG).show();
//				} else {
//					Log.d(TAG, "file corrupted");
//				}
//			} else if (resultCode == RESULT_CANCELED) {
//				// User cancelled the image capture
//			} else {
//				// Image capture failed, advise user
//			}
//		}
//	}
//
//	private boolean uploadImg(int imgId) {
//		return false;
//	}
//

//}
