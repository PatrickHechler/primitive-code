package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.primitive.runtime.enums.DebugState;
import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.BreakHandle;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.DebugPVM;
import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional.PVMCommand;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;

public abstract class AbstractDebugPVM extends AbstractPVM implements DebugPVM {
	
	private final BreakHandle pb = new PosBreakHandleImpl();
	
	private final BreakHandle di = new PosBreakHandleImpl() {
		
		@Override
		public boolean add(long newStop) {
			if (newStop < 0L || newStop >= PrimAsmConstants.INTERRUPT_COUNT) {
				throw new IllegalArgumentException("out of bounds: intcnt=" + PrimAsmConstants.INTERRUPT_COUNT + " intnum=" + newStop);
			}
			return super.add(newStop);
		};
		
		protected int index(long breakPoint) {
			return (int) ( (ls.length - 1) & breakPoint);
		}
		
	};
	
	private final BreakHandle ai = new PosBreakHandleImpl() {
		
		@Override
		public boolean add(long newStop) {
			if (newStop < 0L) {
				throw new IllegalArgumentException("there are no negative interrupts! intnum=" + newStop);
			}
			return super.add(newStop);
		};
		
		protected int index(long breakPoint) {
			return (int) ( (ls.length - 1) & breakPoint);
		}
		
	};
	
	private static final int[] DEEP_CNT_DEC = new int[] {PrimAsmCommands.RET, PrimAsmCommands.IRET };
	private static final int[] DEEP_CNT_INC = new int[] {PrimAsmCommands.CALL, PrimAsmCommands.CALO };
	// INT is done in anyInt() and defInt() because defInts do not IRET
	
	private final PVMCommand[] deepCntCmds = new PVMCommand[DEEP_CNT_DEC.length + DEEP_CNT_INC.length];
	
	private static final int S_WAIT      = 1;
	private static final int S_RUN       = 2;
	private static final int S_STEP_IN   = 3;
	private static final int S_STEP_OVER = 4;
	private static final int S_STEP_OUT  = 5;
	private static final int S_STEP_DEEP = 6;
	
	/**
	 * the current PVM state
	 */
	private volatile int  state  = S_WAIT;
	/**
	 * the new state
	 */
	private volatile int  nstate = S_WAIT;
	private volatile long ndeep;
	
	private final PipedOutputStream stdinp = new PipedOutputStream();
	
	public AbstractDebugPVM(PatrFileSysImpl fs) {
		this(fs, new Random(), new DebugOutStr(), new DebugOutStr());
	}
	
	public AbstractDebugPVM(PatrFileSysImpl fs, Random rnd) {
		this(fs, rnd, new DebugOutStr(), new DebugOutStr());
	}
	
	public AbstractDebugPVM(PatrFileSysImpl fs, Random rnd, DebugOutStr stdout, DebugOutStr stdlog) {
		super(fs, rnd, stdout, stdlog, new PipedInputStream());
		try {
			stdinp.connect((PipedInputStream) stdin);
		} catch (IOException e) {
			throw new IOError(e);
		}
		for (int i = 0; i < DEEP_CNT_DEC.length; i ++ ) {
			deepCntCmds[i] = commands[DEEP_CNT_DEC[i]];
		}
		for (int i = 0; i < DEEP_CNT_INC.length; i ++ ) {
			deepCntCmds[i + DEEP_CNT_DEC.length] = commands[DEEP_CNT_INC[i]];
		}
		Thread worker = new Thread(this::exe, "debug-pvm");
		worker.setPriority(Thread.MAX_PRIORITY - 1);
		worker.setDaemon(false);
		worker.run();
	}
	
	/**
	 * ignore breakpoints for the first command (force execution)
	 */
	private boolean fe;
	
