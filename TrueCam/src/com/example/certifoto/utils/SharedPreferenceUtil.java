package com.example.certifoto.utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil {
	private static final String PREF_LOGIN_TOKEN = "pref_login_token";

	public static String getLoginToken(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(PREF_LOGIN_TOKEN, null);
	}

	public static void saveLoginToken(Context context, String token) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(PREF_LOGIN_TOKEN, token).commit();
	}
}
