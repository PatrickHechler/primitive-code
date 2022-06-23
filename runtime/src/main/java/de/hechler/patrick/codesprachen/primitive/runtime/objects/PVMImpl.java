package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.util.Random;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;

public class PVMImpl extends AbstractPVM {
	
	private final MemoryContainer mem  = new MemoryContainer();
	private final long[]          regs = new long[256];
	
	
	public PVMImpl(PatrFileSysImpl fs) {
		super(fs, System.out, System.err, System.in);
	}
	
	public PVMImpl(PatrFileSysImpl fs, Random rnd) {
		super(fs, rnd, System.out, System.err, System.in);
	}
	
	@Override
	protected void putReg(int reg, long val) {
		regs[reg] = val;
	}
	
	@Override
	protected long getReg(int reg) {
		return regs[reg];
	}
	
	@Override
	protected void putLong(long addr, long val) throws PrimitiveErrror {
		mem.set(addr, val);
	}
	
	@Override
	protected long getLong(long addr) throws PrimitiveErrror {
		return mem.get(addr);
	}
	
	@Override
	protected void putChar(long addr, char val) throws PrimitiveErrror {
		mem.setByte(addr, (byte) val);
		mem.setByte(addr + 1, (byte) (val >> 8));
	}
	
	@Override
	protected char getChar(long addr) throws PrimitiveErrror {
		char c = (char) mem.getByte(addr);
		c |= mem.getByte(addr + 1) << 8;
		return c;
	}
	
	@Override
	protected void putByte(long addr, byte val) throws PrimitiveErrror {
		mem.setByte(addr, val);
	}
	
	@Override
	protected byte getByte(long addr) throws PrimitiveErrror {
		return (byte) mem.getByte(addr);
	}
	
	@Override
	protected long malloc(long len) throws OutOfMemoryError {
		return mem.malloc(len);
	}
	
	@Override
	protected long realloc(long addr, long len) throws PrimitiveErrror, OutOfMemoryError {
		return mem.realloc(addr, len);
	}
	
	@Override
	protected void free(long addr) throws PrimitiveErrror {
		mem.free(addr);
	}
	
	@Override
	protected void memcpy(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		mem.copy(srcaddr, dstaddr, len);
	}
	
	@Override
	protected void memmov(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		mem.move(srcaddr, dstaddr, len);
	}
	
	@Override
	protected void memset(long addr, long len, byte val) throws PrimitiveErrror {
		mem.membset(addr, len, val);
	}
	
	@Override
	protected void memset(long addr, long len, long val) throws PrimitiveErrror {
		mem.memset(addr, len, val);
	}
	
	@Override
	protected void set(char[] src, long dstaddr, int cc) throws PrimitiveErrror {
		for (int i = 0; i < cc; i ++ , dstaddr += 2) {
			char c = src[i];
			mem.setByte(dstaddr, (byte) c);
			mem.setByte(dstaddr + 1, (byte) (c >>> 8));
		}
	}
	
	@Override
	protected void set(byte[] src, long dstaddr, int bc) throws PrimitiveErrror {
		for (int i = 0; i < bc; i ++ , dstaddr ++ ) {
			mem.setByte(dstaddr, src[i]);
		}
	}
	
	@Override
	protected void cpy(char[] src, char[] dst, int cc) throws PrimitiveErrror {
		System.arraycopy(src, 0, dst, 0, cc);
	}
	
	@Override
	protected void cpy(String[] src, String[] dst, int oc) throws PrimitiveErrror {
		System.arraycopy(src, 0, dst, 0, oc);
	}
	
	@Override
	protected void cpy(byte[] src, byte[] dst, int bc) throws PrimitiveErrror {
		System.arraycopy(src, 0, dst, 0, bc);
	}
	
	@Override
	protected void get(long srcaddr, char[] dst, int cc) throws PrimitiveErrror {
		for (int i = 0; i < cc; i ++ , srcaddr += 2) {
			char c = (char) (0xFF & mem.getByte(srcaddr));
			c |= (mem.getByte(srcaddr + 1) & 0xFF) << 8;
			dst[i] = c;
		}
	}
	
	@Override
	protected void get(long srcaddr, byte[] dst, int bc) throws PrimitiveErrror {
		for (int i = 0; i < bc; i ++ , srcaddr ++ ) {
			dst[i] = mem.getByte(srcaddr);
		}
	}
	
	@Override
	protected void get(long srcaddr, byte[] dst, int bo, int bc) throws PrimitiveErrror {
		for (int i = 0; i < bc; i ++ , srcaddr ++ ) {
			dst[bo + i] = mem.getByte(srcaddr);
		}
	}
	
	@Override
	protected void getregs(long dstaddr) throws PrimitiveErrror {
		mem.set(dstaddr, regs[0]);
		mem.set(dstaddr + 8L, regs[1]);
		mem.set(dstaddr + 16L, regs[2]);
		mem.set(dstaddr + 24L, regs[3]);
		mem.set(dstaddr + 32L, regs[4]);
		mem.set(dstaddr + 40L, regs[5]);
		mem.set(dstaddr + 48L, regs[6]);
		mem.set(dstaddr + 56L, regs[7]);
		mem.set(dstaddr + 64L, regs[8]);
		mem.set(dstaddr + 72L, regs[9]);
		mem.set(dstaddr + 80L, regs[10]);
		mem.set(dstaddr + 88L, regs[11]);
		mem.set(dstaddr + 96L, regs[12]);
		mem.set(dstaddr + 104L, regs[13]);
		mem.set(dstaddr + 112L, regs[14]);
		mem.set(dstaddr + 120L, regs[15]);
	}
	
	@Override
	protected void setregs(long srcaddr) throws PrimitiveErrror {
		regs[0] = mem.get(srcaddr);
		regs[1] = mem.get(srcaddr + 8L);
		regs[2] = mem.get(srcaddr + 16L);
		regs[3] = mem.get(srcaddr + 24L);
		regs[4] = mem.get(srcaddr + 32L);
		regs[5] = mem.get(srcaddr + 40L);
		regs[6] = mem.get(srcaddr + 48L);
		regs[7] = mem.get(srcaddr + 56L);
		regs[8] = mem.get(srcaddr + 64L);
		regs[9] = mem.get(srcaddr + 72L);
		regs[10] = mem.get(srcaddr + 80L);
		regs[11] = mem.get(srcaddr + 88L);
		regs[12] = mem.get(srcaddr + 96L);
		regs[13] = mem.get(srcaddr + 104L);
		regs[14] = mem.get(srcaddr + 112L);
		regs[15] = mem.get(srcaddr + 120L);
	}
	
	@Override
	protected void getregs(byte[] dst) {
		System.arraycopy(regs, 0, dst, 0, 256);
	}
	
	@Override
	protected void setregs(byte[] src) {
		System.arraycopy(src, 0, regs, 0, 256);
	}
	
	@Override
	protected void checkmem(long addr, long len) throws PrimitiveErrror {
		mem.check(addr, len);
	}
	
}
