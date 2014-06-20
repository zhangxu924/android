package com.example.certifoto.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings.Secure;
import android.util.Log;

import com.example.certifoto.CertifotoActivity;

public class DeviceInfoUtils {
	private static final String TAG = CertifotoActivity.TAG;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
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
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		boolean isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isGPSEnabled && !isNetworkEnabled) {
			return null;
		} else {
			if (isNetworkEnabled) {
				if (locationManager != null) {
				location=locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Log.d(TAG, "Network location provider");
					
				}
			}
			if (isGPSEnabled) {
				if (location == null) {
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						Log.d("TAG", "GPS location provider");
					}
				}
			}

		}

		return location;
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}
}
