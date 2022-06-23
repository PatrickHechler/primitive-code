package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakHandle;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakIter;

public class PosBreakHandleImpl implements BreakHandle {
	
	/**
	 * the container for the breakpoints.<br>
	 * <code>-1</code> values stand for empty entries.
	 */
	protected long[] ls   = new long[16];
	/**
	 * the number of entries in {@link #ls}
	 */
	private int      size = 0;
	/**
	 * the number of modifications<br>
	 * since an overflow does not really affect the use of this value it can be an {@code int}
	 */
	private int      mods;
	
	public PosBreakHandleImpl() {
		Arrays.fill(ls, -1L);
	}
	
	@Override
	public synchronized boolean add(long newStop) {
		if (newStop == -1L) {
			throw new IllegalArgumentException("-1 address");
		}
		mods ++ ;
		int i = index(newStop);
		if (size > ls.length >> 1) {
			grow();
		}
		for (;;) {
			if (ls[i] == -1L) {
				ls[i] = newStop;
				return true;
			} else if (ls[i] == newStop) {
				return false;
			} else {
				i = inc(i);
			}
		}
	}
	
	@Override
	public synchronized boolean remove(long formerStop) {
		int i = index(formerStop);
		for (;; i = inc(i)) {
			if (ls[i] == -1L) {
				return false;
			} else if (ls[i] == formerStop) {
				remove(i);
				return true;
			}
		}
	}
	
	@Override
	public synchronized boolean contains(long stop) {
		int i = index(stop);
		for (;; i = inc(i)) {
			if (ls[i] == -1L) {
				return false;
			} else if (ls[i] == stop) {
				return true;
			}
		}
	}
	
	@Override
	public synchronized BreakIter iter() {
		return new BreakIter() {
			
			/**
			 * the current index in the {@link PosBreakHandleImpl#ls} array
			 */
			private int     i     = 0;
			/**
			 * the number of breakpoints which had been read already
			 */
			private int     r     = 0;
			/**
			 * the expected value of {@link PosBreakHandleImpl#mods}
			 */
			private int     emods = mods;
			/**
			 * <code>true</code> when {@link #remove()} is allowed to be called and <code>false</code> if not
			 */
			private boolean d     = false;
			
			@Override
			public Long next() {
				return nextBreak();
			}
			
			@Override
			public boolean hasNext() {
				synchronized (PosBreakHandleImpl.this) {
					return r < size;
				}
			}
			
			@Override
			public void remove() {
				synchronized (PosBreakHandleImpl.this) {
					if ( !d) throw new IllegalStateException("no next before remove");
					if (emods != mods) throw new ConcurrentModificationException("I have been modified");
					if (r >= size) throw new NoSuchElementException("fully iterated");
					r -- ;
					if (i != 0) i -- ;
					else i = ls.length - 1;
					PosBreakHandleImpl.this.remove(i);
					emods = mods;
					d = false;
				}
			}
			
			@Override
			public long nextBreak() {
				synchronized (PosBreakHandleImpl.this) {
					if (emods != mods) throw new ConcurrentModificationException("I have been modified");
					if (r >= size) throw new NoSuchElementException("fully iterated");
					r ++ ;
					for (;; i = inc(i)) {
						long val = ls[i];
						if (val != -1L) {
							i = inc(i);
							d = true;
							return val;
						}
					}
				}
			}
			
		};
	}
	
	@Override
	public synchronized Iterator <Long> iterator() {
		return iter();
	}
	
	@Override
	public synchronized int size() {
		return size;
	}
	
	@Override
	public synchronized boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public synchronized Object[] toArray() {
		Object[] a = new Object[size];
		int i = 0;
		for (Long l : this) {
			a[i ++ ] = l;
		}
		return a;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public synchronized <T> T[] toArray(T[] a) {
		if (a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		} else if (a.length > size) {
			a[size] = null;
		}
		int i = 0;
		for (Long l : this) {
			a[i ++ ] = (T) l;
		}
		return a;
	}
	
	@Override
	public synchronized boolean containsAll(Collection <?> c) {
		for (Object obj : c) {
			if (contains(obj)) continue;
			return false;
		}
		return true;
	}
	
	@Override
	public synchronized boolean addAll(Collection <? extends Long> c) {
		int s = size;
		if (c.contains(null)) throw new NullPointerException("can not add null elements");
		for (Long l : c) {
			add((long) l);
		}
		return s != size;
	}
	
	@Override
	public synchronized boolean retainAll(Collection <?> c) {
		int m = mods;
		for (BreakIter iter = iter(); iter.hasNext();) {
			if (c.contains(iter.next())) continue;
			iter.remove();
		}
		return m != mods;
	}
	
	@Override
	public synchronized boolean removeAll(Collection <?> c) {
		int m = mods;
		for (Object obj : c) {
			remove(obj);
		}
		return m != mods;
	}
	
	@Override
	public synchronized void clear() {
		Arrays.fill(ls, -1L);
		size = 0;
		mods ++ ;
	}
	
	protected int index(long breakPoint) {
		return (int) ( (ls.length - 1) & (breakPoint >> 3));
	}
	
	private void grow() {
		long[] o = ls,
			n = new long[o.length << 1];
		Arrays.fill(n, -1L);
		ls = n;
		for (int i = 0; i < o.length; i ++ ) {
			long val = o[i];
			if (val == -1) continue;
			add(val);
		}
	}
	
	private void remove(int index) {
		mods ++ ;
		size -- ;
		for (int i = index;; index = i) {
			i = inc(i);
			long val = ls[index];
			ls[i] = val;
			if (val == -1L) {
				break;
			}
		}
	}
	
	private final int inc(int i) {
		i ++ ;
		if (i >= ls.length) {
			assert i == ls.length;
			i = 0;
		}
		return i;
	}
	
}
