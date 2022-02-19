package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertByteArrToLong;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToByteArr;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToHexString;

import java.io.PrintStream;


public class PVMSnapshot {
	
	public static final int PVM_SNAPSHOT_LENGH = 256 * 8;
	
	public static final long PVM_SNAPSHOT_STATUS_LOWER = 0x0000000000000001L;
	public static final long PVM_SNAPSHOT_STATUS_GREATHER = 0x0000000000000002L;
	public static final long PVM_SNAPSHOT_STATUS_CARRY = 0x0000000000000004L;
	public static final long PVM_SNAPSHOT_STATUS_ZERO = 0x0000000000000008L;
	
	public long ip;
	public long sp;
	public long status;
	public long intcnt;
	public long intp;
	public long[] x;
	
	public PVMSnapshot(long[] longs) {
		this.ip = longs[0];
		this.sp = longs[1];
		this.status = longs[2];
		this.intcnt = longs[3];
		this.intp = longs[4];
		this.x = new long[256 - 5];
		System.arraycopy(longs, 5, this.x, 0, 256 - 5);
	}
	
	public static PVMSnapshot create(byte[] bytes) throws AssertionError {
		if (bytes.length != PVM_SNAPSHOT_LENGH) {
			throw new AssertionError();
		}
		long[] longs = new long[256];
		for (int li = 0, bi = 0; li < 256; li ++, bi += 8 ) {
			longs[li] = convertByteArrToLong(bytes, bi);
		}
		return new PVMSnapshot(longs);
	}
	
	public byte[] toBytes() {
		byte[] bytes = new byte[PVM_SNAPSHOT_LENGH];
		convertLongToByteArr(bytes, 0, this.ip);
		convertLongToByteArr(bytes, 8, this.sp);
		convertLongToByteArr(bytes, 16, this.status);
		convertLongToByteArr(bytes, 24, this.intcnt);
		convertLongToByteArr(bytes, 32, this.intp);
		for (int i = 0, off = 40; i < x.length; i ++ , off += 8) {
			convertLongToByteArr(bytes, off, this.x[i]);
		}
		return bytes;
	}
	
	
	public void print(PrintStream out) {
		out.print(toString());
	}
	
	@SuppressWarnings("unused")
	private String statusToString(long status) {
		StringBuilder build = new StringBuilder();
		statusToString(build, status);
		return build.toString();
	}
	
	private void statusToString(StringBuilder build, long status) {
		build.append("[");
		boolean first = true;
		if ( (status & PVM_SNAPSHOT_STATUS_LOWER) != 0) {
			build.append("lower");
			first = false;
		}
		if ( (status & PVM_SNAPSHOT_STATUS_GREATHER) != 0) {
			if ( !first) {
				build.append(" | ");
			}
			build.append("greather");
			first = false;
		}
		if ( (status & PVM_SNAPSHOT_STATUS_CARRY) != 0) {
			if ( !first) {
				build.append(" | ");
			}
			build.append("carry");
			first = false;
		}
		build.append(']');
	}
	
	@Override
	public String toString() {
		return toString(256 - 5);
	}
	
	public String toString(int xcnt) {
		assert xcnt >= 0;
		assert xcnt <= 256 - 5;
		StringBuilder build = new StringBuilder("PVMSnapshot:");
		build.append("  ip=").append(convertLongToHexString(ip)).append('\n');
		build.append("  sp=").append(convertLongToHexString(sp)).append('\n');
		build.append("  status=").append(convertLongToHexString(status)).append(" : ");
		statusToString(build, status);
		build.append('\n');
		build.append("  intcnt=").append(convertLongToHexString(intcnt)).append('\n');
		build.append("  intp=").append(convertLongToHexString(intp)).append('\n');
		for (int i = 0; i < xcnt; i ++ ) {
			build.append("  X");
			String str = Integer.toHexString(i);
			if (str.length() == 1) {
				build.append('0');
			}
			build.append(str).append('=').append(convertLongToHexString(x[i])).append('\n');
		}
		return build.toString();
	}
	
}
