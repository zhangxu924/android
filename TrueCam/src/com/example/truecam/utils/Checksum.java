package com.example.truecam.utils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import android.util.Log;

public class Checksum {

	//
	// returns 0 error
	// 1 ok (create)
	// 1 same (check)
	// 2 different (check)
	//
	public String create(String filename) {
		try {
			byte[] chk = createChecksum(filename);
			return new String(chk);
		} catch (Exception e) {
			e.printStackTrace();
			return "create checksum failed";
		}
	}

	public String create(byte[] data) {
		try {
			byte[] chk = createChecksum(data);
			return new String(chk);
		} catch (Exception e) {
			e.printStackTrace();
			return "create checksum failed";
		}
	}

	public int check(String filename, String checksum) {
		int rc = 0;
		try {
			byte[] chk = createChecksum(filename);
			Log.d("MyCameraApp", "checksum is: " + new String(chk));
			if (new String(chk).equals(checksum)) {
				System.out.println("Same!");
				rc = 1;
			} else {
				System.out.println("Different!");
				rc = 2;
			}
			return rc;
		} catch (Exception e) {
			e.printStackTrace();
			return rc;
		}
	}

	public int check(byte[] data, String checksum) {
		int rc = 0;
		try {
			byte[] chk = createChecksum(data);
			Log.d("MyCameraApp", "checksum is: " + new String(chk));
			if (new String(chk).equals(checksum)) {
				System.out.println("Same!");
				rc = 1;
			} else {
				System.out.println("Different!");
				rc = 2;
			}
			return rc;
		} catch (Exception e) {
			e.printStackTrace();
			return rc;
		}
	}

	public byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public byte[] createChecksum(byte[] data) throws Exception {
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead = 0;
		while (numRead < data.length) {
			complete.update(data, numRead,
					Math.min(1024, data.length - numRead));
			numRead += 1024;
		}
		return complete.digest();
	}

}