	private void exe() {
		while (true) {
			long old;
			synchronized (this) {
				old = state;
				state = nstate;
				if (old != state) {
					this.notifyAll();
				}
			}
			try {
				switch (state) {
				case S_WAIT:
					exeWait();
					break;
				case S_RUN:
					fe = old != S_RUN;
					synchronized (this) {
						execute();
					}
					break;
				case S_STEP_IN:
					fe = true;
					synchronized (this) {
						if (state == nstate) {
							nstate = S_WAIT;
						}
						execute();
					}
					break;
				case S_STEP_OVER:
					deep = 0L;
					exeStepDeep();
					break;
				case S_STEP_OUT:
					deep = 1L;
					exeStepDeep();
					break;
				case S_STEP_DEEP:
					deep = ndeep;
					exeStepDeep();
					break;
				default:
					throw new InternalError("unknown state: " + state);
				}
			} catch (BreakpointException b) {
				synchronized (this) {
					state = S_WAIT;
				}
			}
		}
	}
	
	private long deep;
	
	private void exeStepDeep() {
		exeStepInit();
		synchronized (this) {
			fe = true;
			execute();
			fe = false;
		}
		while (deep > 0) {
			synchronized (this) {
				if (state != nstate) {
					break;
				}
				execute();
			}
		}
		synchronized (this) {
			if (nstate == state) {
				nstate = S_WAIT;
			}
		}
		exeStepEnd();
	}
	
	private void exeStepEnd() {
		for (int i = 0; i < DEEP_CNT_DEC.length; i ++ ) {
			final int cmdNum = DEEP_CNT_DEC[i];
			commands[cmdNum] = deepCntCmds[i];
		}
		for (int i = 0; i < DEEP_CNT_INC.length; i ++ ) {
			final int cmdNum = DEEP_CNT_INC[i];
			commands[cmdNum] = deepCntCmds[DEEP_CNT_DEC.length + i];
		}
	}
	
	private void exeStepInit() {
		for (int i = 0; i < DEEP_CNT_DEC.length; i ++ ) {
			final int cmdNum = DEEP_CNT_DEC[i];
			final PVMCommand orig = commands[cmdNum];
			commands[cmdNum] = new PVMCommand() {
				
				@Override
				public void execute() throws PrimitiveErrror {
					deep -- ;
					orig.execute();
				}
				
			};
		}
		for (int i = 0; i < DEEP_CNT_INC.length; i ++ ) {
			final int cmdNum = DEEP_CNT_INC[i];
			final PVMCommand orig = commands[cmdNum];
			commands[cmdNum] = new PVMCommand() {
				
				@Override
				public void execute() throws PrimitiveErrror {
					deep ++ ;
					orig.execute();
				}
				
			};
		}
	}
	
