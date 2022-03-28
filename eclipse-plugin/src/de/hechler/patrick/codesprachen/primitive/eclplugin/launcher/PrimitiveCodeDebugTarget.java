package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.RuntimeProcess;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.Command;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.CmdNums;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

@Deprecated
public class PrimitiveCodeDebugTarget implements IDebugTarget {

	public static final String PVM_MODEL_IDENTIFIER = "primitive virtaul mashine";

	private static final int STATE_NOT_STARTED = 0;
	private static final int STATE_RUNNING = 1;
	private static final int STATE_WAITING = 2;
	private static final int STATE_DISCONECTED = 4;
	private static final int STATE_STEPPING = STATE_RUNNING | 8;
	private static final int STATE_TERMINATED = 16;

	private static final long INT_ERRORS_ILLEGAL_MEMORY = 2L;

	private static final int SCS_NOTHING_REQUESTED = 0;
	private static final int SCS_ABORT_REQUESTED = 1;
	private static final int SCS_CHANGE_POSSIBLE = 2;

	private final ILaunch launch;
	private final String[] args;
	private PVMDebugingComunicator com;
	private Process pvm;
	private IProcess process;
	public final PrimCodeThread myThread;
	public final IFile sourcefile;
	public final PrimitiveCodeTopStackFrame topStackFrame;
	private List<PrimitiveCodeStackFrame> stackFrames;
	private volatile int state;
	private volatile int stateChangeState = SCS_NOTHING_REQUESTED;
	private Consumer<Integer> termiationListener = e -> {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		breakpointManager.removeBreakpointListener(this);
	};
	private IBreakpoint[] hittedBreaks;
	private Map<IBreakpoint, Long> breaksJTP;
	private Map<Long, IBreakpoint> breaksPTJ;
	private long startAddress;
	private long binaryLength;
	private long stackMemory;

	private long oldSP;
	private long stacksize;

	public PrimitiveCodeDebugTarget(ILaunch launch, String[] args, IFile sourceFile) {
		this.launch = launch;
		this.args = args;
		this.state = STATE_NOT_STARTED;
		this.myThread = new PrimCodeThread();
		this.sourcefile = sourceFile;
		this.topStackFrame = new PrimitiveCodeTopStackFrame(PrimitiveCodeDebugTarget.this);
		this.stackFrames = new ArrayList<>();
		this.stackFrames.add(this.topStackFrame);
	}

	public void onTermiation(Consumer<Integer> onExit) {
		termiationListener = exitCode -> {
			termiationListener.accept(exitCode);
			onExit.accept(exitCode);
		};
	}

	public void start(byte[] binaryData, int port, ILaunch launch) throws IOException {
		initRun(null, launch);
		initDebug(port, binaryData.length);
	}

	public void start(String dir, int port, long binlen, ILaunch launch) throws IOException {
		initRun(new File(dir), launch);
		if (port != -1) {
			initDebug(port, binlen);
		}
	}

	public PVMDebugingComunicator getCom() {
		return com;
	}

	public long getStackMemory() {
		return stackMemory;
	}

	public long getStartAddress() {
		return startAddress;
	}

	public long getBinaryLength() {
		return binaryLength;
	}

