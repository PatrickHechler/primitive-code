package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;

public class DebugPVMImpl extends AbstractDebugPVM {
	
	private final AbstractPVM d;
	
	public DebugPVMImpl(AbstractPVM d) {
		super(d.fs, d.rnd);
		this.d = d;
	}
	
	protected void putReg(int reg, long val) {
		d.putReg(reg, val);
	}
	
	protected long getReg(int reg) {
		return d.getReg(reg);
	}
	
	protected void putLong(long addr, long val) throws PrimitiveErrror {
		d.putLong(addr, val);
	}
	
	protected long getLong(long addr) throws PrimitiveErrror {
		return d.getLong(addr);
	}
	
	protected void putChar(long addr, char val) throws PrimitiveErrror {
		d.putChar(addr, val);
	}
	
	protected char getChar(long addr) throws PrimitiveErrror {
		return d.getChar(addr);
	}
	
	protected void putByte(long addr, byte val) throws PrimitiveErrror {
		d.putByte(addr, val);
	}
	
	protected byte getByte(long addr) throws PrimitiveErrror {
		return d.getByte(addr);
	}
	
	protected long malloc(long len) throws OutOfMemoryError {
		return d.malloc(len);
	}
	
	protected long realloc(long addr, long len) throws PrimitiveErrror, OutOfMemoryError {
		return d.realloc(addr, len);
	}
	
	protected void free(long addr) throws PrimitiveErrror {
		d.free(addr);
	}
	
	protected void memcpy(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		d.memcpy(srcaddr, dstaddr, len);
	}
	
	protected void memmov(long srcaddr, long dstaddr, long len) throws PrimitiveErrror {
		d.memmov(srcaddr, dstaddr, len);
	}
	
	protected void memset(long addr, long len, byte val) throws PrimitiveErrror {
		d.memset(addr, len, val);
	}
	
	protected void memset(long addr, long len, long val) throws PrimitiveErrror {
		d.memset(addr, len, val);
	}
	
	protected void set(char[] src, long dstaddr, int cc) throws PrimitiveErrror {
		d.set(src, dstaddr, cc);
	}
	
	protected void set(byte[] src, long dstaddr, int bc) throws PrimitiveErrror {
		d.set(src, dstaddr, bc);
	}
	
	protected void cpy(char[] src, char[] dst, int cc) throws PrimitiveErrror {
		d.cpy(src, dst, cc);
	}
	
	protected void cpy(String[] src, String[] dst, int oc) throws PrimitiveErrror {
		d.cpy(src, dst, oc);
	}
	
	protected void cpy(byte[] src, byte[] dst, int bc) throws PrimitiveErrror {
		d.cpy(src, dst, bc);
	}
	
	protected void get(long srcaddr, char[] dst, int cc) throws PrimitiveErrror {
		d.get(srcaddr, dst, cc);
	}
	
	protected void get(long srcaddr, byte[] dst, int bc) throws PrimitiveErrror {
		d.get(srcaddr, dst, bc);
	}
	
	@Override
	protected void get(long srcaddr, byte[] dst, int bo, int bc) throws PrimitiveErrror {
		d.get(srcaddr, dst, bo, bc);
	}
	
	protected void getregs(long dstaddr) throws PrimitiveErrror {
		d.getregs(dstaddr);
	}
	
	protected void setregs(long srcaddr) throws PrimitiveErrror {
		d.setregs(srcaddr);
	}
	
	protected void getregs(byte[] dst) {
		d.getregs(dst);
	}
	
	protected void setregs(byte[] src) {
		d.setregs(src);
	}
	
	@Override
	public void checkmem(long addr, long len) throws PrimitiveErrror {
		d.checkmem(addr, len);
	}
	
}
