package com.example.certifoto.utils;

import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;
import android.util.Log;

import com.example.certifoto.R;


public class SSLCustomSocketFactory extends SSLSocketFactory {
	private static final String TAG = "SSLCustomSocketFactory";

	private static final String KEY_PASS = "xzandbh";

	public SSLCustomSocketFactory(KeyStore trustStore) throws Throwable {
		super(trustStore);
	}

	public static SSLSocketFactory getSocketFactory(Context context) {
		try {
			InputStream ins = context.getResources().openRawResource(
					R.raw.certifoto);

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			try {
				trustStore.load(ins, KEY_PASS.toCharArray());
			} finally {
				ins.close();
			}
			SSLSocketFactory factory = new SSLCustomSocketFactory(trustStore);
			return factory;
		} catch (Throwable e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}