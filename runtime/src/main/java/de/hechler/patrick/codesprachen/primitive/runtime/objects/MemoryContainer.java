package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY;

import java.util.Arrays;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;

public final class MemoryContainer {
	
	private static final long DEFAULT_FREE_SPACE = 1024L;
	private static final long MIN_FREE_SPACE     = 8L;
	
	private long[]   starts = new long[16];
	private long[][] blocks = new long[16][];
	private int      length = 0;
	
	public MemoryContainer() {}
	
	public final long malloc(long length) {
		if ( (length & 7) != 0) {
			length &= ~7;
			length += 8;
		}
		if (length < 0L) {
			return -1L;
		}
		final int startLen = this.length;
		final long[] startStarts = this.starts;
		final long[][] startBlocks = this.blocks;
		try {
			if (this.length >= this.starts.length) {
				this.starts = arrayGrow(this.starts, (length / (1L << 33L)) + 1L);
				this.blocks = arrayGrow(this.blocks, (length / (1L << 33L)) + 1L);
			}
			long address = this.length == 0 ? DEFAULT_FREE_SPACE : ( (this.starts[this.length] + DEFAULT_FREE_SPACE) & ~7);
			final long result = address;
			while (length > 0) {
				int len = (int) Math.min(length >> 3, 1 << 30);
				this.blocks[this.length] = new long[len];
				this.starts[this.length ++ ] = address;
				long ncl = ((long) len) << 3L;
				address += ncl;
				length -= ncl;
			}
			return result;
		} catch (OutOfMemoryError e) {
			int endLen = this.length;
			this.length = startLen;
			this.starts = startStarts;
			this.blocks = startBlocks;
			for (int i = startLen; i < endLen; i ++ ) {
				this.blocks[i] = null;
			}
			return -1L;
		}
	}
	
