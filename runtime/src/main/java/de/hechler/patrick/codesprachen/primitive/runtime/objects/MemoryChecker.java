package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryChecker {
	
	private static final long BASE_OFF = Unsafe.ARRAY_LONG_BASE_OFFSET;
	private static final int  IDX_LEN  = Unsafe.ARRAY_LONG_INDEX_SCALE;
	
	private final Unsafe u;
	
	private long[] starts  = new long[16];
	private long[] lengths = new long[16];
	private long   arrsize = BASE_OFF;
	
	public MemoryChecker(Unsafe u) {
		this.u = u;
	}
	
	public final void malloc(long address, long length) {
		long start = BASE_OFF,
			end = this.arrsize - IDX_LEN,
			mid;
		if (end < start) {
			u.putLong(starts, start, address);
			u.putLong(lengths, start, length);
			return;
		}
		while (true) {
			mid = (start + end) >>> 1;
			long midAddr = u.getLong(starts, mid);
			if (midAddr > address) {
				end = mid - IDX_LEN;
				if (start <= end) continue;
				break;
			} else {
				start = mid + IDX_LEN;
				if (start <= end) continue;
				break;
			}
		}
		long as = ((long) lengths.length) * (long) IDX_LEN + BASE_OFF;
		long nas = arrsize + IDX_LEN;
		long[] os = starts,
			ol = lengths;
		if (as > nas) {
			starts = new long[lengths.length + lengths.length >> 1];
			lengths = new long[lengths.length + lengths.length >> 1];
			long len = mid - BASE_OFF;
			u.copyMemory(os, BASE_OFF, starts, BASE_OFF, len);
			u.copyMemory(ol, BASE_OFF, lengths, BASE_OFF, len);
		}
		long len = arrsize - mid - 8L;
		long next = mid + 8L;
		u.copyMemory(os, mid, starts, next, len);
		u.copyMemory(ol, mid, lengths, next, len);
		u.putLong(starts, mid, address);
		u.putLong(lengths, mid, length);
	}
	
	public final void realloc(long oldAddress, long newAddress, long length) {
		if (oldAddress == newAddress) {
			long a = fEA(oldAddress);
			if (a == -1L) {
				throw new AssertionError();
			}
			u.putLong(lengths, a, length);
		} else {
			free(oldAddress);
			malloc(newAddress, length);
		}
	}
	
	public final void free(long address) {
		long a = fEA(address);
		if (a == -1L) {
			throw new AssertionError();
		}
		long len = this.arrsize - a;
		long nextA = a + IDX_LEN;
		u.copyMemory(starts, nextA, starts, a, len);
		u.copyMemory(lengths, nextA, lengths, a, len);
		arrsize -= IDX_LEN;
	}
	
	public final void check(long address, long length) throws PrimitiveErrror {
		long a = fEA(address);
		if (a == -1L) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		long start = u.getLong(starts, a),
			len = u.getLong(lengths, a);
		len -= address - start;
		if (length > len) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
	}
	
	public void chackAllocated(long address, long length) throws PrimitiveErrror {
		long a = fEA(address);
		if (a == -1L) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		long start = u.getLong(starts, a),
			len = u.getLong(lengths, a);
		if (length != len || start != address) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
	}
	
	public void chackAllocated(long address) throws PrimitiveErrror {
		long a = fEA(address);
		if (a == -1L) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		long start = u.getLong(starts, a);
		if (start != address) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
	}
	
	/**
	 * returns the number of available bytes from this address or {@code -1} if the address is invalid
	 * 
	 * @param address
	 * @return
	 */
	public final long avl(long address) {
		long a = fEA(address);
		if (a == -1L) return -1L;
		long length = u.getLong(lengths, a),
			start = u.getLong(starts, a);
		long off = address - start;
		return length - off;
	}
	
	private long fEA(long address) {
		long start = BASE_OFF,
			end = this.arrsize - IDX_LEN;
		while (start <= end) {
			long mid = (start + end) >>> 1;
			long midAddr = u.getLong(starts, mid);
			if (midAddr > address) {
				end = mid - IDX_LEN;
			} else if (midAddr < address) {
				long endAddr = midAddr + (u.getLong(lengths, mid) << 3);
				if (endAddr > address) {
					return mid;
				}
				start = mid + IDX_LEN;
			} else {
				return mid;
			}
		}
		return -1L;
	}
	
}

