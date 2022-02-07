package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.*;

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
	public long intp;
	public long intcnt;
	
	public static PVMSnapshot create(byte[] bytes) throws AssertionError {
		if (bytes.length != PVM_SNAPSHOT_LENGH) {
			throw new AssertionError();
		}
		return new PVMSnapshot(convertByteArrToLong(bytes, 0), convertByteArrToLong(bytes, 8), convertByteArrToLong(bytes, 16), convertByteArrToLong(bytes, 24),
				convertByteArrToLong(bytes, 32), convertByteArrToLong(bytes, 40), convertByteArrToLong(bytes, 48), convertByteArrToLong(bytes, 56),
				convertByteArrToLong(bytes, 64));
	}
	
	public PVMSnapshot(long ax, long bx, long cx, long dx, long sp, long ip, long status, long intp, long intcnt) {
		this.ax = ax;
		this.bx = bx;
		this.cx = cx;
		this.dx = dx;
		this.sp = sp;
		this.ip = ip;
		this.status = status;
		this.intp = intp;
		this.intcnt = intcnt;
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
		convertLongToByteArr(bytes, 56, this.intp);
		convertLongToByteArr(bytes, 64, this.intcnt);
		return bytes;
	}
	
	
	public void print(PrintStream out) {
		out.println("PVMSnapshot:");
		out.println("  ax=" + PVMDebugger.toFullHexStr(ax));
		out.println("  bx=" + PVMDebugger.toFullHexStr(bx));
		out.println("  cx=" + PVMDebugger.toFullHexStr(cx));
		out.println("  dx=" + PVMDebugger.toFullHexStr(dx));
		out.println("  sp=" + PVMDebugger.toFullHexStr(sp));
		out.println("  ip=" + PVMDebugger.toFullHexStr(ip));
		out.println("  status=" + PVMDebugger.toFullHexStr(status)+ " : " + statusToString(status));
		out.println("  intp=" + PVMDebugger.toFullHexStr(intp));
		out.println("  intcnt=" + PVMDebugger.toFullHexStr(intcnt));
	}
	
	private String statusToString(long status) {
		StringBuilder build = new StringBuilder("[");
		boolean first = true;
		if ((status & PVM_SNAPSHOT_STATUS_LOWER) != 0) {
			build.append("lower");
			first = false;
		}
		if ((status & PVM_SNAPSHOT_STATUS_GREATHER) != 0) {
			if (!first) {
				build.append(" | ");
			}
			build.append("greather");
			first = false;
		}
		if ((status & PVM_SNAPSHOT_STATUS_CARRY) != 0) {
			if (!first) {
				build.append(" | ");
			}
			build.append("carry");
			first = false;
		}
		return build.append(']').toString();
	}
	
	@Override
	public String toString() {
		return "PVMSnapshot:\n  ax=" + ax + "\n  bx=" + bx + ", cx=" + cx + ", dx=" + dx + ", ip=" + ip + ", sp=" + sp + ", intp=" + intp + ", status=" + status + "]";
	}
	
}
