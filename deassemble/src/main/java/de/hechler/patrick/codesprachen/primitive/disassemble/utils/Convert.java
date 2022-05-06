package de.hechler.patrick.codesprachen.primitive.disassemble.utils;

import java.util.List;

public class Convert {
	
	private Convert() {
		throw new UnsupportedOperationException();
	}
	
	public static long[] convertLongListToLongArr(List <Long> ls) {
		long[] res = new long[ls.size()];
		for (int i = 0; i < res.length; i ++ ) {
			res[i] = ls.get(i);
		}
		return res;
	}
	
	public static void convertLongToByteArr(byte[] bytes, int off, long val) {
		bytes[off] = (byte) (val & 0xFFL);
		bytes[off + 1] = (byte) ( (val >> 8) & 0xFFL);
		bytes[off + 2] = (byte) ( (val >> 16) & 0xFFL);
		bytes[off + 3] = (byte) ( (val >> 24) & 0xFFL);
		bytes[off + 4] = (byte) ( (val >> 32) & 0xFFL);
		bytes[off + 5] = (byte) ( (val >> 40) & 0xFFL);
		bytes[off + 6] = (byte) ( (val >> 48) & 0xFFL);
		bytes[off + 7] = (byte) ( (val >> 56) & 0xFFL);
	}
	
	public static long convertByteArrToLong(byte[] bytes, int off) {
		long res = bytes[off] & 0xFFL;
		res |= (bytes[off + 1] & 0xFFL) << 8;
		res |= (bytes[off + 2] & 0xFFL) << 16;
		res |= (bytes[off + 3] & 0xFFL) << 24;
		res |= (bytes[off + 4] & 0xFFL) << 32;
		res |= (bytes[off + 5] & 0xFFL) << 40;
		res |= (bytes[off + 6] & 0xFFL) << 48;
		res |= (bytes[off + 7] & 0xFFL) << 56;
		return res;
	}
	
	public static long convertByteArrToLong(byte[] bytes) {
		long val;
		val = 0xFFL & bytes[0];
		val |= (0xFFL & bytes[1]) << 8;
		val |= (0xFFL & bytes[2]) << 16;
		val |= (0xFFL & bytes[3]) << 24;
		val |= (0xFFL & bytes[4]) << 32;
		val |= (0xFFL & bytes[5]) << 40;
		val |= (0xFFL & bytes[6]) << 48;
		val |= (0xFFL & bytes[7]) << 56;
		return val;
	}
	
	public static String convertLongToHexString(String postfix, long val, String suffix) {
		String str = "0000000000000000";
		String hex = Long.toHexString(val);
		return postfix + str.substring(hex.length()) + hex + suffix;
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes, int off, int len, String suffix) {
		StringBuilder build = new StringBuilder( (len * 2) + postfix.length() + suffix.length());
		build.append(postfix);
		String str;
		for (int i = len - 1; i >= 0; i -- ) {
			str = Integer.toHexString(bytes[off + i] & 0xFF);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes, String suffix) {
		StringBuilder build = new StringBuilder(18 + postfix.length() + suffix.length());
		build.append(postfix);
		String str;
		for (int i = bytes.length - 1; i >= 0; i -- ) {
			str = Integer.toHexString(bytes[i] & 0xFF);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertLongToHexString(String postfix, long val) {
		String str = "0000000000000000";
		String hex = Long.toHexString(val);
		return postfix + str.substring(hex.length()) + hex;
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes) {
		StringBuilder build = new StringBuilder(18 + postfix.length());
		build.append(postfix);
		String str;
		for (int i = bytes.length - 1; i >= 0; i -- ) {
			str = Integer.toHexString(bytes[i] & 0xFF);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str);
		}
		return build.toString();
	}
	
	public static String convertLongToHexString(long val) {
		String str = "0000000000000000";
		String hex = Long.toHexString(val);
		return str.substring(hex.length()) + hex;
	}
	
	public static String convertByteArrToHexString(byte[] bytes) {
		StringBuilder build = new StringBuilder(16);
		String str;
		for (int i = bytes.length - 1; i >= 0; i -- ) {
			str = Integer.toHexString(bytes[i] & 0xFF);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str);
		}
		return build.toString();
	}
	
	public static String convertLongToHexString(long val, String suffix) {
		String str = "0000000000000000";
		String hex = Long.toHexString(val);
		return str.substring(hex.length()) + hex + suffix;
	}
	
	public static String convertByteArrToHexString(byte[] bytes, String suffix) {
		StringBuilder build = new StringBuilder(16 + suffix.length());
		String str;
		for (int i = bytes.length - 1; i >= 0; i -- ) {
			str = Integer.toHexString(bytes[i] & 0xFF);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertByteArrToHexString(byte[] bytes, int offset, int len) {
		StringBuilder res = new StringBuilder(len * 2);
		for (int i = len - 1; i >= 0; i -- ) {
			String str = Integer.toHexString(0xFF & bytes[offset + i]);
			if (str.length() == 1) {
				res.append('0');
			}
			res.append(str);
		}
		return res.toString();
	}
	
	
}
