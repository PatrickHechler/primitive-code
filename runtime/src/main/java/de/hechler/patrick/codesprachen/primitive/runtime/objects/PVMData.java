package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTCNT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INTP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.IP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.SP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_CARRY;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_GREATHER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.STATUS_LOWER;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.X_ADD;
import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.convertLongToHexString;

import java.io.PrintStream;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.pfs.utils.PatrFileSysConstants;

public class PVMData {
	
	public final long[] regs = new long[256];
	
	public PVMData(MemoryContainer mem) {
		this.regs[IP] = -1L;
		this.regs[SP] = -1L;
		this.regs[STATUS] = 0L;
		this.regs[INTCNT] = INTERRUPT_COUNT;
		this.regs[INTP] = mem.malloc(INTERRUPT_COUNT << 3);
		for (int i = 0; i < INTERRUPT_COUNT; i ++ ) {
			try {
				mem.set(this.regs[INTP] + (i << 3), -1L);
			} catch (PrimitiveErrror e) {
				throw new InternalError(e);
			}
		}
		this.regs[FS_LOCK] = PatrFileSysConstants.NO_LOCK;
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
		if ( (status & STATUS_LOWER) != 0) {
			build.append("lower");
			first = false;
		}
		if ( (status & STATUS_GREATHER) != 0) {
			if ( !first) {
				build.append(" | ");
			}
			build.append("greather");
			first = false;
		}
		if ( (status & STATUS_CARRY) != 0) {
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
		return toString(256);
	}
	
	public String toString(int regcnt) {
		assert regcnt >= 0;
		assert regcnt <= 256;
		StringBuilder build = new StringBuilder("PVM:\n");
		for (int reg = 0; reg < regcnt; reg ++ ) {
			switch (reg) {
			case IP:
				build.append("  ip=").append(convertLongToHexString(regs[IP])).append('\n');
				break;
			case SP:
				build.append("  sp=").append(convertLongToHexString(regs[SP])).append('\n');
				break;
			case STATUS:
				build.append("  status=").append(convertLongToHexString(regs[STATUS])).append(" : ");
				statusToString(build, regs[STATUS]);
				build.append('\n');
				break;
			case INTCNT:
				build.append("  intcnt=").append(convertLongToHexString(regs[INTCNT])).append('\n');
				break;
			case INTP:
				build.append("  intp=").append(convertLongToHexString(regs[INTP])).append('\n');
				break;
			case FS_LOCK:
				build.append("  fs-lock=").append(convertLongToHexString(regs[FS_LOCK])).append('\n');
				break;
			default:
				build.append("  X");
				String str = Integer.toHexString(reg - X_ADD);
				if (str.length() == 1) {
					build.append('0');
				}
				build.append(str).append('=').append(convertLongToHexString(regs[reg])).append('\n');
				break;
			}
		}
		return build.toString();
	}
	
}