	public final long realloc(long address, long length) throws PrimitiveErrror {
		if ( (length & 7) != 0) {
			length &= ~7;
			length += 8;
		}
		if (length < 0L) {
			return -1L;
		}
		final int startLen = this.length;
		final long[] startStarts = this.starts;
		final long[][] startBlocks = this.blocks;
		try {
			int index = findIndex(address);
			if (this.starts[index] != address) {
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			if (index > 0 && this.starts[index - 1] + ( ((long) this.blocks[index - 1].length) << 3L) >= address) {
				assert this.starts[index - 1] + ( ((long) this.blocks[index - 1].length) << 3L) == address;
				throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
			}
			return doRealloc(address, length, startLen, startStarts, startBlocks, index);
		} catch (OutOfMemoryError e) {
			this.length = startLen;
			this.starts = startStarts;
			this.blocks = startBlocks;
			return -1L;
		}
	}
	
	private long doRealloc(long address, long length, final int startLen, final long[] startStarts, final long[][] startBlocks, int index) {
		long oldLength = this.blocks[index].length;
		long endAddr = address + oldLength;
		int lastIndex = index;
		for (lastIndex ++ ; lastIndex < this.length; lastIndex ++ ) {
			if (this.starts[lastIndex] != endAddr) {
				assert this.starts[lastIndex] > endAddr;
				break;
			}
			int len = this.blocks[lastIndex].length;
			endAddr += len;
			oldLength += len;
		}
		lastIndex -- ;
		long diff = length - oldLength;
		if (diff > 0) {
			return reallocGrow(startLen, startStarts, startBlocks, index, lastIndex, diff);
		} else if (diff == 0) {
			return address;
		} else {
			return reallocShrink(length, index, lastIndex);
		}
	}
	
	private long reallocShrink(long length, int index, int lastIndex) {
		int skip = (int) (length / (1L << 33L));
		int newLastLen = (int) ( (length % (1L << 33L)) >> 3L);
		int off = index + skip;
		if (newLastLen != 0) {
			this.blocks[off] = Arrays.copyOf(this.blocks[off], newLastLen);
			off ++ ;
		}
		int cpylen = lastIndex - off;
		System.arraycopy(this.blocks, lastIndex + 1, this.blocks, off, cpylen);
		this.length -= cpylen;
		for (cpylen -- ; cpylen >= 0; cpylen -- ) {
			this.blocks[this.length + cpylen] = null;
		}
		return this.starts[index];
	}
	
	private long reallocGrow(final int startLen, final long[] startStarts, final long[][] startBlocks, int index, int lastIndex, long diff) {
		if (lastIndex == this.length) {
			return reallocGrowLastEntry(index, lastIndex, diff);
		} else {
			long afterFree = this.starts[lastIndex + 1] - this.starts[lastIndex] - ( ((long) this.blocks[lastIndex].length) << 3L);
			long beforeFree = index > 0 ? this.starts[index - 1] - this.starts[index] - ( ((long) this.blocks[index - 1].length) << 3L) : 0L;
			if (afterFree + afterFree - diff >= MIN_FREE_SPACE << 1L) {
				return reallocGrowInPlace(startLen, startStarts, startBlocks, index, lastIndex, diff, afterFree, beforeFree);
			} else {
				return reallocGrowNewPlace(startLen, startStarts, startBlocks, index, lastIndex, diff);
			}
		}
	}
	
	private long reallocGrowNewPlace(final int startLen, final long[] startStarts, final long[][] startBlocks, int index, int lastIndex, long diff) {
		diff += this.blocks[lastIndex].length;
		long _bc = (diff / (1L << 33)) + 1L;
		if (_bc >= Integer.MAX_VALUE - lastIndex) {
			return -1L;
		}
		int bc = (int) _bc;
		if (this.blocks.length - bc <= this.length) {
			this.starts = new long[this.blocks.length + bc];
			this.blocks = new long[this.blocks.length + bc][];
		} else {
			this.starts = new long[this.blocks.length];
			this.blocks = new long[this.blocks.length][];
		}
		System.arraycopy(startStarts, 0, this.starts, 0, index);
		System.arraycopy(startBlocks, 0, this.blocks, 0, index);
		System.arraycopy(startStarts, lastIndex + 1, this.starts, index, startLen - lastIndex - 1);
		System.arraycopy(startBlocks, lastIndex + 1, this.blocks, index, startLen - lastIndex - 1);
		System.arraycopy(startBlocks, index, this.blocks, index, index - lastIndex + 1);
		long addr = startStarts[startLen - 1] + ( ((long) startBlocks[startLen - 1].length) << 3L) + DEFAULT_FREE_SPACE;
		int off;
		final long result = addr;
		final int offset = startLen - index;
		for (off = index; off < lastIndex; off ++ ) {
			this.blocks[off + offset] = startBlocks[off];
			this.starts[off + offset] = addr;
			addr += 1L << 33L;
		}
		for (off = 0; diff > 0L; off ++ ) {
			long bl = Math.min(diff, 1L << 33L);
			this.blocks[startLen + off] = new long[(int) (bl >> 3L)];
			this.starts[startLen + off] = addr;
			diff -= bl;
			addr += bl;
			this.length ++ ;
		}
		return result;
	}
	
	private long reallocGrowInPlace(final int startLen, final long[] startStarts, final long[][] startBlocks, int index, int lastIndex, long diff, long afterFree, long beforeFree) {
		diff += this.blocks[lastIndex].length;
		long _bc = (diff / (1L << 33)) + 1L;
		if (_bc >= Integer.MAX_VALUE - lastIndex) {
			return -1L;
		}
		int bc = (int) _bc;
		if (this.blocks.length - bc <= this.length) {
			this.starts = new long[this.blocks.length + bc];
			this.blocks = new long[this.blocks.length + bc][];
		} else {
			this.starts = new long[this.blocks.length];
			this.blocks = new long[this.blocks.length][];
		}
		System.arraycopy(startStarts, 0, this.starts, 0, lastIndex + 1);
		System.arraycopy(startBlocks, 0, this.blocks, 0, lastIndex);
		int off = 0;
		this.blocks[lastIndex] = Arrays.copyOf(this.blocks[lastIndex], (int) (Math.min(diff, 1L << 33L) >> 3L));
		long addr = this.starts[lastIndex] + 1L << 33L;
		for (off = 1; diff > 0L; off ++ ) {
			long bl = Math.min(diff, 1L << 33L);
			this.blocks[lastIndex + off] = new long[(int) (bl >> 3L)];
			this.starts[lastIndex + off] = addr;
			diff -= bl;
			addr += bl;
			this.length ++ ;
		}
		System.arraycopy(startStarts, lastIndex + 1, this.starts, lastIndex + off + 1, startLen - lastIndex - 1);
		System.arraycopy(startBlocks, lastIndex + 1, this.blocks, lastIndex + off + 1, startLen - lastIndex - 1);
		if (afterFree - diff < MIN_FREE_SPACE) {
			long subAll = beforeFree >> 1L;
			if (afterFree - diff + subAll < MIN_FREE_SPACE) {
				subAll = diff - afterFree + MIN_FREE_SPACE;
			}
			assert this.starts[index - 1] + ( ((long) this.blocks[index - 1].length) << 3L) + MIN_FREE_SPACE < this.starts[index] - subAll;
			assert subAll > 0L;
			for (int i = index; i < lastIndex + off; i ++ ) {
				this.starts[i] -= subAll;
			}
		}
		return this.starts[index];
	}
	
	private long reallocGrowLastEntry(int index, int lastIndex, long diff) {
		diff += this.blocks[lastIndex].length;
		long _bc = (diff / (1L << 33)) + 1L;
		if (_bc >= Integer.MAX_VALUE - lastIndex) {
			return -1L;
		}
		int bc = (int) _bc;
		if (this.blocks.length - bc <= this.length) {
			this.starts = Arrays.copyOf(this.starts, this.blocks.length + bc);
			this.blocks = Arrays.copyOf(this.blocks, this.blocks.length + bc);
		}
		this.blocks[lastIndex] = Arrays.copyOf(this.blocks[lastIndex], (int) (Math.min(diff, 1L << 33L) >> 3L));
		long addr = this.starts[lastIndex] + 1L << 33;
		for (int off = 1; diff > 0L; off ++ ) {
			long bl = Math.min(diff, 1L << 33L);
			this.blocks[lastIndex + off] = new long[(int) (bl >> 3L)];
			this.starts[lastIndex + off] = addr;
			diff -= bl;
			addr += bl;
			this.length ++ ;
		}
		return this.starts[index];
	}
	
	public final void free(long address) throws PrimitiveErrror {
		int index = findIndex(address);
		if (index > 0 && this.starts[index - 1] + ( ((long) this.blocks[index - 1].length) << 3L) >= address) {
			assert (index > 0 && this.starts[index - 1] + ( ((long) this.blocks[index - 1].length) << 3L) == address);
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		if (this.starts[index] != address) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		int len = 1;
		long addr = this.starts[index];
		addr += ((long) this.blocks[index].length) << 3L;
		for (; index + len < this.length; len ++ ) {
			if (this.starts[index + len] > addr) {
				break;
			}
			assert this.starts[index + len] == addr;
			addr += ((long) this.blocks[index + len].length) << 3L;
		}
		System.arraycopy(this.blocks, index + len, this.blocks, index, len);
		System.arraycopy(this.starts, index + len, this.starts, index, len);
		this.length -= len;
		for (int off = this.length; off < this.length + len; off ++ ) {
			this.blocks[off] = null;
		}
	}
	
	public final long get(long address) throws PrimitiveErrror {
		if ( (address & 7) != 0) {
			throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
		}
		int index = findIndex(address);
		int off = (int) ( (address - this.starts[index]) >> 3L);
		return this.blocks[index][off];
	}
	
	public final int getByte(long address) throws PrimitiveErrror {
		int index = findIndex(address);
		int off = (int) ( (address - this.starts[index]) >> 3L);
		long val = this.blocks[index][off];
		return (int) (0xFF & (val >> ( (address & 7) << 3)));
	}
	
	public final void set(long address, long value) throws PrimitiveErrror {
		int index = findIndex(address);
		int off = (int) (address - this.starts[index]);
		this.blocks[index][off] = value;
	}
	
	public final void setByte(long address, int value) throws PrimitiveErrror {
		assert (value & 0xFF) == value;
		int index = findIndex(address);
		int off = (int) ( (address - this.starts[index]) >> 3L);
		long val = this.blocks[index][off];
		long byteShift = (address & 7) << 3;
		val = (value << byteShift) | (val & ~ (0xFF << byteShift));
		this.blocks[index][off] = val;
	}
	
	// FIXME make these methods faster and not just use get/set/getByte/setByte (use System.arraycopy(...) where possible)
	public final void copy(long fromAddress, long toAddress, long length) throws PrimitiveErrror {
		if ( ( (fromAddress | toAddress | length) & 7) != 0) {
			for (; length > 0L; length -- , fromAddress ++ , toAddress ++ ) {
				setByte(toAddress, getByte(fromAddress));
			}
		} else {
			for (; length > 0L; length -= 8, fromAddress += 8, toAddress += 8) {
				set(toAddress, get(fromAddress));
			}
		}
	}
	
	public final void move(long fromAddress, long toAddress, long length) throws PrimitiveErrror {
		if (fromAddress > toAddress) {
			copy(fromAddress, toAddress, length);
		} else if ( ( (fromAddress | toAddress | length) & 7) != 0) {
			for (length -- ; length >= 0L; length -- ) {
				setByte(toAddress + length, getByte(fromAddress + length));
			}
		} else {
			for (length -= 8; length > 0L; length -= 8) {
				set(toAddress + length, get(fromAddress + length));
			}
		}
	}
	
	public final void membset(long address, long length, int bval) throws PrimitiveErrror {
		assert ( ((int) bval) & 0xFF) == (int) bval;
		if ( ( (address | length) & 7) != 0) {
			for (; length > 0; length -- , address ++ ) {
				setByte(address, bval);
			}
		} else {
			long val = bval | (bval << 8) | (bval << 16) | (bval << 24) | (bval << 32) | (bval << 40) | (bval << 48) | (bval << 56);
			for (; length > 0; length -= 8, address += 8) {
				set(address, val);
			}
		}
	}
	
	public final void memset(long address, long length, long val) throws PrimitiveErrror {
		if ( ( (address | length) & 7) != 0) {
			int b1 = (int) (val & 0xFF), b2 = (int) ( (val >> 8) & 0xFF), b3 = (int) ( (val >> 16) & 0xFF), b4 = (int) ( (val >> 24) & 0xFF),
				b5 = (int) ( (val >> 32) & 0xFF), b6 = (int) ( (val >> 40) & 0xFF), b7 = (int) ( (val >> 48) & 0xFF), b8 = (int) ( (val >> 56) & 0xFF);
			for (; length > 0; length -- ) {
				setByte(address ++ , b1);
				setByte(address ++ , b2);
				setByte(address ++ , b3);
				setByte(address ++ , b4);
				setByte(address ++ , b5);
				setByte(address ++ , b6);
				setByte(address ++ , b7);
				setByte(address ++ , b8);
			}
		} else {
			for (; length > 0; length -= 8, address += 8) {
				set(address, val);
			}
		}
	}
	
	private final long[] arrayGrow(long[] array, long minNewLen) {
		long _newLen = Math.max(this.length + this.length >> 1, minNewLen);
		int newLen = (int) _newLen;
		if (newLen != _newLen) {
			throw new OutOfMemoryError();
		}
		long[] copy = new long[newLen];
		System.arraycopy(array, 0, copy, 0, this.length);
		return copy;
	}
	
	private final long[][] arrayGrow(long[][] array, long minNewLen) {
		long _newLen = Math.max(this.length + this.length >> 1, minNewLen);
		int newLen = (int) _newLen;
		if (newLen != _newLen) {
			throw new OutOfMemoryError();
		}
		long[][] copy = new long[newLen][];
		System.arraycopy(array, 0, copy, 0, this.length);
		return copy;
	}
	
	private int findIndex(long address) throws PrimitiveErrror {
		int start = 0,
			end = this.length - 1;
		while (start <= end) {
			int mid = (start + end) >>> 1;
			long midAddr = this.starts[mid];
			if (midAddr > address) {
				end = mid - 1;
			} else if (midAddr < address) {
				long endAddr = midAddr + (this.blocks[mid].length << 3);
				if (endAddr > address) {
					return mid;
				}
				start = mid + 1;
			} else {
				return mid;
			}
		}
		throw new PrimitiveErrror(INT_ERRORS_ILLEGAL_MEMORY);
	}
	
}

