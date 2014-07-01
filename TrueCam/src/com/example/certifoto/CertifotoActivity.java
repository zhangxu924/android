package com.example.certifoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;



public class CertifotoActivity extends Activity implements
		DemoCameraFragment.Contract {
    public static final String TAG = "mycamera";

    private ImageView ivShutter;
	private ImageView ivCameraSwitcher;
    private CameraManager cameraManager;
    private SurfaceView previewLayout;
	private DemoCameraFragment current = null;
	private boolean singleShot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.certifoto_layout);
		// ivCameraSwitcher = (ImageView) findViewById(R.id.mc_facing_switcher);
		current = DemoCameraFragment.newInstance(false);
		getFragmentManager().beginTransaction()
				.replace(R.id.container, current).commit();
		// cameraManager = new CameraManager(this, previewLayout, btAnimation);
    }


	//
	//
	// private MyCameraButtonAnimation btAnimation = new
	// MyCameraButtonAnimation() {
	// @Override
	// public void executeAnimation(Animation animation) {
	// ivShutter.startAnimation(animation);
	// ivCameraSwitcher.startAnimation(animation);
	// }
	// };

	@Override
	public boolean isSingleShotMode() {
		return (singleShot);
	}

	@Override
	public void setSingleShotMode(boolean mode) {
		singleShot = mode;
	}
}
