package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.CmdNums;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PrimitiveCodeThread extends PrimitiveCodeNTDE implements IThread {

	private static final long INT_ERRORS_ILLEGAL_MEMORY = 2L;
	private static final int MAX_ARRAY_SIYE = 1 << 30;

	public static final int STATE_FREE_FLAG = 0x80;
	public static final int STATE_CHANGE_REQUESTED = 0x01;
	public static final int STATE_WAITING = 0x02;
	public static final int STATE_TERMINATING = STATE_WAITING;
	public static final int STATE_STEPPING = 0x04;
	public static final int STATE_RUNNING = 0x08;
	public static final int STATE_DISCONNECTED = 0x10;

	public final PVMDebugingComunicator com;
	public final PVMDebugingComunicator com2;
	private final Map<IBreakpoint, Long> breaksJTP;
	private final Map<Long, IBreakpoint> breaksPTJ;
	private final List<PrimitiveCodeStackStackFrame> stackFrames;
	private int state;
	private long stackMemory;
	private IBreakpoint hit;
	private long stacksize;

	public PrimitiveCodeThread(PrimitiveCodeDebugTarget debug, PVMDebugingComunicator com, PVMDebugingComunicator com2) {
		super(debug);
		this.com = com;
		this.com2 = com2;
		this.breaksJTP = new HashMap<>();
		this.breaksPTJ = new HashMap<>();
		this.stackFrames = new ArrayList<>();
		this.stackFrames.add(new PrimitiveCodeTopStackFrame(debug, 0L, 0));
		this.state = STATE_FREE_FLAG;
		this.hit = null;
		debug.process.atExit(p -> {
			synchronized (PrimitiveCodeThread.this) {
				this.state = STATE_DISCONNECTED;
			}
			this.stackFrames.clear();
		});
	}

	public void setDisconnect() {
		this.state = STATE_DISCONNECTED;
	}

	@Override
	public boolean canResume() {
		return (this.state & STATE_WAITING) != 0;
	}

	@Override
	public boolean canSuspend() {
		return (this.state & (STATE_STEPPING | STATE_RUNNING)) != 0;
	}

	@Override
	public boolean isSuspended() {
		return (this.state & STATE_WAITING) != 0;
	}

	@Override
	public void resume() {
		executeCommand(this::blockingResume, PrimitiveCodeCommandTypes.Resume);
	}

	public void blockingResume() {
		try {
			byte[] bytes = new byte[24];
			boolean first = true;
			while (this.state != STATE_CHANGE_REQUESTED) {
				PVMSnapshot sn = com.getSnapshot();
				if (sn.intp != -1L && sn.intcnt > INT_ERRORS_ILLEGAL_MEMORY) {
					com.getMem(sn.intp + INT_ERRORS_ILLEGAL_MEMORY * 8, bytes, 0, 8);
					long pntr = Convert.convertByteArrToLong(bytes, 0);
					if (sn.ip == pntr) {
						sn = findNewStackPointer(bytes, sn);
					}
				}
				if (stackMemory == -1) {
					stackMemory = sn.sp;
				}
				if (!first) {
					IBreakpoint breakpoint = this.breaksPTJ.get(sn.ip);
					if (breakpoint != null) {
						this.hit = breakpoint;
						return;
					}
				}
				first = false;
				if (stackMemory != -1) {
					com.executeUntilErrorOrExitCall();
				} else {
					com.executeNext();
				}
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public void suspend() {
		executeCommand(this::blockingSuspend, PrimitiveCodeCommandTypes.Suspend);
	}

	public void blockingSuspend() {
	}

	@Override
	public boolean canStepInto() {
		return !this.debug.process.isTerminated();
	}

	@Override
	public boolean canStepOver() {
		return !this.debug.process.isTerminated();
	}

	@Override
	public boolean canStepReturn() {
		return getStackSize() > 0L && !this.debug.process.isTerminated();
	}

	@Override
	public boolean isStepping() {
		return (this.state & STATE_STEPPING) != 0;
	}

	@Override
	public void stepInto() {
		executeCommand(this::blockingStepInto, PrimitiveCodeCommandTypes.Step_Into);
	}

	public void blockingStepInto() {
		try {
			com.executeNext();
			if (this.stacksize != -1L) {
				findNewStackPointerStep(com.getSnapshot());
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public void stepOver() {
		executeCommand(this::blockingStepOver, PrimitiveCodeCommandTypes.Step_Over);
	}

	public void blockingStepOver() {
		try {
			PVMSnapshot sn = com.getSnapshot();
			byte[] bytes = new byte[8];
			com.getMem(sn.ip, bytes, 0, 8);
			int cmdlen = -1;
			switch (0xFF & bytes[0]) {
			case CmdNums.CALL:
				cmdlen = 16;
			case CmdNums.INT: {
				sn = com.getSnapshot();
				long stackPointer = sn.sp;
				long instPointer = sn.ip;
				if (cmdlen == -1) {
					try {
						ParamBuilder b = new ParamBuilder();
						b.art = bytes[1];
						cmdlen = 8 + b.build().length();
					} catch (NoCommandException e) {
						throw new AssertionError(e);
					}
				}
				instPointer += cmdlen;
				do {
					com.executeNext();
					sn = com.getSnapshot();
					IBreakpoint breakpoint = breaksPTJ.get(sn.ip);
					if (breakpoint != null) {
						this.hit = breakpoint;
						return;
					}
				} while (state != STATE_CHANGE_REQUESTED && (sn.sp >= stackPointer || sn.ip != instPointer));
				break;
			}
			default:
				com.executeNext();
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public void stepReturn() {
		executeCommand(this::blockingStepReturn, PrimitiveCodeCommandTypes.Step_Return);
	}

	public void blockingStepReturn() {
		if (!canStepReturn()) {
			return;
		}
		try {
			byte[] bytes = new byte[24];
			boolean first = true;
			PVMSnapshot sn = com.getSnapshot();
			long stackPointer = sn.sp;
			while (this.state != STATE_CHANGE_REQUESTED && sn.sp >= stackPointer) {
				if (sn.intp != -1L && sn.intcnt > INT_ERRORS_ILLEGAL_MEMORY) {
					com.getMem(sn.intp + INT_ERRORS_ILLEGAL_MEMORY * 8, bytes, 0, 8);
					long pntr = Convert.convertByteArrToLong(bytes, 0);
					if (sn.ip == pntr) {
						sn = findNewStackPointer(bytes, sn, s -> s.sp >= stackPointer);
					}
				}
				if (stackMemory == -1) {
					stackMemory = sn.sp;
				}
				if (!first) {
					IBreakpoint breakpoint = this.breaksPTJ.get(sn.ip);
					if (breakpoint != null) {
						this.hit = breakpoint;
						return;
					}
				}
				first = false;
				com.executeNext();
				sn = com.getSnapshot();
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public boolean canTerminate() {
		return (this.state & STATE_DISCONNECTED) == 0;
	}

	@Override
	public boolean isTerminated() {
		return (this.state & STATE_DISCONNECTED) != 0;
	}

	@Override
	public void terminate() {
		executeCommand(this::blockingTerminate, PrimitiveCodeCommandTypes.Terminate);
	}

	public void blockingTerminate() {
		try {
			this.com.exit();
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public IStackFrame[] getStackFrames() {
		return this.stackFrames.toArray(new IStackFrame[this.stackFrames.size()]);
	}

	@Override
	public boolean hasStackFrames() {
		return !this.debug.process.isTerminated();
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public IStackFrame getTopStackFrame() {
		return this.stackFrames.get(0);
	}

	@Override
	public String getName() {
		return "PVM Thread";
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		if (this.hit == null) {
			return new IBreakpoint[0];
		} else {
			return new IBreakpoint[]{this.hit};
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this, this.com);
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return this.debug;
	}

	public void addBreakpoint(IBreakpoint breakpoint, long address) {
		Long val = this.breaksJTP.remove(breakpoint);
		this.breaksPTJ.remove(val);
		long addr = val;
		try {
			this.com.addBreakpoints(new long[]{addr});
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public void removeBreakpoint(IBreakpoint breakpoint) {
		Long val = this.breaksJTP.remove(breakpoint);
		this.breaksPTJ.remove(val);
		long addr = val;
		try {
			this.com.removeBreakpoints(new long[]{addr});
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	private PVMSnapshot findNewStackPointer(byte[] bytes, PVMSnapshot sn) throws IOException {
		return findNewStackPointer(bytes, sn, always -> this.state != STATE_CHANGE_REQUESTED);
	}

	private PVMSnapshot findNewStackPointer(byte[] bytes, PVMSnapshot sn, ContinueFunction c) throws IOException {
		this.stacksize = sn.sp - this.stackMemory;
		do {
			com.executeNext();
			sn = com.getSnapshot();
			if (findNewStackPointerStep(bytes, sn)) {
				return sn;
			}
		} while (c.cont(sn));
		return sn;
	}

	private static interface ContinueFunction {
		boolean cont(PVMSnapshot sn);
	}

	private boolean findNewStackPointerStep(PVMSnapshot sn) throws IOException {
		return findNewStackPointerStep(new byte[8], sn);
	}

	private boolean findNewStackPointerStep(byte[] bytes, PVMSnapshot sn) throws IOException {
		com.getMem(sn.ip, bytes, 0, 8);
		switch (0xFF & bytes[0]) {
		default:
			if (this.stackMemory + this.stacksize < sn.sp || this.stackMemory > sn.sp) {
				stackMemory = sn.sp - this.stacksize;
			}
			return false;
		case CmdNums.CALL:
		case CmdNums.PUSH:
		case CmdNums.IRET:
			this.stacksize = -1L;
			return true;
		}
	}

	public void commandInit(Object caller, PrimitiveCodeCommandTypes type) {
		while (true) {
			synchronized (this) {
				if (this.state == STATE_CHANGE_REQUESTED) {
					try {
						this.wait(100L);
					} catch (InterruptedException e) {
					}
					continue;
				} else if (0 != (this.state & STATE_FREE_FLAG)) {
					this.hit = null;
					type.init.accept(this.com2);
					if (type.dns != -1) {
						fireEvent(new DebugEvent(caller, type.dns, type.ddns));
					}
					this.state = type.stateNum;
					return;
				} else if (this.state == STATE_DISCONNECTED) {
					throw new IllegalStateException("can not execute commands, when disconnected or terminated!");
				} else {
					this.state = STATE_CHANGE_REQUESTED;
				}
			}
		}
	}

	public void commandCleanup(Object caller, PrimitiveCodeCommandTypes type, boolean successful) {
		try {
			List<DebugEvent> fire = new ArrayList<>();
			if (this.hit != null) {
				fire.add(new DebugEvent(caller, DebugEvent.SUSPEND, DebugEvent.BREAKPOINT));
			}
			if (type.dne != -1 && successful) {
				fire.add(new DebugEvent(type, type.dne, type.ddne));
			}
			long stackSize = getStackSize();
			int index = 0;
			if (stackSize > 0L) {
				int stacklen = (int) Math.min(stackSize, (long) MAX_ARRAY_SIYE);
				byte[] mem = new byte[stacklen];
				int len = 0;
				long bytesoff = stackSize - stacklen;
				this.com.getMem(this.stackMemory + bytesoff, mem, 0, stacklen);
				for (long off = stackSize; off >= 0; off -= 8, len += 8) {
					long addr = Convert.convertByteArrToLong(mem, (int) (off - bytesoff));
					IFile f = this.debug.getFile(addr);
					if (f == null) {
						PrimitiveCodeStackStackFrame sf;
						if (index >= this.stackFrames.size()) {
							sf = new PrimitiveCodeStackStackFrame(this.debug, off, len);
							this.stackFrames.add(sf);
							fire.add(new DebugEvent(sf, DebugEvent.CREATE));
						} else {
							sf = this.stackFrames.get(index);
							sf.setStackPos(off, len);
							fire.add(new DebugEvent(sf, DebugEvent.CHANGE, DebugEvent.CONTENT));
						}
						sf.update(fire);
						index++;
						len = 0;
					}
					if (off <= bytesoff + stacklen) {
						bytesoff = off - stacklen;
						this.com.getMem(this.stackMemory + off - stacklen, mem, 0, stacklen);
					}
				}
			}
			List<PrimitiveCodeStackStackFrame> remove = this.stackFrames.subList(++index, this.stackFrames.size());
			remove.clear();
			if (!fire.isEmpty() || !successful) {
				fire.add(0, new DebugEvent(this.debug, DebugEvent.CHANGE, DebugEvent.CONTENT));
				fire.add(1, new DebugEvent(this, DebugEvent.CHANGE, DebugEvent.CONTENT));
				fireEvent(fire.toArray(new DebugEvent[fire.size()]));
			}
		} catch (IOException e) {
			throw new IOError(e);
		} finally {
			try {
				if (type.stateNum != STATE_DISCONNECTED) {
					this.state |= STATE_FREE_FLAG;
				}
				synchronized (this) {
					notify();
				}
			} catch (Throwable t) {// example: NullPointer(/ThreadDeath)
				this.state |= STATE_FREE_FLAG;
				throw t;
			}
		}
	}

	public long getStackSize() {
		if (this.stacksize != -1L) {
			return this.stacksize;
		}
		try {
			PVMSnapshot sn = this.com.getSnapshot();
			return sn.sp - this.stackMemory;
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public boolean stateChangeRequested() {
		return this.state == STATE_CHANGE_REQUESTED;
	}

}
