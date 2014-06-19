package com.example.certifoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.certifoto.R;



public class CertifotoActivity extends Activity implements
		Button.OnClickListener {
    public static final String TAG = "mycamera";

    private ImageView ivShutter;
	// private ImageView ivCameraSwitcher;
    private CameraManager cameraManager;
    private SurfaceView previewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.certifoto_layout);

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
