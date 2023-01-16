//package de.hechler.patrick.codesprachen.primitive.core.utils;
//
//import java.util.List;
//
//public class Convert {
//	
//	private Convert() {
//		throw new UnsupportedOperationException();
//	}
//	
//	public static long[] convertLongListToLongArr(List <Long> ls) {
//		long[] res = new long[ls.size()];
//		for (int i = 0; i < res.length; i ++ ) {
//			res[i] = ls.get(i);
//		}
//		return res;
//	}
//	
//	public static void convertLongToByteArr(byte[] bytes, int off, long val) {
//		ConvertNumByteArr.longToByteArr(bytes, off, val);
//	}
//	
//	public static long convertByteArrToLong(byte[] bytes, int off) {
//		return ConvertNumByteArr.byteArrToLong(bytes, off);
//	}
//	
//	public static long convertByteArrToLong(byte[] bytes) {
//		return ConvertNumByteArr.byteArrToLong(bytes, 0);
//	}
//	
//	public static void convertIntToByteArr(byte[] bytes, int off, int val) {
//		ConvertNumByteArr.intToByteArr(bytes, off, val);
//	}
//	
//	public static int convertByteArrToInt(byte[] bytes, int off) {
//		return ConvertNumByteArr.byteArrToInt(bytes, off);
//	}
//	
//	public static int convertByteArrToInt(byte[] bytes) {
//		return ConvertNumByteArr.byteArrToInt(bytes, 0);
//	}
//	
//	public static String convertLongToHexString(String postfix, long val, String suffix) {
//		String str = "0000000000000000";
//		String hex = Long.toHexString(val);
//		return postfix + str.substring(hex.length()) + hex + suffix;
//	}
//	
//	public static String convertByteArrToHexString(String postfix, byte[] bytes, int off, int len, String suffix) {
//		StringBuilder build = new StringBuilder( (len * 2) + postfix.length() + suffix.length());
//		build.append(postfix);
//		String str;
//		for (int i = len - 1; i >= 0; i -- ) {
//			str = Integer.toHexString(bytes[off + i] & 0xFF);
//			if (str.length() == 1) {
//				build.append('0');
//			}
//			build.append(str);
//		}
//		return build.append(suffix).toString();
//	}
//	
//	public static String convertByteArrToHexString(String postfix, byte[] bytes, String suffix) {
//		StringBuilder build = new StringBuilder(18 + postfix.length() + suffix.length());
//		build.append(postfix);
//		String str;
//		for (int i = bytes.length - 1; i >= 0; i -- ) {
//			str = Integer.toHexString(bytes[i] & 0xFF);
//			if (str.length() == 1) {
//				build.append('0');
//			}
//			build.append(str);
//		}
//		return build.append(suffix).toString();
//	}
//	
//	public static String convertLongToHexString(String postfix, long val) {
//		String str = "0000000000000000";
//		String hex = Long.toHexString(val);
//		return postfix + str.substring(hex.length()) + hex;
//	}
//	
//	public static String convertByteArrToHexString(String postfix, byte[] bytes) {
//		StringBuilder build = new StringBuilder(18 + postfix.length());
//		build.append(postfix);
//		String str;
//		for (int i = bytes.length - 1; i >= 0; i -- ) {
//			str = Integer.toHexString(bytes[i] & 0xFF);
//			if (str.length() == 1) {
//				build.append('0');
//			}
//			build.append(str);
//		}
//		return build.toString();
//	}
//	
//	public static String convertLongToHexString(long val) {
//		String str = "0000000000000000";
//		String hex = Long.toHexString(val);
//		return str.substring(hex.length()) + hex;
//	}
//	
//	public static String convertByteArrToHexString(byte[] bytes) {
//		StringBuilder build = new StringBuilder(16);
//		String str;
//		for (int i = bytes.length - 1; i >= 0; i -- ) {
//			str = Integer.toHexString(bytes[i] & 0xFF);
//			if (str.length() == 1) {
//				build.append('0');
//			}
//			build.append(str);
//		}
//		return build.toString();
//	}
//	
//	public static String convertLongToHexString(long val, String suffix) {
//		String str = "0000000000000000";
//		String hex = Long.toHexString(val);
//		return str.substring(hex.length()) + hex + suffix;
//	}
//	
//	public static String convertByteArrToHexString(byte[] bytes, String suffix) {
//		StringBuilder build = new StringBuilder(16 + suffix.length());
//		String str;
//		for (int i = bytes.length - 1; i >= 0; i -- ) {
//			str = Integer.toHexString(bytes[i] & 0xFF);
//			if (str.length() == 1) {
//				build.append('0');
//			}
//			build.append(str);
//		}
//		return build.append(suffix).toString();
//	}
//	
//	public static String convertByteArrToHexString(byte[] bytes, int offset, int len) {
//		StringBuilder res = new StringBuilder(len * 2);
//		for (int i = len - 1; i >= 0; i -- ) {
//			String str = Integer.toHexString(0xFF & bytes[offset + i]);
//			if (str.length() == 1) {
//				res.append('0');
//			}
//			res.append(str);
//		}
//		return res.toString();
//	}
//	
//	
//}
