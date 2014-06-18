package com.example.certifoto.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.util.Log;


public class SSLTrustAllSocketFactory extends SSLSocketFactory {

	private static final String TAG = "SSLTrustAllSocketFactory";
	private SSLContext mSSLContext;

	public class SSLTrustAllManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	public SSLTrustAllSocketFactory(KeyStore truststore) throws Throwable {
		super(truststore);
		try {
			mSSLContext = SSLContext.getInstance("TLS");
			mSSLContext.init(null,
					new TrustManager[] { new SSLTrustAllManager() }, null);
			setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception ex) {
		}
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return mSSLContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return mSSLContext.getSocketFactory().createSocket();
	}

	public static SSLSocketFactory getSocketFactory() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory factory = new SSLTrustAllSocketFactory(trustStore);
			return factory;
		} catch (Throwable e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}