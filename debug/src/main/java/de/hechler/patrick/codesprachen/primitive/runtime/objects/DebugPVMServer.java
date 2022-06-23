package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.*;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.ADD_DEF_INT_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.ADD_POS_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.DEBUG_CONNECT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.DISCONNECT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_ALL_INT_BREAKS_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_ALL_INT_BREAK_COUNT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_DEF_INT_BREAKS_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_DEF_INT_BREAK_COUNT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_MEM_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_POS_BREAKS_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_POS_BREAK_COUNT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.GET_SN_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.HAS_ALL_INT_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.HAS_DEF_INT_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.HAS_POS_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.ILLEGAL_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.NOTHING_DONE_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.NOT_WAITING_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.REM_ALL_INT_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.REM_DEF_INT_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.REM_POS_BREAK_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.RUN_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.SET_MEM_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.SET_SN_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.STD_ERR_CONNECT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.STD_IN_CONNECT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.STD_OUT_CONNECT_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.STEP_DEEP_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.STEP_MAGIC;
import static de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants.WAIT_MAGIC;
import static de.hechler.patrick.pfs.utils.ConvertNumByteArr.byteArrToLong;
import static de.hechler.patrick.pfs.utils.ConvertNumByteArr.longToByteArr;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.hechler.patrick.codesprachen.primitive.runtime.enums.DebugState;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakHandle;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakIter;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.DebugPVM;
import de.hechler.patrick.codesprachen.primitive.runtime.utils.PVMDebugConstants;

public class DebugPVMServer implements Runnable {
	
	/** debug worker id */
	private long dwid = 0;
	
	private final DebugPVM                     pvm;
	private final Map <SocketChannel, Connect> cons    = new HashMap <>();
	private final int                          port;
	private final ServerSocketChannel          sok;
	private final Selector                     select;
	private final Queue <PVMTask>              tasks   = new ConcurrentLinkedQueue <>();
	private volatile int                       workers = 0;
	private final DebugOutStr                  stdout;
	private final DebugOutStr                  stdlog;
	private final OutputStream                 stdin;
	
