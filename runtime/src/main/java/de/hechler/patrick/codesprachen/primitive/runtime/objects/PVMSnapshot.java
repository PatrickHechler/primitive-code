package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertByteArrToLong;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToByteArr;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToHexString;

import java.io.PrintStream;


public class PVMSnapshot {
	
	public static final int PVM_SNAPSHOT_LENGH = 9 * 8;
	
	public static final long PVM_SNAPSHOT_STATUS_LOWER = 0x0000000000000001L;
	public static final long PVM_SNAPSHOT_STATUS_GREATHER = 0x0000000000000002L;
	public static final long PVM_SNAPSHOT_STATUS_CARRY = 0x0000000000000004L;
	
	public long ax;
	public long bx;
	public long cx;
	public long dx;
	public long sp;
	public long ip;
	public long status;
	public long intcnt;
	public long intp;
	
	public static PVMSnapshot create(byte[] bytes) throws AssertionError {
		if (bytes.length != PVM_SNAPSHOT_LENGH) {
			throw new AssertionError();
		}
		return new PVMSnapshot(convertByteArrToLong(bytes, 0), convertByteArrToLong(bytes, 8), convertByteArrToLong(bytes, 16), convertByteArrToLong(bytes, 24),
				convertByteArrToLong(bytes, 32), convertByteArrToLong(bytes, 40), convertByteArrToLong(bytes, 48), convertByteArrToLong(bytes, 56),
				convertByteArrToLong(bytes, 64));
	}
	
	public PVMSnapshot(long ax, long bx, long cx, long dx, long sp, long ip, long status, long intcnt, long intp) {
		this.ax = ax;
		this.bx = bx;
		this.cx = cx;
		this.dx = dx;
		this.sp = sp;
		this.ip = ip;
		this.status = status;
		this.intcnt = intcnt;
		this.intp = intp;
	}
	
	public byte[] toBytes() {
		byte[] bytes = new byte[PVM_SNAPSHOT_LENGH];
		convertLongToByteArr(bytes, 0, this.ax);
		convertLongToByteArr(bytes, 8, this.bx);
		convertLongToByteArr(bytes, 16, this.cx);
		convertLongToByteArr(bytes, 24, this.dx);
		convertLongToByteArr(bytes, 32, this.sp);
		convertLongToByteArr(bytes, 40, this.ip);
		convertLongToByteArr(bytes, 48, this.status);
		convertLongToByteArr(bytes, 56, this.intcnt);
		convertLongToByteArr(bytes, 64, this.intp);
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
		StringBuilder build = new StringBuilder("PVMSnapshot:");
		build.append("  ax=").append(convertLongToHexString(ax)).append('\n');
		build.append("  bx=").append(convertLongToHexString(bx)).append('\n');
		build.append("  cx=").append(convertLongToHexString(cx)).append('\n');
		build.append("  dx=").append(convertLongToHexString(dx)).append('\n');
		build.append("  sp=").append(convertLongToHexString(sp)).append('\n');
		build.append("  ip=").append(convertLongToHexString(ip)).append('\n');
		build.append("  status=").append(convertLongToHexString(status)).append(" : ");
		statusToString(build, status);
		build.append('\n');
		build.append("  intcnt=").append(convertLongToHexString(intcnt)).append('\n');
		build.append("  intp=").append(convertLongToHexString(intp)).append('\n');
		return build.toString();
	}
	
}
