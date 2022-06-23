package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY;

import java.lang.reflect.Field;
import java.util.Random;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class PVMUnsafeImpl extends AbstractPVM {
	
	private static final Unsafe U;
	
	static {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			U = (Unsafe) theUnsafe.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new InternalError(e);
		}
		if (Unsafe.ARRAY_BYTE_INDEX_SCALE != 1 || Unsafe.ARRAY_CHAR_INDEX_SCALE != 2) {
			throw new AssertionError();
		}
	}
	
	private static final int BS_BO = Unsafe.ARRAY_BYTE_BASE_OFFSET;
	private static final int CS_BO = Unsafe.ARRAY_CHAR_BASE_OFFSET;
	private static final int ST_BO = U.arrayBaseOffset(Object[].class);
	
	private final MemoryChecker mem  = new MemoryChecker(U);
	private final long          regs = U.allocateMemory(256L * 8L);
	
	public PVMUnsafeImpl(PatrFileSysImpl fs) {
		super(fs, System.out, System.err, System.in);
	}
	
	public PVMUnsafeImpl(PatrFileSysImpl fs, Random rnd) {
		super(fs, rnd, System.out, System.err, System.in);
	}
	
	@Override
	protected void putReg(int reg, long val) {
		U.putLong(regs + (reg << 8), val);
	}
	
	@Override
	protected long getReg(int reg) {
		return U.getLong(regs + (reg << 8));
	}
	
	@Override
	protected void putLong(long addr, long val) throws PrimitiveErrror {
		mem.check(addr, 8L);
		U.putLong(addr, val);
	}
	
	@Override
	protected long getLong(long addr) throws PrimitiveErrror {
		mem.check(addr, 8L);
		return U.getLong(addr);
	}
	
	@Override
	protected void putChar(long addr, char val) throws PrimitiveErrror {
		mem.check(addr, 2L);
		U.putChar(addr, val);
	}
	
	@Override
	protected char getChar(long addr) throws PrimitiveErrror {
		mem.check(addr, 2L);
		return U.getChar(addr);
	}
	
	@Override
	protected void putByte(long addr, byte val) throws PrimitiveErrror {
		mem.check(addr, 1L);
		U.putByte(addr, val);
	}
	
	@Override
	protected byte getByte(long addr) throws PrimitiveErrror {
		mem.check(addr, 1L);
		return U.getByte(addr);
	}
	
	@Override
	protected long malloc(long len) throws OutOfMemoryError {
		long addr = U.allocateMemory(len);
		mem.malloc(addr, len);
		return addr;
	}
	
	@Override
	protected long realloc(long addr, long len) throws PrimitiveErrror, OutOfMemoryError {
		mem.chackAllocated(addr);
		long naddr = U.reallocateMemory(addr, len);
		mem.realloc(addr, naddr, len);
		return naddr;
	}
	
	@Override
	protected void free(long addr) throws PrimitiveErrror {
		mem.chackAllocated(addr);
		U.freeMemory(addr);
		mem.free(addr);
	}
	
	@Override
	protected void memcpy(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		U.copyMemory(null, srcaddr, null, dstaddr, len);
	}
	
	@Override
	protected void memmov(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		U.copyMemory(null, srcaddr, null, dstaddr, len);
	}
	
	@Override
	protected void memset(long addr, long len, byte val) throws PrimitiveErrror {
		U.setMemory(null, addr, len, val);
	}
	
	@Override
	protected void memset(long addr, long len, long val) throws PrimitiveErrror {
		long bv = val & 0xFF;
		long bl = len << 3;
		if (bl != len) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		mem.check(addr, bl);
		if (bv != ( (val >>> 8) & 0xFF) || bv != ( (val >>> 16) & 0xFF) || bv != ( (val >>> 24) & 0xFF) || bv != ( (val >>> 32) & 0xFF) || bv != ( (val >>> 40) & 0xFF)
			|| bv != ( (val >>> 48) & 0xFF) || bv != ( (val >>> 56) & 0xFF)) {
			for (; len > 0L; len -- ) {
				U.putLong(addr ++ , val);
			}
		} else {
			U.setMemory(null, addr, bl, (byte) bv);
		}
	}
	
	@Override
	protected void set(char[] src, long dstaddr, int cc) throws PrimitiveErrror {
		U.copyMemory(src, CS_BO, null, dstaddr, ((long) cc) << 1L);
	}
	
	@Override
	protected void set(byte[] src, long dstaddr, int bc) throws PrimitiveErrror {
		U.copyMemory(src, BS_BO, null, dstaddr, (long) bc);
	}
	
	@Override
	protected void cpy(char[] src, char[] dst, int cc) throws PrimitiveErrror {
		U.copyMemory(src, CS_BO, dst, CS_BO, ((long) cc) << 1L);
	}
	
	@Override
	protected void cpy(String[] src, String[] dst, int oc) throws PrimitiveErrror {
		U.copyMemory(src, ST_BO, dst, ST_BO, ((long) oc) << 3L);
	}
	
	@Override
	protected void cpy(byte[] src, byte[] dst, int bc) throws PrimitiveErrror {
		U.copyMemory(src, BS_BO, dst, BS_BO, ((long) bc) << 1L);
	}
	
	@Override
	protected void get(long srcaddr, char[] dst, int cc) throws PrimitiveErrror {
		U.copyMemory(null, srcaddr, dst, CS_BO, ((long) cc) << 1L);
	}
	
	@Override
	protected void get(long srcaddr, byte[] dst, int bc) throws PrimitiveErrror {
		U.copyMemory(null, srcaddr, dst, BS_BO, (long) bc);
	}
	
	@Override
	protected void getregs(long dstaddr) throws PrimitiveErrror {
		U.copyMemory(null, regs, null, dstaddr, 128L);
	}
	
	@Override
	protected void setregs(long srcaddr) throws PrimitiveErrror {
		U.copyMemory(null, srcaddr, null, regs, 128L);
	}
	
	@Override
	protected void getregs(byte[] dst) {
		if (dst.length != 256) {
			throw new AssertionError();
		}
		U.copyMemory(null, regs, dst, Unsafe.ARRAY_BYTE_BASE_OFFSET, 256L);
	}
	
	@Override
	protected void setregs(byte[] src) {
		if (src.length != 256) {
			throw new AssertionError();
		}
		U.copyMemory(null, regs, src, Unsafe.ARRAY_BYTE_BASE_OFFSET, 256L);
	}
	
}