	public DebugPVMServer(DebugPVM pvm, int port, DebugOutStr stdout, DebugOutStr stdlog, OutputStream stdin) {
		this.pvm = pvm;
		this.port = port;
		this.stdout = stdout;
		this.stdlog = stdlog;
		this.stdin = stdin;
		try {
			this.sok = ServerSocketChannel.open();
			this.select = Selector.open();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	
	public InetSocketAddress listeningAddr() {
		try {
			return (InetSocketAddress) sok.getLocalAddress();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	@Override
	public void run() {
		try {
			sok.configureBlocking(false);
			InetSocketAddress addr = new InetSocketAddress(port);
			sok.bind(addr);
			sok.register(select, SelectionKey.OP_ACCEPT);
			select.select();
			while (true) {
				Set <SelectionKey> selected = select.selectedKeys();
				int selectedLen = selected.size();
				if (selectedLen == 0) {
					select.select();
					continue;
				}
				for (Iterator <SelectionKey> iter = selected.iterator(); iter.hasNext();) {
					SelectionKey key = iter.next();
					if (key.isAcceptable()) {
						SocketChannel connect = sok.accept();
						connect.register(select, SelectionKey.OP_READ);
						Connect c = new Connect();
						c.buf.limit(8);
						cons.put(connect, c);
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						Connect con = cons.get(channel);
						channel.read(con.buf);
						if (con.buf.capacity() == 0) {
							con.buf.rewind();
							switch (con.state) {
							case STATE_UNCONNECTED: {
								tasks.add(new ConnectTask(channel, con));
								break;
							}
							case STATE_REALLOC: {
								tasks.add(new ReallocTask(channel, con));
								break;
							}
							case STATE_MALLOC: {
								tasks.add(new MallocTask(channel, con));
								break;
							}
							case STATE_FREE_MEM: {
								tasks.add(new FreeMemTask(channel, con));
								break;
							}
							case STATE_SET_SN: {
								tasks.add(new SetSNTask(channel, con));
								break;
							}
							case STATE_DISCARD: {
								tasks.add(new DiscardTask(channel, con));
								break;
							}
							case STATE_SET_MEM: {
								tasks.add(new SetMemTask(channel, con));
								break;
							}
							case STATE_GET_MEM: {
								tasks.add(new GetMemTask(channel, con));
								break;
							}
							case STATE_CHECK_MEM: {
								tasks.add(new CheckMemTask(channel, con));
								break;
							}
							case STATE_ADD_POS_BREAK:
							case STATE_ADD_DI_BREAK:
							case STATE_ADD_AI_BREAK: {
								tasks.add(new AddBreakTask(channel, con));
								break;
							}
							case STATE_REM_POS_BREAK:
							case STATE_REM_DI_BREAK:
							case STATE_REM_AI_BREAK: {
								tasks.add(new RemBreakTask(channel, con));
								break;
							}
							case STATE_HAS_POS_BREAK:
							case STATE_HAS_DI_BREAK:
							case STATE_HAS_AI_BREAK: {
								tasks.add(new HasBreakTask(channel, con));
								break;
							}
							case STATE_DEEP_STEP: {
								tasks.add(new StepDeepTask(channel, con));
								break;
							}
							case STATE_CONNECTED: {
								tasks.add(new ConnectedFindTaskTask(channel, con));
								break;
							}
							case STATE_STD_IN: {
								tasks.add(new StdinTask(con));
								break;
							}
							case STATE_STD_LOG:
							case STATE_STD_OUT: {
								tasks.add(new DisconnectTask(channel, con, false));
								break;
							}
							case STATE_DISCONNECTED:
								throw new AssertionError("disconnected socket sent data");
							default:
								throw new InternalError("unknown state: " + con.state);
							}
						}
						
					}
				}
				executeTasks(workers);
				int num = select.selectNow();
				if (num > 0) {
					while (num > 0) {
						startNewWorker();
						num /= selectedLen + 1; // shrink even if selected len is one
					}
				} else {
					select.select();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void executeTasks(int stopsize) {
		while (stopsize == 0 ? !tasks.isEmpty() : tasks.size() > stopsize) {
			PVMTask task = tasks.poll();
			if (task == null) break; // last entry removed between isEmpty and poll
			synchronized (task.con) {
				task.run();
			}
		}
	}
	
	private synchronized void startNewWorker() {
		if (Runtime.getRuntime().availableProcessors() - workers - 2 <= 0) { // [-2]: one for this thread and one for the PVM
			return;
		}
		workers ++ ;
		new Thread(() -> {
			while (true) {
				boolean used = false;
				synchronized (DebugPVMServer.this) {
					if (tasks.isEmpty()) {
						try {
							DebugPVMServer.this.wait(5000L);
						} catch (InterruptedException e) {}
					}
				}
				executeTasks(0);
				if ( !used) {
					synchronized (this) {
						if (tasks.isEmpty()) {
							try {
								DebugPVMServer.this.wait(1000L);
							} catch (InterruptedException e) {}
							if (tasks.isEmpty()) {
								workers -- ;
								break;
							}
						}
					}
				}
			}
		}, "debug-worker: " + (dwid ++ )).start();
	}
	
	private static final int STATE_UNCONNECTED = 1, STATE_CONNECTED = 2, STATE_DISCONNECTED = 3, STATE_DEEP_STEP = 4, STATE_SET_SN = 5, STATE_SET_MEM = 6, STATE_ADD_POS_BREAK = 7, STATE_ADD_DI_BREAK = 8,
		STATE_ADD_AI_BREAK = 9, STATE_STD_IN = 10, STATE_STD_OUT = 11, STATE_STD_LOG = 12, STATE_REM_POS_BREAK = 13, STATE_REM_DI_BREAK = 14, STATE_REM_AI_BREAK = 15, STATE_HAS_POS_BREAK = 16, STATE_HAS_DI_BREAK = 17,
		STATE_HAS_AI_BREAK = 18, STATE_GET_MEM = 19, STATE_DISCARD = 20, STATE_CHECK_MEM = 21, STATE_FREE_MEM = 22, STATE_MALLOC = 23, STATE_REALLOC = 24;
	
	private static class Connect {
		
		/** current state */
		int        state = STATE_UNCONNECTED;
		/** buffer */
		ByteBuffer buf   = ByteBuffer.allocate(256);
		/** remain */
		long       r;
		/** address */
		long       a;
		
	}
	
	private static abstract class PVMTask implements Runnable {
		
		final SocketChannel sok;
		final Connect       con;
		
		public PVMTask(SocketChannel sok, Connect con) {
			this.sok = sok;
			this.con = con;
		}
		
		@Override
		public void run() {
			try {
				sok.read(con.buf);
				execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		protected abstract void execute() throws IOException;
		
	}
	
	private static abstract class SimpleTask extends PVMTask {
		
		protected int newstate = STATE_CONNECTED;
		
		public SimpleTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		public void run() {
			try {
				sok.read(con.buf);
				if (con.buf.remaining() == 0) {
					execute();
					con.buf.rewind();
					con.state = newstate;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		protected abstract void execute() throws IOException;
		
	}
	
	private static abstract class ResponseTask extends PVMTask {
		
		protected int newstate = STATE_CONNECTED;
		
		public ResponseTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		public void run() {
			try {
				sok.read(con.buf);
				if (con.buf.remaining() == 0) {
					execute();
					con.buf.rewind();
					sok.write(con.buf);
					con.state = newstate;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		protected abstract void execute() throws IOException;
		
	}
	
	private class GetBreaksTask extends ResponseTask {
		
		private final BreakHandle bh;
		
		public GetBreaksTask(SocketChannel sok, Connect con, BreakHandle bh) {
			super(sok, con);
			this.bh = bh;
		}
		
		@Override
		protected void execute() throws IOException {
			byte[] bytes = con.buf.array();
			int off;
			for (BreakIter iter = bh.iter(); iter.hasNext();) {
				con.buf.position(0);
				for (off = 0; off < 256; off += 8) {
					longToByteArr(bytes, off, iter.nextBreak());
				}
				con.buf.limit(off);
				sok.write(con.buf);
			}
			con.buf.limit(8);
			longToByteArr(bytes, 0, -1L);
		}
		
	}
	
	private class GetBreakCountTask extends ResponseTask {
		
		private final BreakHandle bh;
		
		public GetBreakCountTask(SocketChannel sok, Connect con, BreakHandle bh) {
			super(sok, con);
			this.bh = bh;
		}
		
		@Override
		protected void execute() throws IOException {
			sok.write(con.buf);
			longToByteArr(con.buf.array(), 0, (long) bh.size());
		}
		
	}
	
	private class StepDeepTask extends ResponseTask {
		
		public StepDeepTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			byte[] bytes = con.buf.array();
			long num = byteArrToLong(bytes, 0);
			pvm.step(num);
			longToByteArr(bytes, 0, STEP_DEEP_MAGIC);
		}
		
	}
	
	private class HasBreakTask extends ResponseTask {
		
		public HasBreakTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long num = byteArrToLong(con.buf.array(), 0);
			BreakHandle bh;
			switch (con.state) {
			case STATE_HAS_AI_BREAK:
				bh = pvm.allIntBreakHandle();
				break;
			case STATE_HAS_DI_BREAK:
				bh = pvm.defIntBreakHandle();
				break;
			case STATE_HAS_POS_BREAK:
				bh = pvm.posBreakHandle();
				break;
			default:
				throw new InternalError("illegal state: " + con.state);
			}
			if ( !bh.contains(num)) {
				longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC); // nothing done is also for not there
			}
		}
		
	}
	
	private class RemBreakTask extends ResponseTask {
		
		public RemBreakTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long num = byteArrToLong(con.buf.array(), 0);
			BreakHandle bh;
			switch (con.state) {
			case STATE_REM_AI_BREAK:
				bh = pvm.allIntBreakHandle();
				break;
			case STATE_REM_DI_BREAK:
				bh = pvm.defIntBreakHandle();
				break;
			case STATE_REM_POS_BREAK:
				bh = pvm.posBreakHandle();
				break;
			default:
				throw new InternalError("illegal state: " + con.state);
			}
			if ( !bh.remove(num)) {
				longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC);
			}
		}
		
	}
	
	private class AddBreakTask extends ResponseTask {
		
		public AddBreakTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long num = byteArrToLong(con.buf.array(), 0);
			BreakHandle bh;
			switch (con.state) {
			case STATE_ADD_AI_BREAK:
				bh = pvm.allIntBreakHandle();
				break;
			case STATE_ADD_DI_BREAK:
				bh = pvm.defIntBreakHandle();
				break;
			case STATE_ADD_POS_BREAK:
				bh = pvm.posBreakHandle();
				break;
			default:
				throw new InternalError("illegal state: " + con.state);
			}
			if ( !bh.add(num)) {
				longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC);
			}
		}
		
	}
	
	/**
	 * properly discard the sent data, before sending the {@link PVMDebugConstants#NOT_WAITING_MAGIC}
	 * <p>
	 * so the client always send the full data.<br>
	 * this is important, because the not discarded data would be interpreted as commands otherwise
	 */
	private class DiscardTask extends SimpleTask {
		
		public DiscardTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			int len = con.buf.position();
			int cpy = (int) Math.min(len, con.r);
			pvm.setMem(con.a, con.buf.array(), cpy);
			con.r -= cpy;
			con.a += cpy;
			if (con.r > 0L) {
				newstate = STATE_DISCARD;
			} else {
				longToByteArr(con.buf.array(), 0, NOT_WAITING_MAGIC);
				con.buf.position(0);
				con.buf.limit(8);
				sok.write(con.buf);
			}
		}
		
	}
	
	private class SetMemTask extends SimpleTask {
		
		public SetMemTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			boolean notWait;
			synchronized (pvm) {
				if (pvm.state() != DebugState.waiting) {
					notWait = true;
				} else {
					notWait = false;
					int len = con.buf.position();
					int cpy = (int) Math.min(len, con.r);
					pvm.setMem(con.a, con.buf.array(), cpy);
					con.r -= cpy;
					con.a += cpy;
				}
			}
			if (con.r > 0L) {
				if (notWait) {
					newstate = STATE_DISCARD;
				} else {
					newstate = STATE_SET_MEM;
				}
			} else {
				longToByteArr(con.buf.array(), 0, notWait ? NOT_WAITING_MAGIC : SET_MEM_MAGIC);
				con.buf.position(0);
				con.buf.limit(8);
				sok.write(con.buf);
			}
		}
		
	}
	
	private class CheckMemTask extends SimpleTask {
		
		public CheckMemTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			byte[] bytes = con.buf.array();
			long addr = byteArrToLong(bytes, 8),
				len = byteArrToLong(bytes, 16);
			if (len <= 0) {
				new DisconnectTask(sok, con, false).execute();
				return;
			}
			boolean success;
			synchronized (pvm) {
				try {
					pvm.memcheck(addr, len);
					success = true;
				} catch (IllegalArgumentException e) {
					success = false;
				}
			}
			if ( !success) {
				longToByteArr(bytes, 0, MEM_CHECK_MAGIC);
			}
			con.buf.position(0);
			con.buf.limit(8);
			sok.write(con.buf);
		}
		
	}
	
	private class GetMemTask extends SimpleTask {
		
		public GetMemTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			byte[] bytes = con.buf.array();
			long addr = byteArrToLong(bytes, 8),
				len = byteArrToLong(bytes, 16);
			int off;
			synchronized (pvm) {
				try {
					pvm.memcheck(addr, len);
				} catch (IllegalArgumentException e) {
					longToByteArr(bytes, 0, INVALID_VALUE_MAGIC);
					con.buf.position(0);
					con.buf.limit(8);
					sok.write(con.buf);
					return;
				}
				con.buf.limit(256);
				for (off = 8; len > 0;) {
					if (off == 256) {
						con.buf.position(0);
						off = 0;
						sok.write(con.buf);
					}
					int cpy = (int) Math.min(len, 256 - off);
					pvm.getMem(addr, bytes, off, cpy);
					off += cpy;
					addr += cpy;
					len -= cpy;
				}
			}
			con.buf.position(0);
			con.buf.limit(off);
			sok.write(con.buf);
			con.buf.limit(8);
		}
		
	}
	
	private class SetSNTask extends ResponseTask {
		
		public SetSNTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			boolean notWait;
			synchronized (pvm) {
				if (pvm.state() != DebugState.waiting) {
					notWait = true;
				} else {
					notWait = false;
					pvm.putPVM(con.buf.array());
				}
			}
			if (notWait) {
				longToByteArr(con.buf.array(), 0, NOT_WAITING_MAGIC);
			} else {
				longToByteArr(con.buf.array(), 0, SET_SN_MAGIC);
			}
			con.buf.limit(8);
		}
		
	}
	
	private class ReallocTask extends SimpleTask {
		
		public ReallocTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long oa = byteArrToLong(con.buf.array(), 8),
				nl = byteArrToLong(con.buf.array(), 16),
				na;
			if (nl > 0L) {
				try {
					synchronized (pvm) {
						na = pvm.reallocMemory(oa, nl);
					}
				} catch (OutOfMemoryError e) {
					na = -1L;
					longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC);
				} catch (IllegalArgumentException e) {
					na = -1L;
					longToByteArr(con.buf.array(), 0, INVALID_VALUE_MAGIC);
				}
			} else {
				na = -1L;
				longToByteArr(con.buf.array(), 0, INVALID_VALUE_MAGIC);
			}
			longToByteArr(con.buf.array(), 8, na);
			con.buf.limit(16);
			sok.write(con.buf);
			con.buf.limit(8);
		}
		
	}
	
	private class MallocTask extends SimpleTask {
		
		public MallocTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long l = byteArrToLong(con.buf.array(), 8),
				a;
			if (l > 0L) {
				try {
					synchronized (pvm) {
						a = pvm.mallocMemory(l);
					}
				} catch (OutOfMemoryError e) {
					a = -1L;
					longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC);
				} catch (IllegalArgumentException e) {
					a = -1L;
					longToByteArr(con.buf.array(), 0, INVALID_VALUE_MAGIC);
				}
			} else {
				a = -1L;
				longToByteArr(con.buf.array(), 0, INVALID_VALUE_MAGIC);
			}
			longToByteArr(con.buf.array(), 8, a);
			con.buf.limit(16);
			sok.write(con.buf);
			con.buf.limit(8);
		}
		
	}
	
