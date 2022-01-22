package de.hechler.patrick.codesprachen.primitive.disassemble.utils;

import java.util.List;

public class Convert {
	
	private Convert() {
	}
	
	public static long[] convertLongListToLongArr(List<Long> ls) {
		long[] res = new long[ls.size()];
		for(int i = 0; i < res.length; i ++) {
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
	
}
