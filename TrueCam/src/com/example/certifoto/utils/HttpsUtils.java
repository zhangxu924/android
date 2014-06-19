package com.example.certifoto.utils;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.certifoto.CertifotoActivity;



public class HttpsUtils {
	private static final String TAG = CertifotoActivity.TAG;

	


	public static HttpClient getClient(Context context) {
		BasicHttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		// schReg.register(new Scheme("http",
		// PlainSocketFactory.getSocketFactory(), 80));
		// schReg.register(new Scheme("https", SSLTrustAllSocketFactory
		// .getSocketFactory(), 443));
		schReg.register(new Scheme("https", SSLCustomSocketFactory
				.getSocketFactory(context), 443));
		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);
		return new DefaultHttpClient(connMgr, params);
	}

	public static JSONObject buildJsonParams(Context context, String md5) {
		JSONObject params = new JSONObject();
		JSONObject coordinates = new JSONObject();
		try {
			long takenTime=DeviceInfoUtils.getCurrentTime();
			params.put("takenTime", takenTime);
			String device = DeviceInfoUtils.getDeviceID(context);
			params.put("device", device);

			Location location = DeviceInfoUtils.getLocation(context);
			if (location != null) {
			coordinates.put("latitude", location.getLatitude());
			coordinates.put("longitude", location.getLongitude());
			coordinates.put("altitude", location.getAltitude());
			coordinates.put("accuracy", location.getAccuracy());
			coordinates.put("heading", location.getBearing());
			coordinates.put("speed", location.getSpeed());
			coordinates.put("timestamp", location.getTime());
			params.put("coords", coordinates);
			} else {
				Log.d(TAG, "location is null");
			}
			params.put("md5", md5);
			Log.d(TAG, params.toString());
		} catch (JSONException ex) {
			Log.d(TAG, "error creating geolocation json object");
		}
		
		return params;
	}

	public static String convertInputStreamToString(InputStream is) {
		java.util.Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}


}
