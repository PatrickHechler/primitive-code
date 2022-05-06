package de.hechler.patrick.codesprachen.primitive.assemble;

public class TestUtils {

	private final static String ENDL = System.lineSeparator();
	
	public static long[] toLong(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		int byteSize = bytes.length;
		if (byteSize % 8 != 0) {
			throw new RuntimeException("Unaliged byte array, length ("+byteSize+") must be multiple of 8 (size_of long).");
		}
		long[] result = new long[byteSize/8];
		int bytePos = 0;
		for (int longPos=0; longPos<result.length; longPos++) {
			long convert = 0;
			for (int shift=0; shift<64; shift+=8) {
				convert = convert | (Byte.toUnsignedLong(bytes[bytePos++])<<shift);
			}
			result[longPos] = convert;
		}
		return result;
	}
	
	public static String toHexCode(byte[] bytes) {
		return toHexCode(bytes, 0);
	}
	public static String toHexCode(byte[] bytes, int cols) {
		StringBuilder result = new StringBuilder();
		int col=0;
		for (byte b:bytes) {
			result.append(toHex(b));
			if (++col == cols) {
				col = 0;
				result.append(ENDL);
			}
			else {
				result.append(" ");
			}
		}
		return result.toString();
	}
	
	public static String toHexCode(long[] longs) {
		return toHexCode(longs, 0);
	}
	public static String toHexCode(long[] longs, int cols) {
		StringBuilder result = new StringBuilder();
		int col=0;
		for (long l:longs) {
			result.append(toHex(l));
			if (++col == cols) {
				col = 0;
				result.append(ENDL);
			}
			else {
				result.append(" ");
			}
		}
		return result.toString();		
	}

	
	public static String toHex(byte b) {
		String result = Integer.toHexString(Byte.toUnsignedInt(b));
		if (result.length()==1) {
			result = "0"+result;
		}
		return result;
	}

	public static String toHex(long l) {
		String result = Long.toHexString(l);
		int len = result.length();
		if (len<16) {
			result = "000000000000000".substring(0, 16-len)+result;
		}
		return result;
	}

	
}
