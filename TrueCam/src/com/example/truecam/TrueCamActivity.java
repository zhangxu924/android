package com.example.truecam;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TrueCamActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_true_cam);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.true_cam, menu);
		return true;
	}

}
