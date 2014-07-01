package com.example.certifoto;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.certifoto.utils.SharedPreferenceUtil;

public class LoginActivity extends Activity {
	private WebView webView;

	public String getCookie(String siteName, String CookieName) {
		String CookieValue = null;

		CookieManager cookieManager = CookieManager.getInstance();
		String cookies = cookieManager.getCookie(siteName);
		if (cookies == null)
			return cookies;
		String[] temp = cookies.split("[;]");
		for (String ar1 : temp) {
			if (ar1.contains(CookieName)) {
				String[] temp1 = ar1.split("[=]");
				CookieValue = temp1[1];
			}
		}
		return CookieValue;
	}

	public boolean checkToken() {

		if (SharedPreferenceUtil.getLoginToken(this) != null) {
			// if (getCookie("https://112.126.68.2", "token") != null) {
			// Log.d("TAG", getCookie("https://112.126.68.2", "token"));
			launchCamera();
			return true;
		} else {
			Log.d("TAG", "pref token null");
			return false;
		}
	}

	public void launchCamera() {
		Intent CameraIntent = new Intent(LoginActivity.this,
				CertifotoActivity.class);
		CameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		CameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CameraIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(CameraIntent);
		finish();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!checkToken()) {
		setContentView(R.layout.login_layout);
		webView = (WebView) findViewById(R.id.loginView);
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.getSettings().setDefaultTextEncodingName("gb2312");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
					String token=getCookie("https://112.126.68.2", "token");
					if (token != null) {
						Log.d("TAG", token);
						SharedPreferenceUtil.saveLoginToken(LoginActivity.this,
								token);
						launchCamera();
					} else {
						Log.d("TAG", "cookies token null");
					}
				super.onPageFinished(view, url);
			}

		});
			webView.loadUrl("https://112.126.68.2:3004/samlLogin");
		}
	}
}
