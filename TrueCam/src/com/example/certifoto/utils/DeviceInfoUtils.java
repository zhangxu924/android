package com.example.certifoto.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.example.certifoto.CertifotoActivity;

public class DeviceInfoUtils {
	private static final String TAG = CertifotoActivity.TAG;
	public static String getDeviceID(Context context) {
		return Secure
				.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
	}

	public static Location getLocation(Context context) {
		// JSONObject coordinates = new JSONObject();
		Location location = null;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		if (provider == null) {
			Intent settingsIntent = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(settingsIntent);
		} else {
			location = locationManager.getLastKnownLocation(provider);
		}
		return location;
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}
}
