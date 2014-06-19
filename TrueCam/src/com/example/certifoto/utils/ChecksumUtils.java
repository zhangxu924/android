package com.example.certifoto.utils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.util.Log;

public class ChecksumUtils {

	//
	// returns 0 error
	// 1 ok (create)
	// 1 same (check)
	// 2 different (check)
	//
	public String create(String filename) {
		try {
			byte[] chk = createChecksum(filename);
			BigInteger bigInt = new BigInteger(1,chk);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
			
			return hashtext;
		} catch (Exception e) {
			e.printStackTrace();
			return "create checksum failed";
		}
	}

	public String create(byte[] data) {
		try {
			byte[] chk = createChecksum(data);
			BigInteger bigInt = new BigInteger(1, chk);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext;
		} catch (Exception e) {
			e.printStackTrace();
			return "create checksum failed";
		}
	}

	public int check(String filename, String checksum) {
		int rc = 0;
		try {
			String chk = create(filename);
			Log.d("MyCameraApp", "checksum is: " + chk);
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
			String chk = create(data);
			Log.d("MyCameraApp", "checksum is: " + chk);
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