	private class FreeMemTask extends ResponseTask {
		
		public FreeMemTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			long a = byteArrToLong(con.buf.array(), 8);
			try {
				synchronized (pvm) {
					pvm.freeMemory(a);
				}
			} catch (OutOfMemoryError | IllegalArgumentException e) {
				longToByteArr(con.buf.array(), 0, NOTHING_DONE_MAGIC);
			}
			con.buf.limit(8);
		}
		
	}
	
	private class GetSNTask extends SimpleTask {
		
		public GetSNTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			sok.write(con.buf);
			synchronized (pvm) {
				pvm.getPVM(con.buf.array());
			}
			con.buf.limit(256);
			sok.write(con.buf);
			con.buf.limit(8);
		}
		
	}
	
	private class ConnectTask extends SimpleTask {
		
		public ConnectTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		public void execute() throws IOException {
			long num = byteArrToLong(con.buf.array(), 0);
			if (num == DEBUG_CONNECT_MAGIC) {
				newstate = STATE_CONNECTED;
			} else if (num == STD_IN_CONNECT_MAGIC) {
				newstate = STATE_STD_IN;// nothing more to do
			} else if (num == STD_OUT_CONNECT_MAGIC) {
				newstate = STATE_STD_OUT;
				stdout.add(asStream(sok));
			} else if (num == STD_ERR_CONNECT_MAGIC) {
				newstate = STATE_STD_LOG;
				stdlog.add(asStream(sok));
			} else {
				newstate = STATE_DISCONNECTED;
				new DisconnectTask(sok, con, false).execute();
				return;
			}
			con.buf.position(0);
			sok.write(con.buf);
		}
		
		private OutputStream asStream(SocketChannel sok) {
			return new OutputStream() {
				
				@Override
				public void write(int b) throws IOException {
					write(new byte[] {(byte) b });
				}
				
				@Override
				public void write(byte[] b) throws IOException {
					sok.write(ByteBuffer.wrap(b));
				}
				
				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					sok.write(ByteBuffer.wrap(b, off, len));
				}
				
			};
		}
		
	}
	
	private class StdinTask extends PVMTask {
		
		public StdinTask(Connect con) {
			super(null, con);
		}
		
		@Override
		public void execute() throws IOException {
			int pos = con.buf.position();
			stdin.write(con.buf.array(), 0, pos);
			con.buf.rewind();
		}
		
	}
	
	private class ConnectedFindTaskTask extends PVMTask {
		
		private PVMTask after = null;
		
		public ConnectedFindTaskTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		public void run() {
			try {
				sok.read(con.buf);
				if (con.buf.remaining() == 0) {
					execute();
					con.buf.rewind();
					con.state = STATE_CONNECTED;
					if (after != null) {
						after.run();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		@Override
		protected void execute() throws IOException {
			long num = byteArrToLong(con.buf.array(), 0);
			if (num == GET_SN_MAGIC) {
				after = (new GetSNTask(sok, con));
			} else if (num == WAIT_MAGIC) {
				after = (new WaitTask(sok, con));
			} else if (num == RUN_MAGIC) {
				after = (new RunTask(sok, con));
			} else if (num == STEP_MAGIC) {
				after = (new SimpleStebTask(sok, con));
			} else if (num == STEP_DEEP_MAGIC) {
				con.state = STATE_DEEP_STEP;
			} else if (num == SET_SN_MAGIC) {
				con.state = STATE_SET_SN;
				con.buf.limit(256);
			} else if (num == GET_MEM_MAGIC) {
				con.state = STATE_GET_MEM;
				con.buf.limit(24);
			} else if (num == SET_MEM_MAGIC) {
				con.state = STATE_SET_MEM;
				con.buf.limit(24);
			} else if (num == GET_ALL_INT_BREAK_COUNT_MAGIC) {
				after = (new GetBreakCountTask(sok, con, pvm.allIntBreakHandle()));
			} else if (num == GET_DEF_INT_BREAK_COUNT_MAGIC) {
				after = (new GetBreakCountTask(sok, con, pvm.defIntBreakHandle()));
			} else if (num == GET_POS_BREAK_COUNT_MAGIC) {
				after = (new GetBreakCountTask(sok, con, pvm.posBreakHandle()));
			} else if (num == GET_ALL_INT_BREAKS_MAGIC) {
				after = (new GetBreaksTask(sok, con, pvm.allIntBreakHandle()));
			} else if (num == GET_DEF_INT_BREAKS_MAGIC) {
				after = (new GetBreaksTask(sok, con, pvm.defIntBreakHandle()));
			} else if (num == GET_POS_BREAKS_MAGIC) {
				after = (new GetBreaksTask(sok, con, pvm.posBreakHandle()));
			} else if (num == FREE_MEMORY_MAGIC) {
				con.state = STATE_FREE_MEM;
				con.buf.limit(16);
			} else if (num == MALLOC_MEMORY_MAGIC) {
				con.state = STATE_MALLOC;
				con.buf.limit(16);
			} else if (num == REALLOC_MEMORY_MAGIC) {
				con.state = STATE_REALLOC;
				con.buf.limit(24);
			} else if (num == REM_ALL_INT_BREAK_MAGIC) {
				con.state = STATE_REM_AI_BREAK;
			} else if (num == REM_DEF_INT_BREAK_MAGIC) {
				con.state = STATE_REM_DI_BREAK;
			} else if (num == REM_POS_BREAK_MAGIC) {
				con.state = STATE_REM_POS_BREAK;
			} else if (num == HAS_ALL_INT_BREAK_MAGIC) {
				con.state = STATE_HAS_AI_BREAK;
			} else if (num == HAS_DEF_INT_BREAK_MAGIC) {
				con.state = STATE_HAS_DI_BREAK;
			} else if (num == HAS_POS_BREAK_MAGIC) {
				con.state = STATE_HAS_POS_BREAK;
			} else if (num == ADD_ALL_INT_BREAK_MAGIC) {
				con.state = STATE_ADD_AI_BREAK;
			} else if (num == ADD_DEF_INT_BREAK_MAGIC) {
				con.state = STATE_ADD_DI_BREAK;
			} else if (num == ADD_POS_BREAK_MAGIC) {
				con.state = STATE_ADD_POS_BREAK;
			} else if (num == MEM_CHECK_MAGIC) {
				con.state = STATE_CHECK_MEM;
				con.buf.limit(24);
			} else if (num == DISCONNECT_MAGIC) {
				after = (new DisconnectTask(sok, con, true));
			} else {
				after = (new DisconnectTask(sok, con, false));
			}
		}
		
	}
	
	private class DisconnectTask extends SimpleTask {
		
		private final boolean goodDisconnect;
		
		public DisconnectTask(SocketChannel sok, Connect con, boolean goodDisconnect) {
			super(sok, con);
			this.goodDisconnect = goodDisconnect;
		}
		
		@Override
		protected void execute() throws IOException {
			if ( !goodDisconnect) {
				longToByteArr(con.buf.array(), 0, ILLEGAL_MAGIC);
			} else {
				longToByteArr(con.buf.array(), 0, DISCONNECT_MAGIC);
			}
			con.buf.rewind();
			con.buf.limit(8);
			IOException err = null;
			try {
				sok.write(con.buf);
			} catch (IOException e) {
				err = e;
			}
			sok.keyFor(select).cancel();
			try {
				sok.close();
			} catch (IOException e) {
				if (err != null) {
					e.addSuppressed(err);
				}
				throw e;
			}
			newstate = STATE_DISCONNECTED;
		}
		
	}
	
	private class SimpleStebTask extends ResponseTask {
		
		public SimpleStebTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			pvm.step();
		}
		
	}
	
	private class RunTask extends ResponseTask {
		
		public RunTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			pvm.run();
		}
		
	}
	
	private class WaitTask extends ResponseTask {
		
		public WaitTask(SocketChannel sok, Connect con) {
			super(sok, con);
		}
		
		@Override
		protected void execute() throws IOException {
			pvm.stop();
		}
		
	}
	
}