	private void exeWait() {
		synchronized (this) {
			if (nstate == state) {
				assert state == S_WAIT;
				try {
					this.wait(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void exeCmd(int cmdNum) throws PrimitiveErrror {
		if ( !fe && pb.contains(ip)) {
			throw new BreakpointException();
		}
		super.exeCmd(cmdNum);
	}
	
	@Override
	protected void anyInt(long intNum) throws PrimitiveErrror, OutOfMemoryError {
		deep ++ ;
		if ( !fe && ai.contains(intNum)) {
			throw new BreakpointException();
		}
		super.anyInt(intNum);
	}
	
	@Override
	protected void defInt(long intNum) throws PrimitiveErrror {
		deep -- ;
		if ( !fe && di.contains(intNum)) {
			throw new BreakpointException();
		}
		super.defInt(intNum);
	}
	
	@Override
	public DebugState state() {
		synchronized (this) {
			switch (state) {
			case S_WAIT:
				return DebugState.waiting;
			case S_RUN:
				return DebugState.running;
			case S_STEP_IN:
			case S_STEP_OVER:
			case S_STEP_OUT:
			case S_STEP_DEEP:
				return DebugState.stepping;
			default:
				throw new InternalError("unknown state: " + state);
			}
		}
	}
	
	@Override
	public void run() {
		setState(S_RUN);
	}
	
	@Override
	public void step() {
		setState(S_STEP_IN);
	}
	
	@Override
	public void stepOver() {
		setState(S_STEP_OVER);
	}
	
	@Override
	public void stepOut() {
		setState(S_STEP_OUT);
	}
	
	@Override
	public void stop() {
		setState(S_WAIT);
	}
	
	@Override
	public void step(long deep) {
		synchronized (this) {
			ndeep = deep;
			setState(S_STEP_DEEP);
		}
	}
	
	private void setState(int nstat) {
		synchronized (this) {
			nstate = nstat;
			this.notifyAll();
			while (true) {
				if (state == nstat) return;
				else if (nstate != nstat) return;
				try {
					this.wait(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public BreakHandle posBreakHandle() {
		return pb;
	}
	
	@Override
	public BreakHandle defIntBreakHandle() {
		return di;
	}
	
	@Override
	public BreakHandle allIntBreakHandle() {
		return ai;
	}
	
	@Override
	public void getMem(long addr, byte[] buf, int len) throws IllegalArgumentException {
		if (len < 0 || buf.length - len < 0) {
			throw new IndexOutOfBoundsException("len=" + len + " buf.len=" + buf.length);
		}
		try {
			get(addr, buf, len);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("addr does not point to a valid memory block (addr=" + addr + " len=" + len + ")");
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public void getMem(long addr, byte[] buf, int boff, int len) throws IllegalArgumentException {
		if (len < 0 || boff < 0 || buf.length - len - boff < 0) {
			throw new IndexOutOfBoundsException("boff=" + boff + " len=" + len + " buf.len=" + buf.length);
		}
		try {
			get(addr, buf, boff, len);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("addr does not point to a valid memory block (addr=" + addr + " len=" + len + ")");
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public void setMem(long addr, byte[] buf, int len) throws IllegalArgumentException {
		try {
			set(buf, addr, len);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("addr does not point to the start of a  memory block (addr=" + addr + ")");
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public void getPVM(byte[] buf) throws IllegalArgumentException {
		if (buf.length != 256) {
			throw new IllegalArgumentException();
		}
		getregs(buf);
	}
	
	@Override
	public void putPVM(byte[] buf) throws IllegalArgumentException {
		if (buf.length != 256) {
			throw new IllegalArgumentException();
		}
		setregs(buf);
	}
	
	@Override
	public void memcheck(long addr, long len) throws IllegalArgumentException {
		try {
			checkmem(addr, len);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("invalid range: addr=" + addr + " len=" + len);
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public long mallocMemory(long len) throws OutOfMemoryError {
		return malloc(len);
	}
	
	@Override
	public long reallocMemory(long addr, long len) throws OutOfMemoryError, IllegalArgumentException {
		try {
			return realloc(addr, len);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("addr does not point to the start of a  memory block (addr=" + addr + ")");
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public void freeMemory(long addr) throws IllegalArgumentException {
		try {
			free(addr);
		} catch (PrimitiveErrror e) {
			if (e.intNum == PrimAsmConstants.INT_ERRORS_ILLEGAL_MEMORY) {
				throw new IllegalArgumentException("addr does not point to the start of a  memory block (addr=" + addr + ")");
			} else {
				throw new AssertionError(e.getLocalizedMessage(), e);
			}
		}
	}
	
	@Override
	public OutputStream stdin() {
		return stdinp;
	}
	
	@Override
	public InputStream stdout() throws IOException {
		PipedOutputStream po = new PipedOutputStream();
		PipedInputStream pi = new PipedInputStream();
		po.connect(pi);
		((DebugOutStr) stdlog).add(po);
		return pi;
	}
	
	@Override
	public InputStream stdlog() throws IOException {
		PipedOutputStream po = new PipedOutputStream();
		PipedInputStream pi = new PipedInputStream();
		po.connect(pi);
		((DebugOutStr) stdlog).add(po);
		return pi;
	}
	
	private class BreakpointException extends RuntimeException {
		
		/** UID */
		private static final long serialVersionUID = 1966057270401220353L;
		
	}
	
}