	private void initRun(File dir, ILaunch launch) throws IOException {
		this.pvm = Runtime.getRuntime().exec(this.args, null, dir);
		new Thread(() -> {
			while (this.pvm.isAlive()) {
				try {
					this.pvm.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			termiationListener.accept(this.pvm.exitValue());
		}, "Termination Waiter: " + getArgs()).start();
		this.process = new RuntimeProcess(launch, pvm,
				(sourcefile == null ? "pvm: " : sourcefile.getName() + ": ") + Arrays.toString(this.args), null);
	}

	private void initDebug(int port, long binlen) throws IOException, UnknownHostException {
		appendDebugCom(port);
		setValues(binlen);
		initBreakpoints();
		setValuesWithCom();
	}

	private void setValuesWithCom() throws IOException {
		PVMSnapshot sn = com.getSnapshot();
		this.startAddress = sn.ip;
		this.stackMemory = sn.sp;
	}

	private void setValues(long binlen) {
		this.stateChangeState = SCS_CHANGE_POSSIBLE;
		this.state = STATE_WAITING;
		this.binaryLength = binlen;
	}

	private void appendDebugCom(int port) throws IOException, UnknownHostException {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		com = new PVMDebugingComunicator(pvm, new Socket("localhost", port));
	}

	private void initBreakpoints() {
		this.breaksJTP = new HashMap<>();
		this.breaksPTJ = new HashMap<>();
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		breakpointManager.addBreakpointListener(this);
		IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	public IFile getSourcefile() {
		return sourcefile;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this, com, this.pvm, this.process);
	}

	public static <T> T findAdapter(Class<T> adapter, Object... condidates) {
		for (Object canditade : condidates) {
			if (adapter.isInstance(canditade)) {
				return adapter.cast(canditade);
			}
		}
		return null;
	}

	@Override
	public boolean canTerminate() {
		return true;
	}

	@Override
	public boolean isTerminated() {
		return !pvm.isAlive();
	}

	@Override
	public void terminate() {
		executeCommand(this::blockingTerminate, "terminate");
	}

	public void blockingTerminate() {
		changeState(STATE_TERMINATED);
		try {
			if (com != null) {
				try {
					com.exit();
				} catch (IOException | RuntimeException e1) {
				}
			}
			if (pvm.isAlive()) {
				pvm.destroy();
				try {
					pvm.waitFor(1000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
				}
				if (pvm.isAlive()) {
					pvm.destroyForcibly();
				}
			}
		} finally {
			stateChangeState = SCS_CHANGE_POSSIBLE;
		}
	}

//	@Override
//	public String getLabel() {
//		StringBuilder result = new StringBuilder();
//		if (pvm.isAlive()) {
//			result.append("alive: ");
//		} else {
//			result.append("exit code=").append(pvm.exitValue()).append(": ");
//		}
//		getArgs(result);
//		return result.toString();
//	}

	private String getArgs() {
		StringBuilder result = new StringBuilder();
		getArgs(result);
		return result.toString();
	}

	private void getArgs(StringBuilder result) {
		String zw = args[0].replaceAll("\"", "\\\\\"");
		zw = zw.replaceAll("\\\\", "\\\\\\\\");
		result.append('"').append(zw).append('"');
		for (int i = 1, len = args.length; i < len; i++) {
			zw = args[i].replaceAll("\"", "\\\\\"");
			zw = zw.replaceAll("\\\\", "\\\\\\\\");
			result.append(" \"").append(zw).append('"');
		}
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public String getModelIdentifier() {
		return PVM_MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public boolean canResume() {
		return state == STATE_WAITING;
	}

	@Override
	public boolean canSuspend() {
		return (state & STATE_RUNNING) != 0;
	}

	@Override
	public boolean isSuspended() {
		return state == STATE_WAITING;
	}

	@Override
	public void resume() {
		executeCommand(this::blockingResume, "resume");
	}

	public void blockingResume() {
		changeState(STATE_RUNNING);
		try {
			byte[] bytes = new byte[24];
			boolean first = true;
			while (stateChangeState == SCS_NOTHING_REQUESTED) {
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
						this.hittedBreaks = new IBreakpoint[] { breakpoint };
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
		} finally {
			stateChangeState = SCS_CHANGE_POSSIBLE;
		}
	}

	private PVMSnapshot findNewStackPointer(byte[] bytes, PVMSnapshot sn) throws IOException {
		return findNewStackPointer(bytes, sn, always -> stateChangeState == SCS_NOTHING_REQUESTED);
	}

	private PVMSnapshot findNewStackPointer(byte[] bytes, PVMSnapshot sn, ContinueFunction c) throws IOException {
		oldSP = sn.sp;
		stacksize = oldSP - stackMemory;
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
			if (oldSP < sn.sp || stackMemory > sn.sp) {
				stackMemory = sn.sp - stacksize;
			}
			return false;
		case CmdNums.CALL:
		case CmdNums.PUSH:
		case CmdNums.IRET:
			stacksize = -1L;
			oldSP = -1L;
			return true;
		}
	}

	@Override
	public void suspend() {
		executeCommand(this::blockingSuspend, "suspend");
	}

	public void blockingSuspend() {
		changeState(STATE_WAITING);
		stateChangeState = SCS_CHANGE_POSSIBLE;
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		System.out.println("add breakpoint: " + breakpoint.getClass().getName() + " : " + breakpoint);
		IMarker marker = breakpoint.getMarker();
		IResource res = marker.getResource();
		if (!res.equals(this.sourcefile)) {
			return;
		}
		if (breakpoint instanceof ILineBreakpoint) {
			ILineBreakpoint lb = (ILineBreakpoint) breakpoint;
			try {
				int ln = lb.getLineNumber();
				sourcefile.exists();
				ParseContext pc = ValidatorDocumentSetupParticipant.getContext(sourcefile);
				for (int i = 0, size = pc.children.size(); i < size; i++) {
					ParseTree pt = pc.children.get(i);
					if (pt instanceof AnythingContext) {
						AnythingContext ac = (AnythingContext) pt;
						if (ac.stop.getLine() >= ln) {
							if (ac.command == null && ac.CONSTANT_POOL == null
									|| ac.command != null && ac.command.LABEL_DECLARATION != null) {
								continue;
							}
							long addr = ac.pos_ - ((Command) ac.zusatz).length();
							addr += startAddress;
							com.addBreakpoints(new long[] { addr });
							Long address = (Long) addr;
							this.breaksJTP.put(lb, address);
							this.breaksPTJ.put(address, lb);
							break;
						}
					}
				}
			} catch (Exception e) {
				System.err.println("breakpoint ignored because of exception: " + breakpoint);
				e.printStackTrace();
			}
		} else {
//			System.err.println("unknown breakpoint ignored: " + breakpoint.getClass().getName());
		}
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		breakpointChanged(breakpoint, delta);
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		Long oldadr = this.breaksJTP.remove(breakpoint);
		IBreakpoint br = this.breaksPTJ.remove(oldadr);
		assert breakpoint.equals(br);
		breakpointAdded(breakpoint);
	}

	@Override
	public boolean canDisconnect() {
		return state == STATE_RUNNING;
	}

	@Override
	public void disconnect() {
		executeCommand(this::blockingDisconnect, "disconect");
	}

	public void blockingDisconnect() {
		changeState(STATE_DISCONECTED);
		try {
			com.detach();
		} finally {
			stateChangeState = SCS_CHANGE_POSSIBLE;
		}
	}

	@Override
	public boolean isDisconnected() {
		return state == STATE_DISCONECTED;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return true;
	}

	@Deprecated
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		if (length > Integer.MAX_VALUE) {
			IStatus status = new Status(IStatus.ERROR, getClass(), "too large length: (max=2^31-1) len=" + length);
			throw new DebugException(status);
		}
		int len = (int) length;
		byte[] bytes = new byte[len];
		return new IMemoryBlock() {

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public String getModelIdentifier() {
				return PVM_MODEL_IDENTIFIER;
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTarget.this.launch;
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTarget.this;
			}

			@Override
			public boolean supportsValueModification() {
				return true;
			}

			@Override
			public void setValue(long offset, byte[] newbytes) throws DebugException {
				if (offset + newbytes.length > bytes.length) {
					IStatus status = new Status(IStatus.ERROR, getClass(), "too large: my length=" + len + " off="
							+ offset + " bytes.len=" + newbytes.length + " added-len=" + (offset + newbytes.length));
					throw new DebugException(status);
				}
				try {
					com.setMem(startAddress + offset, newbytes, 0, newbytes.length);
				} catch (IOException e) {
					comunicationError(e);
				}
			}

			@Override
			public long getStartAddress() {
				return startAddress;
			}

			@Override
			public long getLength() {
				return length;
			}

			@Override
			public byte[] getBytes() throws DebugException {
				try {
					com.getMem(startAddress, bytes, 0, len);
				} catch (IOException e) {
					comunicationError(e);
				}
				return bytes.clone();
			}

		};
	}

	@Override
	public IProcess getProcess() {
		return this.process;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return pvm.isAlive() ? new IThread[] { this.myThread } : new IThread[0];
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return pvm.isAlive();
	}

	@Override
	public String getName() throws DebugException {
		return "PrimitiveVirtualMashine";
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint instanceof ILineBreakpoint) {
			return true;
		} else {
			return false;
		}
	}
	@Deprecated
	public class PrimCodeThread implements IThread {

		@Override
		public boolean canStepInto() {
			return state == STATE_WAITING;
		}

		@Override
		public boolean canStepOver() {
			return state == STATE_WAITING;
		}

		@Override
		public boolean canStepReturn() {
			return state == STATE_WAITING;
		}

		@Override
		public boolean isStepping() {
			return state == STATE_STEPPING;
		}

		@Override
		public void stepInto() {
			executeCommand(this::blockingStepInto, "step into");
		}

		public void blockingStepInto() {
			changeState(STATE_STEPPING);
			try {
				com.executeNext();
				if (stacksize != -1L) {
					findNewStackPointerStep(com.getSnapshot());
				}
			} catch (IOException e) {
				throw new IOError(e);
			} finally {
				stateChangeState = SCS_CHANGE_POSSIBLE;
			}
		}

		@Override
		public void stepOver() {
			executeCommand(this::blockingStepOver, "step over");
		}

		public void blockingStepOver() {
			changeState(STATE_STEPPING);
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
							hittedBreaks = new IBreakpoint[] { breakpoint };
							return;
						}
					} while (stateChangeState == SCS_NOTHING_REQUESTED
							&& (sn.sp >= stackPointer || sn.ip != instPointer));
					break;
				}
				default:
					com.executeNext();
				}
			} catch (IOException e) {
				throw new IOError(e);
			} finally {
				stateChangeState = SCS_CHANGE_POSSIBLE;
			}
		}

		@Override
		public void stepReturn() {
			executeCommand(this::blockingStepReturn, "step return");
		}

		public void blockingStepReturn() {
			try {
				boolean stop = false;
				PVMSnapshot sn = com.getSnapshot();
				do {
					byte[] bytes = new byte[8];
					com.getMem(sn.ip, bytes, 0, 8);
					switch (0xFF & bytes[0]) {
					case CmdNums.RET:
					case CmdNums.IRET:
						stop = true;
					}
					com.executeNext();
					sn = com.getSnapshot();
					IBreakpoint breakpoint = breaksPTJ.get(sn.ip);
					if (breakpoint != null) {
						hittedBreaks = new IBreakpoint[] { breakpoint };
						return;
					}
				} while (stateChangeState == SCS_NOTHING_REQUESTED && !stop && !breaksPTJ.containsKey(sn.ip));
//				rebuildStack(sn); //todo
				fireDebugEvent(new DebugEvent(this, DebugEvent.SUSPEND, DebugEvent.STEP_END));
			} catch (IOException e) {
				throw new IOError(e);
			} finally {
				stateChangeState = SCS_CHANGE_POSSIBLE;
			}
		}

		@Override
		public IStackFrame[] getStackFrames() throws DebugException {
			if (state != STATE_WAITING) {
				return new IStackFrame[0];
			}
			try {
				PVMSnapshot sn = com.getSnapshot();
				IStackFrame[] result = new IStackFrame[(int) Math.min(1L + (sn.sp - stackMemory) / 8,
						(1L + (long) Integer.MAX_VALUE) / 8)];
				result[0] = topStackFrame;
				if (result.length == 1) {
					return result;
				}
				byte[] bytes = new byte[(result.length - 1) * 8];
				com.getMem(sn.sp - bytes.length, bytes, 0, bytes.length);
				for (int i = 1; i < result.length; i++) {
					long addr = sn.sp - (i * 8);
					result[i] = new PrimitiveCodeStackStackFrame(PrimitiveCodeDebugTarget.this, addr);
				}
				return result;
			} catch (IOException e) {
				throw comunicationError(e);
			}
		}

		@Override
		public IBreakpoint[] getBreakpoints() {
			return hittedBreaks;
		}

		@Override
		public String getModelIdentifier() {
			return PrimitiveCodeDebugTarget.this.getModelIdentifier();
		}

		@Override
		public PrimitiveCodeDebugTarget getDebugTarget() {
			return PrimitiveCodeDebugTarget.this;
		}

		@Override
		public ILaunch getLaunch() {
			return PrimitiveCodeDebugTarget.this.getLaunch();
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return findAdapter(adapter, this, PrimitiveCodeDebugTarget.this, PrimitiveCodeDebugTarget.this.com,
					PrimitiveCodeDebugTarget.this.pvm);
		}

		@Override
		public boolean canResume() {
			return PrimitiveCodeDebugTarget.this.canResume();
		}

		@Override
		public boolean canSuspend() {
			return PrimitiveCodeDebugTarget.this.canSuspend();
		}

		@Override
		public boolean isSuspended() {
			return PrimitiveCodeDebugTarget.this.isSuspended();
		}

		@Override
		public void resume() throws DebugException {
			PrimitiveCodeDebugTarget.this.resume();
		}

		@Override
		public void suspend() throws DebugException {
			PrimitiveCodeDebugTarget.this.suspend();
		}

		@Override
		public boolean canTerminate() {
			return PrimitiveCodeDebugTarget.this.canTerminate();
		}

		@Override
		public boolean isTerminated() {
			return PrimitiveCodeDebugTarget.this.isTerminated();
		}

		@Override
		public void terminate() throws DebugException {
			PrimitiveCodeDebugTarget.this.terminate();
		}

		@Override
		public String getName() throws DebugException {
			return "Primitive Virtual Mashine Thread";
		}

		@Override
		public boolean hasStackFrames() throws DebugException {
			return true;
		}

		@Override
		public int getPriority() throws DebugException {
			return 1;
		}

		@Override
		public IStackFrame getTopStackFrame() throws DebugException {
			return topStackFrame;
		}
	}

	private void changeState(int newState) {
		while (true) {
			synchronized (this) {
				switch (stateChangeState) {
				case SCS_NOTHING_REQUESTED:
					stateChangeState = SCS_ABORT_REQUESTED;
				case SCS_ABORT_REQUESTED:
					if (state == STATE_RUNNING) {
						try {
							com.pause();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case SCS_CHANGE_POSSIBLE:
					stateChangeState = SCS_NOTHING_REQUESTED;
					Runnable init;
					switch (newState) {
					default:
					case STATE_NOT_STARTED:
						throw new InternalError("illegal new state: " + newState);
					case STATE_DISCONECTED:
						init = () -> {
							try {
								com.run();
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
						break;
					case STATE_RUNNING:
					case STATE_WAITING:
					case STATE_STEPPING:
						init = () -> {
							try {
								com.pause();
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
						break;
					case STATE_TERMINATED:
						init = () -> {
						};
					}
					if (newState != state || (newState & STATE_RUNNING) != 0) {
						init.run();
					}
					this.hittedBreaks = null;
					return;
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public static long extractValue(String expression) {
		long val;
		if (expression.startsWith("UHEX-")) {
			val = Long.parseUnsignedLong(expression.substring(5), 16);
		} else if (expression.startsWith("HEX-")) {
			val = Long.parseLong(expression.substring(4), 16);
		} else if (expression.startsWith("NHEX-")) {
			val = Long.parseLong(expression.substring(4), 16);
		} else if (expression.startsWith("DEC-")) {
			val = Long.parseLong(expression.substring(4), 10);
		} else if (expression.startsWith("NDEC-")) {
			val = Long.parseLong(expression.substring(4), 10);
		} else if (expression.startsWith("OCT-")) {
			val = Long.parseLong(expression.substring(4), 8);
		} else if (expression.startsWith("NOCT-")) {
			val = Long.parseLong(expression.substring(4), 8);
		} else if (expression.startsWith("BIN-")) {
			val = Long.parseLong(expression.substring(4), 2);
		} else if (expression.startsWith("NBIN-")) {
			val = Long.parseLong(expression.substring(4), 2);
		} else if (expression.matches("[0-9]*\\.[0-9]*")) {
			val = Double.doubleToRawLongBits(Double.parseDouble(expression));
		} else {
			val = Long.parseLong(expression, 10);
		}
		return val;
	}
	
	/**
	 * throws an {@link DebugException} with the specified cause
	 * 
	 * @param cause cause of the thrown {@link DebugException}
	 * @return never
	 * @throws DebugException always
	 */
	@Deprecated
	private Error comunicationError(Throwable cause) throws DebugException {
		IStatus s = new Status(IStatus.ERROR, getClass(), "error on cumunicating with the pvm: " + cause.getMessage(),
				cause);
		throw new DebugException(s);
	}

	@Deprecated
	public void executeCommand(Runnable cmd, String name) {
		new Thread(() -> {
			cmd.run();
		}, "PVM: " + name).start();
	}

	@Deprecated
	public static void fireDebugEvent(DebugEvent... event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(event);
		}
	}
	
}
