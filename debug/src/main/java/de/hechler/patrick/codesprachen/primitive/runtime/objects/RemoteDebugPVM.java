package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.pfs.utils.ConvertNumByteArr.*;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.InputMismatchException;

import de.hechler.patrick.codesprachen.primitive.runtime.enums.DebugState;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakHandle;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakIter;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.DebugPVM;

public class RemoteDebugPVM implements DebugPVM, Closeable {
	
	
	private final SocketChannel sok;
	private final SocketAddress addr;
	private final ByteBuffer    buf = ByteBuffer.allocate(24);
	
	public RemoteDebugPVM(SocketChannel debug, SocketAddress addr) {
		this.sok = debug;
		this.addr = addr;
		this.buf.limit(8);
	}
	
	public static RemoteDebugPVM create(SocketAddress addr) throws IOException {
		SocketChannel debug = SocketChannel.open(addr);
		debug.configureBlocking(true);
		RemoteDebugPVM debugpvm = new RemoteDebugPVM(debug, addr);
		debugpvm.writeRead(DEBUG_CONNECT_MAGIC);
		return debugpvm;
	}
	
	@Override
	public synchronized DebugState state() {
		try {
			longToByteArr(buf.array(), 0, GET_STATE_MAGIC);
			sok.write(buf);
			long val = read(RUNNING_STATE_MAGIC, STEPPING_STATE_MAGIC, WAITING_STATE_MAGIC);
			switch ((int) val) {
			case (int) RUNNING_STATE_MAGIC:
				return DebugState.running;
			case (int) STEPPING_STATE_MAGIC:
				return DebugState.stepping;
			case (int) WAITING_STATE_MAGIC:
				return DebugState.waiting;
			default:
				throw new InternalError();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void run() {
		writeRead(RUN_MAGIC);
	}
	
	@Override
	public synchronized void step() {
		writeRead(STEP_MAGIC);
	}
	
	@Override
	public synchronized void step(long deep) {
		try {
			buf.limit(16);
			longToByteArr(buf.array(), 0, STEP_DEEP_MAGIC);
			longToByteArr(buf.array(), 8, deep);
			sok.write(buf);
			buf.limit(8);
			read(STEP_DEEP_MAGIC);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void stop() {
		writeRead(WAIT_MAGIC);
	}
	
	@Override
	public synchronized BreakHandle posBreakHandle() {
		return new RemoteBreakHandle(HAS_POS_BREAK_MAGIC, REM_POS_BREAK_MAGIC, ADD_POS_BREAK_MAGIC, GET_POS_BREAKS_MAGIC, GET_POS_BREAK_COUNT_MAGIC);
	}
	
	@Override
	public synchronized BreakHandle defIntBreakHandle() {
		return new RemoteBreakHandle(HAS_DEF_INT_BREAK_MAGIC, REM_DEF_INT_BREAK_MAGIC, ADD_DEF_INT_BREAK_MAGIC, GET_DEF_INT_BREAKS_MAGIC, GET_DEF_INT_BREAK_COUNT_MAGIC);
	}
	
	@Override
	public synchronized BreakHandle allIntBreakHandle() {
		return new RemoteBreakHandle(HAS_ALL_INT_BREAK_MAGIC, REM_ALL_INT_BREAK_MAGIC, ADD_ALL_INT_BREAK_MAGIC, GET_ALL_INT_BREAKS_MAGIC, GET_ALL_INT_BREAK_COUNT_MAGIC);
	}
	
	@Override
	public synchronized void putPVM(byte[] buf) throws IllegalArgumentException, IllegalStateException {
		if (buf.length != 256) {
			throw new IllegalArgumentException("buf.len!=256: " + buf.length);
		}
		try {
			longToByteArr(this.buf.array(), 0, SET_SN_MAGIC);
			sok.write(this.buf);
			sok.write(ByteBuffer.wrap(buf));
			long val = read(SET_SN_MAGIC, NOT_WAITING_MAGIC);
			switch ((int) val) {
			case (int) SET_SN_MAGIC:
				return;
			case (int) NOT_WAITING_MAGIC:
				throw new IllegalStateException("set pvm snapshot only on waiting state allowed");
			default:
				throw new InternalError();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void getPVM(byte[] buf) throws IllegalArgumentException {
		try {
			if (buf.length != 256) {
				throw new IllegalArgumentException("buf.len=" + buf.length + "!=256");
			}
			writeRead(GET_SN_MAGIC);
			sok.write(ByteBuffer.wrap(buf));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void getMem(long addr, byte[] buf, int len) throws IllegalArgumentException {
		try {
			if (buf.length < len) {
				throw new IllegalArgumentException("buf.len=" + buf.length + ", len=" + len);
			}
			writeRead(GET_SN_MAGIC);
			sok.read(ByteBuffer.wrap(buf, 0, len));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void getMem(long addr, byte[] buf, int boff, int len) throws IllegalArgumentException {
		try {
			if (buf.length - len - boff < 0 || boff < 0 || len < 0) {
				throw new IllegalArgumentException("buf.len=" + buf.length + " boff=" + boff + " len=" + len);
			}
			byte[] bytes = this.buf.array();
			longToByteArr(bytes, 0, GET_MEM_MAGIC);
			longToByteArr(bytes, 8, addr);
			longToByteArr(bytes, 16, len);
			this.buf.limit(24);
			sok.write(this.buf);
			this.buf.position(0);
			this.buf.limit(8);
			if (read(GET_MEM_MAGIC, INVALID_VALUE_MAGIC) != GET_MEM_MAGIC) {
				throw new IllegalArgumentException();
			}
			sok.read(ByteBuffer.wrap(bytes, boff, len));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void setMem(long addr, byte[] buf, int len) throws IllegalArgumentException {
		try {
			if (len > buf.length) {
				throw new IllegalArgumentException("len>buf.len: len=" + len + " buf.len=" + buf.length);
			}
			this.buf.position(0);
			this.buf.limit(24);
			byte[] bytes = this.buf.array();
			longToByteArr(bytes, 0, SET_MEM_MAGIC);
			longToByteArr(bytes, 8, addr);
			longToByteArr(bytes, 16, (long) len);
			sok.write(this.buf);
			sok.write(ByteBuffer.wrap(buf, 0, len));
			this.buf.position(0);
			this.buf.limit(8);
			if (read(SET_MEM_MAGIC, NOT_WAITING_MAGIC) == NOT_WAITING_MAGIC) {
				throw new IllegalStateException("not waiting");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized long mallocMemory(long len) throws OutOfMemoryError {
		try {
			buf.position(0);
			buf.limit(16);
			byte[] bytes = buf.array();
			longToByteArr(bytes, 0, REALLOC_MEMORY_MAGIC);
			longToByteArr(bytes, 8, len);
			sok.write(buf);
			buf.limit(8);
			switch ((int) read(MALLOC_MEMORY_MAGIC, NOTHING_DONE_MAGIC, INVALID_VALUE_MAGIC)) {
			case (int) MALLOC_MEMORY_MAGIC:
				buf.position(0);
				sok.read(buf);
				return byteArrToLong(bytes, 0);
			case (int) NOTHING_DONE_MAGIC:
				buf.position(0);
				read( -1);
				throw new OutOfMemoryError();
			case (int) INVALID_VALUE_MAGIC:
				buf.position(0);
				read( -1);
				throw new IllegalArgumentException("len <= 0: len=" + len);
			default:
				throw new InternalError();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized long reallocMemory(long addr, long len) throws OutOfMemoryError, IllegalArgumentException {
		try {
			buf.position(0);
			buf.limit(24);
			byte[] bytes = buf.array();
			longToByteArr(bytes, 0, REALLOC_MEMORY_MAGIC);
			longToByteArr(bytes, 8, addr);
			longToByteArr(bytes, 16, len);
			sok.write(buf);
			buf.limit(8);
			switch ((int) read(REALLOC_MEMORY_MAGIC, NOTHING_DONE_MAGIC, INVALID_VALUE_MAGIC)) {
			case (int) REALLOC_MEMORY_MAGIC:
				buf.position(0);
				sok.read(buf);
				return byteArrToLong(bytes, 0);
			case (int) NOTHING_DONE_MAGIC:
				buf.position(0);
				read( -1);
				throw new OutOfMemoryError();
			case (int) INVALID_VALUE_MAGIC:
				buf.position(0);
				read( -1);
				throw new IllegalArgumentException();
			default:
				throw new InternalError();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void freeMemory(long addr) throws IllegalArgumentException {
		try {
			buf.position(0);
			buf.limit(16);
			byte[] bytes = buf.array();
			longToByteArr(bytes, 0, FREE_MEMORY_MAGIC);
			longToByteArr(bytes, 8, addr);
			sok.write(buf);
			buf.limit(8);
			if (read(FREE_MEMORY_MAGIC, NOTHING_DONE_MAGIC) == NOTHING_DONE_MAGIC) {
				throw new IllegalArgumentException();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized void memcheck(long addr, long len) throws IllegalArgumentException {
		if (len <= 0L) {
			throw new IllegalArgumentException("len < 0: len=" + len);
		}
		try {
			buf.position(0);
			buf.limit(24);
			byte[] bytes = buf.array();
			longToByteArr(bytes, 0, MEM_CHECK_MAGIC);
			longToByteArr(bytes, 8, addr);
			longToByteArr(bytes, 16, len);
			sok.write(buf);
			buf.limit(8);
			if (read(MEM_CHECK_MAGIC, NOTHING_DONE_MAGIC) != MEM_CHECK_MAGIC) {
				throw new IllegalArgumentException();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public synchronized OutputStream stdin() throws IOException {
		SocketChannel c = SocketChannel.open(addr);
		writeRead(c, STD_IN_CONNECT_MAGIC);
		return asOutStream(c);
	}
	
	@Override
	public synchronized InputStream stdout() throws IOException {
		SocketChannel c = SocketChannel.open(addr);
		writeRead(c, STD_OUT_CONNECT_MAGIC);
		return asInStream(c);
	}
	
	@Override
	public synchronized InputStream stdlog() throws IOException {
		SocketChannel c = SocketChannel.open(addr);
		writeRead(c, STD_ERR_CONNECT_MAGIC);
		return asInStream(c);
	}
	
	private InputStream asInStream(SocketChannel c) {
		return new InputStream() {
			
			@Override
			public int read() throws IOException {
				byte[] b = new byte[1];
				int r = read(b);
				if (r == 0) {
					if (c.isConnected()) {
						throw new IOException();
					} else {
						return -1;
					}
				}
				return b[0] & 0xFF;
			}
			
			@Override
			public int read(byte[] b) throws IOException {
				return c.read(ByteBuffer.wrap(b));
			}
			
			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				return c.read(ByteBuffer.wrap(b, off, len));
			}
			
		};
	}
	
	private OutputStream asOutStream(SocketChannel c) {
		return new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				write(new byte[] {(byte) b });
			}
			
			@Override
			public void write(byte[] b) throws IOException {
				c.write(ByteBuffer.wrap(b));
			}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				c.write(ByteBuffer.wrap(b, off, len));
			}
			
		};
	}
	
	private void writeRead(long val) {
		try {
			longToByteArr(buf.array(), 0, val);
			sok.write(buf);
			read(val);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private synchronized long read(long... allowed) throws IOException {
		int r = sok.read(buf);
		if (r != 8) {
			throw new AssertionError(r);
		}
		long val = byteArrToLong(buf.array(), buf.position() - 8);
		for (int i = 0; i < allowed.length; i ++ ) {
			if (val == allowed[i]) {
				buf.position(0);
				return val;
			}
		}
		throw new InputMismatchException("val=" + val + " allowed=" + Arrays.toString(allowed));
	}
	
	private void writeRead(SocketChannel sok, long val) {
		try {
			longToByteArr(buf.array(), 0, val);
			sok.write(buf);
			read(sok, val);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private synchronized long read(SocketChannel sok, long... allowed) throws IOException {
		int r = sok.read(buf);
		if (r != 8) {
			throw new AssertionError(r);
		}
		long val = byteArrToLong(buf.array(), buf.position() - 8);
		for (int i = 0; i < allowed.length; i ++ ) {
			if (val == allowed[i]) {
				buf.position(0);
				return val;
			}
		}
		throw new InputMismatchException("val=" + val + " allowed=" + Arrays.toString(allowed));
	}
	
	@Override
	public synchronized void close() throws IOException {
		writeRead(DISCONNECT_MAGIC);
		sok.close();
	}
	
	public class RemoteBreakHandle implements BreakHandle {
		
		private final long hasBreak, remBreak, addBreak, getBreaks, getCount;
		
		public RemoteBreakHandle(long hasBreak, long remBreak, long addBreak, long getBreaks, long getCount) {
			this.hasBreak = hasBreak;
			this.remBreak = remBreak;
			this.addBreak = addBreak;
			this.getBreaks = getBreaks;
			this.getCount = getCount;
		}
		
		@Override
		public boolean isEmpty() {
			return size() == 0;
		}
		
		@Override
		public Object[] toArray() {
			return toArray(new Object[0]);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			synchronized (RemoteBreakHandle.this) {
				boolean grow = false;
				int i = 0;
				for (BreakIter iter = iter(); iter.hasNext();) {
					if (i >= a.length) {
						grow = true;
						a = Arrays.copyOf(a, i + i >>> 1);
					}
					a[i ++ ] = (T) iter.next();
				}
				if (grow) {
					a = Arrays.copyOf(a, i);
				} else if (a.length > i) {
					a[i] = null;
				}
				return a;
			}
		}
		
		@Override
		public boolean containsAll(Collection <?> c) {
			for (Object obj : c) {
				if (obj == null) return false;
				if ( ! (obj instanceof Long)) return false;
				if ( !contains((long) (Long) obj)) return false;
			}
			return true;
		}
		
		@Override
		public boolean addAll(Collection <? extends Long> c) {
			boolean mod = false;
			for (Long l : c) {
				if (l == null) throw new NullPointerException("no null values permitted!");
				mod |= add((long) l);
			}
			return mod;
		}
		
		@Override
		public boolean retainAll(Collection <?> c) {
			boolean mod = false;
			for (BreakIter iter = iter(); iter.hasNext();) {
				Long l = iter.next();
				if (c.contains(l)) continue;
				remove((long) l);
				mod = true;
			}
			return mod;
		}
		
		@Override
		public boolean removeAll(Collection <?> c) {
			boolean mod = false;
			for (Object o : c) {
				if (o == null) continue;
				if ( ! (o instanceof Long)) continue;
				long l = (long) (Long) o;
				mod |= remove((long) l);
			}
			return mod;
		}
		
		@Override
		public boolean add(long newStop) {
			if (newStop == -1L) {
				throw new IllegalArgumentException("-1 is not allowed");
			}
			synchronized (RemoteDebugPVM.this) {
				try {
					buf.position(0);
					buf.limit(16);
					longToByteArr(buf.array(), 0, addBreak);
					longToByteArr(buf.array(), 8, newStop);
					sok.write(buf);
					buf.position(0);
					buf.limit(8);
					if (read(addBreak, NOTHING_DONE_MAGIC) == NOTHING_DONE_MAGIC) {
						return false;
					} else {
						return true;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public boolean remove(long formerStop) {
			synchronized (RemoteDebugPVM.this) {
				try {
					buf.position(0);
					buf.limit(16);
					longToByteArr(buf.array(), 0, remBreak);
					longToByteArr(buf.array(), 8, formerStop);
					sok.write(buf);
					buf.position(0);
					buf.limit(8);
					if (read(remBreak, NOTHING_DONE_MAGIC) == NOTHING_DONE_MAGIC) {
						return false;
					} else {
						return true;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public boolean contains(long stop) {
			synchronized (RemoteDebugPVM.this) {
				try {
					buf.position(0);
					buf.limit(16);
					longToByteArr(buf.array(), 0, hasBreak);
					longToByteArr(buf.array(), 8, stop);
					sok.write(buf);
					buf.position(0);
					buf.limit(8);
					if (read(hasBreak, NOTHING_DONE_MAGIC) == NOTHING_DONE_MAGIC) {
						return false;
					} else {
						return true;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public int size() {
			synchronized (RemoteDebugPVM.this) {
				try {
					buf.position(0);
					buf.limit(8);
					writeRead(getCount);
					buf.position(0);
					sok.read(buf);
					long s = byteArrToLong(buf.array(), 0);
					return (int) Math.min(s, Integer.MAX_VALUE);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		@Override
		public BreakIter iter() {
			try {
				synchronized (RemoteBreakHandle.this) {
					long[] ls = new long[size()];
					buf.position(0);
					writeRead(getBreaks);
					byte[] bytes = buf.array();
					int i;
					for (i = 0;; i ++ ) {
						buf.position(0);
						sok.read(buf);
						long l = byteArrToLong(bytes, 0);
						if (l == -1) break;
						if (ls.length <= i) {
							ls = Arrays.copyOf(ls, i + 4);// should not bee much since the length is asked at the start
						}
						ls[i] = l;
					}
					final long[] fls = ls;
					final int end = i;
					return new BreakIter() {
						
						int i;
						
						@Override
						public Long next() {
							return fls[i ++ ];
						}
						
						@Override
						public boolean hasNext() {
							return i < end;
						}
						
						@Override
						public void remove() {
							if (i <= 0) {
								throw new IllegalStateException("I am at the first entry, there is no pervios entry to remove");
							}
							if (fls[i - 1] == -1L) {
								throw new IllegalStateException("already removed previeus breakpoint");
							}
							RemoteBreakHandle.this.remove(fls[i - 1]);
							fls[i - 1] = -1L;
						}
						
						@Override
						public long nextBreak() {
							return fls[i ++ ];
						}
						
					};
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void clear() {
			for (BreakIter iter = iter(); iter.hasNext();) {
				iter.nextBreak();
				iter.remove();
			}
		}
		
	}
	
}
