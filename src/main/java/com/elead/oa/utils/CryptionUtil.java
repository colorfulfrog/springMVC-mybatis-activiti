package com.elead.oa.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class CryptionUtil {
	public CryptionUtil() {
	}

	private static String encode(byte data[]) {
		char out[] = new char[((data.length + 2) / 3) * 4];
		int i = 0;
		for (int index = 0; i < data.length; index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = 255 & data[i];
			val <<= 8;
			if (i + 1 < data.length) {
				val |= 255 & data[i + 1];
				trip = true;
			}
			val <<= 8;
			if (i + 2 < data.length) {
				val |= 255 & data[i + 2];
				quad = true;
			}
			out[index + 3] = alphabet[quad ? val & 63 : 64];
			val >>= 6;
			out[index + 2] = alphabet[trip ? val & 63 : 64];
			val >>= 6;
			out[index + 1] = alphabet[val & 63];
			val >>= 6;
			out[index + 0] = alphabet[val & 63];
			i += 3;
		}

		return new String(out);
	}

	private static byte[] decode(char data[]) {
		int len = ((data.length + 3) / 4) * 3;
		if (data.length > 0 && data[data.length - 1] == '=')
			len--;
		if (data.length > 1 && data[data.length - 2] == '=')
			len--;
		byte out[] = new byte[len];
		int shift = 0;
		int accum = 0;
		int index = 0;
		for (int ix = 0; ix < data.length; ix++) {
			int value = codes[data[ix] & 255];
			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) (accum >> shift & 255);
				}
			}
		}

		if (index != out.length)
			throw new Error("miscalculated data length!");
		else
			return out;
	}

	public static String encryption(String plainData, String secretKey) {
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(1, generateKey(secretKey));
			byte buf[] = cipher.doFinal(plainData.getBytes());
			result = encode(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decryption(String secretData, String secretKey) {
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(2, generateKey(secretKey));
			byte buf[] = cipher.doFinal(decode(secretData.toCharArray()));
			result = new String(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static SecretKey generateKey(String secretKey) throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(secretKey.getBytes());
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
		}
		kg.init(secureRandom);
		return kg.generateKey();
	}

	@SuppressWarnings("unused")
	private static String byteArray2HexStingr(byte bs[]) {
		StringBuffer sb = new StringBuffer();
		byte abyte0[];
		int j = (abyte0 = bs).length;
		for (int i = 0; i < j; i++) {
			byte b = abyte0[i];
			sb.append(byte2HexString(b));
		}

		return sb.toString();
	}

	private static String byte2HexString(byte b) {
		String hexStr = null;
		int n = b;
		if (n < 0)
			n = b & 255;
		hexStr = (new StringBuilder(String.valueOf(Integer.toHexString(n / 16)))).append(Integer.toHexString(n % 16))
				.toString();
		return hexStr.toUpperCase();
	}

	public static String md5Hex(String origString) {
		return MD5Encoder(origString, "UTF-8");
	}

	public final static String MD5Encoder(String s, String charset) {
		try {
			byte[] btInput = s.getBytes(charset);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int val = ((int) md[i]) & 0xff;
				if (val < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(val));
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private static byte codes[];
	private static char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

	static {
		codes = new byte[256];
		for (int i = 0; i < 256; i++)
			codes[i] = -1;

		for (int i = 65; i <= 90; i++)
			codes[i] = (byte) (i - 65);

		for (int i = 97; i <= 122; i++)
			codes[i] = (byte) ((26 + i) - 97);

		for (int i = 48; i <= 57; i++)
			codes[i] = (byte) ((52 + i) - 48);

		codes[43] = 62;
		codes[47] = 63;
	}

}

/*
 * DECOMPILATION REPORT
 * 
 * Decompiled from: E:\MeSpace\Me2015S3a\IHRBg\lib\agileai_common_1.1.0.jar
 * Total time: 35 ms Jad reported messages/errors: Exit status: 0 Caught
 * exceptions:
 */
