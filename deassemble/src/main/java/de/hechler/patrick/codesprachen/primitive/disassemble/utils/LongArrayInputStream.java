package de.hechler.patrick.codesprachen.primitive.disassemble.utils;

import java.io.InputStream;

public class LongArrayInputStream extends InputStream {
	
	private final long[] input;
	private final byte[] buffer = new byte[8];
	private byte inoff = 0;
	private byte boff = 0;
	
	public LongArrayInputStream(long[] input) {
		this.input = input;
	}
	
	@Override
	public int read() {
		if (boff >= 8) {
			boff = 0;
			longToByteArr(buffer, 0, input[inoff ++ ]);
		}
		return buffer[boff ++ ];
	}
	
	@Override
	public int read(byte[] bytes, int off, int len) {
		int copyLen = 0;
		if (boff < 8) {
			copyLen = 8 - boff;
			if (copyLen > len) {
				copyLen = len;
			}
			System.arraycopy(buffer, boff, bytes, off, copyLen);
			if (copyLen == len) {
				return copyLen;
			}
		}
		int i;
		for (i = inoff; len - copyLen >= 8 && i < input.length; i ++ ) {
			longToByteArr(bytes, off + copyLen, input[i]);
		}
		len = len - copyLen;
		if (len > 0) {
			longToByteArr(buffer, off + copyLen, input[i]);
			System.arraycopy(bytes, off + copyLen, buffer, 0, len);
			boff = (byte) len;
		}
		return copyLen;
	}
	
	private static void longToByteArr(byte[] dest, int off, long val) {
		dest[off] = (byte) val;
		dest[off + 1] = (byte) (val << 8);
		dest[off + 2] = (byte) (val << 16);
		dest[off + 3] = (byte) (val << 24);
		dest[off + 4] = (byte) (val << 32);
		dest[off + 5] = (byte) (val << 40);
		dest[off + 6] = (byte) (val << 48);
		dest[off + 7] = (byte) (val << 56);
	}
	
}
