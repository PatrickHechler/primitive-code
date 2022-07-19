package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmPreDefines.INT_ERRORS_ILLEGAL_MEMORY;

import java.lang.reflect.Field;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.RegMemExep;
import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class MemoryChecker {
	
	private static final long BASE_OFF = Unsafe.ARRAY_LONG_BASE_OFFSET;
	private static final int  IDX_LEN  = Unsafe.ARRAY_LONG_INDEX_SCALE;
	
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
	
	private long[] starts  = new long[16];
	private long[] lengths = new long[16];
	private long   arrsize = BASE_OFF;
	
	public MemoryChecker() {}
	
	public final void malloc(long address, long length) {
		long start = BASE_OFF,
			end = this.arrsize - IDX_LEN,
			mid;
		if (end < start) {
			if (address <= 0x301) {
				if (address + length >= 0x199) {
					throw new AssertionError();
				}
			}
			U.putLong(starts, start, address);
			U.putLong(lengths, start, length);
			this.arrsize += IDX_LEN;
			return;
		}
		while (true) {
			mid = (start + end) >>> 1;
			long midAddr = U.getLong(starts, mid);
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
			U.copyMemory(os, BASE_OFF, starts, BASE_OFF, len);
			U.copyMemory(ol, BASE_OFF, lengths, BASE_OFF, len);
		}
		long len = arrsize - mid - 8L;
		long next = mid + 8L;
		U.copyMemory(os, mid, starts, next, len);
		U.copyMemory(ol, mid, lengths, next, len);
		U.putLong(starts, mid, address);
		U.putLong(lengths, mid, length);
	}
	
	public final void realloc(long oldAddress, long newAddress, long length) {
		if (oldAddress == newAddress) {
			long a = fEA(oldAddress);
			if (a == -1L) {
				throw new AssertionError();
			}
			U.putLong(lengths, a, length);
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
		U.copyMemory(starts, nextA, starts, a, len);
		U.copyMemory(lengths, nextA, lengths, a, len);
		arrsize -= IDX_LEN;
	}
	
	public final void check(long address, long length) throws PrimitiveErrror, RegMemExep {
		long a = fEA(address);
		if (a == -1L) {
			if (address >= 0x200) {
				if (address + length < 0x300) {
					throw new RegMemExep();
				}
			}
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		long start = U.getLong(starts, a),
			len = U.getLong(lengths, a);
		len -= address - start;
		if (length > len) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
	}
	
	public void chackAllocated(long address) throws PrimitiveErrror {
		long a = fEA(address);
		if (a == -1L) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		long start = U.getLong(starts, a);
		if (start != address) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
	}
	
	// /**
	// * returns the number of available bytes from this address or {@code -1} if the address is invalid
	// *
	// * @param address
	// * @return
	// */
	// public final long avl(long address) {
	// long a = fEA(address);
	// if (a == -1L) return -1L;
	// long length = u.getLong(lengths, a),
	// start = u.getLong(starts, a);
	// long off = address - start;
	// return length - off;
	// }
	//
	private long fEA(long address) {
		long start = BASE_OFF,
			end = this.arrsize - IDX_LEN;
		while (start <= end) {
			long mid = (start + end) >>> 1;
			long midAddr = U.getLong(starts, mid);
			if (midAddr > address) {
				end = mid - IDX_LEN;
			} else if (midAddr < address) {
				long endAddr = midAddr + (U.getLong(lengths, mid) << 3);
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

