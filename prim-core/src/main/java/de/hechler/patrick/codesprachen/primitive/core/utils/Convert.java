//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.core.utils;

import java.util.List;

public class Convert {
	
	private static final String ZERO_16_TIMES = "0000000000000000";
	
	private Convert() { throw new UnsupportedOperationException(); }
	
	public static long[] convertLongListToLongArr(List<Long> ls) {
		long[] res = new long[ls.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = ls.get(i);
		}
		return res;
	}
	
	public static void convertLongToByteArr(byte[] bytes, int off, long val) {
		bytes[off + 0] = (byte) val;
		bytes[off + 1] = (byte) (val >> 8);
		bytes[off + 2] = (byte) (val >> 16);
		bytes[off + 3] = (byte) (val >> 24);
		bytes[off + 4] = (byte) (val >> 32);
		bytes[off + 5] = (byte) (val >> 40);
		bytes[off + 6] = (byte) (val >> 48);
		bytes[off + 7] = (byte) (val >> 56);
	}
	
	public static void convertLongToByteArr(byte[] bytes, long val) { convertLongToByteArr(bytes, 0, val); }
	
	public static long convertByteArrToLong(byte[] bytes, int off) {
		long val = 0xFFL & bytes[off + 0];
		val |= ((0xFFL & bytes[off + 1]) << 8);
		val |= ((0xFFL & bytes[off + 2]) << 16);
		val |= ((0xFFL & bytes[off + 3]) << 24);
		val |= ((0xFFL & bytes[off + 4]) << 32);
		val |= ((0xFFL & bytes[off + 5]) << 40);
		val |= ((0xFFL & bytes[off + 6]) << 48);
		val |= ((0xFFL & bytes[off + 7]) << 56);
		return val;
	}
	
	public static long convertByteArrToLong(byte[] bytes) { return convertByteArrToLong(bytes, 0); }
	
	public static void convertIntToByteArr(byte[] bytes, int off, int val) {
		bytes[off + 0] = (byte) val;
		bytes[off + 1] = (byte) (val >> 8);
		bytes[off + 2] = (byte) (val >> 16);
		bytes[off + 3] = (byte) (val >> 24);
	}
	
	public static int convertByteArrToInt(byte[] bytes, int off) {
		int val = 0xFF & bytes[off + 0];
		val |= ((0xFF & bytes[off + 1]) << 8);
		val |= ((0xFF & bytes[off + 2]) << 16);
		val |= ((0xFF & bytes[off + 3]) << 24);
		return val;
	}
	
	public static int convertByteArrToInt(byte[] bytes) { return convertByteArrToInt(bytes, 0); }
	
	public static String convertLongToHexString(String postfix, long val, String suffix) {
		String str = ZERO_16_TIMES;
		String hex = Long.toHexString(val).toUpperCase();
		return postfix + str.substring(hex.length()) + hex + suffix;
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes, int off, int len, String suffix) {
		StringBuilder build = new StringBuilder((len * 2) + postfix.length() + suffix.length());
		build.append(postfix);
		String str;
		for (int i = len - 1; i >= 0; i--) {
			str = Integer.toHexString(bytes[off + i] & 0xFF).toUpperCase();
			if (str.length() == 1) { build.append('0'); }
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes, String suffix) {
		StringBuilder build = new StringBuilder(18 + postfix.length() + suffix.length());
		build.append(postfix);
		String str;
		for (int i = bytes.length - 1; i >= 0; i--) {
			str = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (str.length() == 1) { build.append('0'); }
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertLongToHexString(String postfix, long val) {
		String str = ZERO_16_TIMES;
		String hex = Long.toHexString(val).toUpperCase();
		return postfix + str.substring(hex.length()) + hex;
	}
	
	public static String convertByteArrToHexString(String postfix, byte[] bytes) {
		StringBuilder build = new StringBuilder(18 + postfix.length());
		build.append(postfix);
		String str;
		for (int i = bytes.length - 1; i >= 0; i--) {
			str = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (str.length() == 1) { build.append('0'); }
			build.append(str);
		}
		return build.toString();
	}
	
	public static String convertLongToHexString(long val) {
		String str = ZERO_16_TIMES;
		String hex = Long.toHexString(val).toUpperCase();
		return str.substring(hex.length()) + hex;
	}
	
	public static String convertByteArrToHexString(byte[] bytes) {
		StringBuilder build = new StringBuilder(16);
		String        str;
		for (int i = bytes.length - 1; i >= 0; i--) {
			str = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (str.length() == 1) { build.append('0'); }
			build.append(str);
		}
		return build.toString();
	}
	
	public static String convertLongToHexString(long val, String suffix) {
		String str = ZERO_16_TIMES;
		String hex = Long.toHexString(val).toUpperCase();
		return str.substring(hex.length()) + hex + suffix;
	}
	
	public static String convertByteArrToHexString(byte[] bytes, String suffix) {
		StringBuilder build = new StringBuilder(16 + suffix.length());
		String        str;
		for (int i = bytes.length - 1; i >= 0; i--) {
			str = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
			if (str.length() == 1) { build.append('0'); }
			build.append(str);
		}
		return build.append(suffix).toString();
	}
	
	public static String convertByteArrToHexString(byte[] bytes, int offset, int len) {
		StringBuilder res = new StringBuilder(len * 2);
		for (int i = len - 1; i >= 0; i--) {
			String str = Integer.toHexString(0xFF & bytes[offset + i]).toUpperCase();
			if (str.length() == 1) { res.append('0'); }
			res.append(str);
		}
		return res.toString();
	}
	
}
