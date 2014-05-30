package com.example.truecam.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncrypteUtils {
	public static void encrypt(String fileInput, String fileOutput)
			throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		// Here you read the cleartext.
		FileInputStream fis = new FileInputStream(fileInput);
		// This stream write the encrypted text. This stream will be wrapped by
		// another stream.
		FileOutputStream fos = new FileOutputStream(fileOutput);

		// Length is 16 byte, the key here needs to be taken care of
		SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
				"AES");
		// Create cipher
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, sks);
		// Wrap the output stream
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);
		// Write bytes
		int b;
		byte[] d = new byte[2048];
		while ((b = fis.read(d)) != -1) {
			cos.write(d, 0, b);
		}
		// Flush and close streams.
		cos.flush();
		cos.close();
		fis.close();
	}

	public static void decrypt(String fileInput, String fileOutput)
			throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		FileInputStream fis = new FileInputStream(fileInput);

		FileOutputStream fos = new FileOutputStream(fileOutput);
		SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(),
				"AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, sks);
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		int b;
		byte[] d = new byte[2048];
		while ((b = cis.read(d)) != -1) {
			fos.write(d, 0, b);
		}
		fos.flush();
		fos.close();
		cis.close();
	}
}
