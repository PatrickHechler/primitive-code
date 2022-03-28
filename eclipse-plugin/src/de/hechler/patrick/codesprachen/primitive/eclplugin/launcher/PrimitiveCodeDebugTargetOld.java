package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.antlr.v4.runtime.Token;
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
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
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
public class PrimitiveCodeDebugTargetOld implements IDebugTarget {

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

	public PrimitiveCodeDebugTargetOld(ILaunch launch, String[] args, IFile sourceFile) {
		this.launch = launch;
		this.args = args;
		this.state = STATE_NOT_STARTED;
		this.myThread = new PrimCodeThread();
		this.sourcefile = sourceFile;
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
		executeCommand(this::blockingTerminate);
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

//	@Override
//	@SuppressWarnings("restriction")
//	public IStreamsProxy getStreamsProxy() {
//		return new org.eclipse.debug.internal.core.StreamsProxy(this.pvm, StandardCharsets.UTF_8);
//	}

//	@Override
//	public void setAttribute(String key, String value) {
//		try {
//			if (!key.matches("IP|SP|STATUS|INTP|INTCNT|X[0-9A-E][0-9A-F]|XF[0-9A]")) {
//				throw new IllegalArgumentException("unknown attribute: '" + key + "'");
//			}
//			synchronized (this) {
//				PVMSnapshot sn = com.getSnapshot();
//				switch (key) {
//				case "IP":
//					sn.ip = parseLong(value);
//					break;
//				case "SP":
//					sn.sp = parseLong(value);
//					break;
//				case "STATUS":
//					sn.status = parseLong(value);
//					break;
//				case "INTP":
//					sn.intp = parseLong(value);
//					break;
//				case "INTCNT":
//					sn.intcnt = parseLong(value);
//				default:
//					sn.x[Integer.parseInt(value.substring(1), 16)] = parseLong(value);
//					break;
//				}
//				com.setSnapshot(sn);
//			}
//		} catch (IOException e) {
//			throw new IOError(e);
//		}
//	}

//	@Override
//	public String getAttribute(String key) {
//		if (!key.matches("IP|SP|STATUS|INTP|INTCNT|X[0-9A-E][0-9A-F]|XF[0-9A]")) {
//			return null;
//		}
//		PVMSnapshot sn;
//		try {
//			sn = com.getSnapshot();
//		} catch (IOException e) {
//			throw new IOError(e);
//		}
//		switch (key) {
//		case "IP":
//			return "UHEX-" + Long.toUnsignedString(sn.ip, 16);
//		case "SP":
//			return "UHEX-" + Long.toUnsignedString(sn.sp, 16);
//		case "STATUS":
//			return "UHEX-" + Long.toUnsignedString(sn.status, 16);
//		case "INTP":
//			return "UHEX-" + Long.toUnsignedString(sn.intp, 16);
//		case "INTCNT":
//			return "DEC-" + Long.toString(sn.intcnt, 10);
//		default:
//			return "HEX-" + Long.toString(sn.intp, 16);
//		}
//	}

//	@Override
//	public int getExitValue() throws DebugException {
//		if (pvm.isAlive()) {
//			IStatus status = new Status(IStatus.ERROR, getClass(), "still running");
//			throw new DebugException(status);
//		}
//		return pvm.exitValue();
//	}

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
		executeCommand(this::blockingResume);
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
		executeCommand(this::blockingSuspend);
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
		executeCommand(this::blockingDisconnect);
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
				return PrimitiveCodeDebugTargetOld.this.launch;
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this;
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
			executeCommand(this::blockingStepInto);
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
			executeCommand(this::blockingStepOver);
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
			executeCommand(this::blockingStepReturn);
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
				stateChangeState = SCS_CHANGE_POSSIBLE;
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
				result[0] = getTopStackFrame();
				if (result.length == 1) {
					return result;
				}
				byte[] bytes = new byte[(result.length - 1) * 8];
				com.getMem(sn.sp - bytes.length, bytes, 0, bytes.length);
				for (int i = 1; i < result.length; i++) {
					final int indexmul8 = i * 8;
					final long addr = sn.sp - indexmul8;
					final int imimul8 = indexmul8 - 8;
					result[i] = new IStackFrame() {

						@Override
						public void terminate() throws DebugException {
							PrimitiveCodeDebugTargetOld.this.terminate();
						}

						@Override
						public boolean isTerminated() {
							return PrimitiveCodeDebugTargetOld.this.isTerminated();
						}

						@Override
						public boolean canTerminate() {
							return PrimitiveCodeDebugTargetOld.this.canTerminate();
						}

						@Override
						public void suspend() throws DebugException {
							PrimitiveCodeDebugTargetOld.this.suspend();
						}

						@Override
						public void resume() throws DebugException {
							PrimitiveCodeDebugTargetOld.this.resume();
						}

						@Override
						public boolean isSuspended() {
							return PrimitiveCodeDebugTargetOld.this.isSuspended();
						}

						@Override
						public boolean canSuspend() {
							return PrimitiveCodeDebugTargetOld.this.canSuspend();
						}

						@Override
						public boolean canResume() {
							return PrimitiveCodeDebugTargetOld.this.canResume();
						}

						@Override
						public void stepReturn() throws DebugException {
							PrimCodeThread.this.stepReturn();
						}

						@Override
						public void stepOver() throws DebugException {
							PrimCodeThread.this.stepOver();
						}

						@Override
						public void stepInto() throws DebugException {
							PrimCodeThread.this.stepInto();
						}

						@Override
						public boolean isStepping() {
							return PrimCodeThread.this.isStepping();
						}

						@Override
						public boolean canStepReturn() {
							return PrimCodeThread.this.canStepReturn();
						}

						@Override
						public boolean canStepOver() {
							return PrimCodeThread.this.canStepOver();
						}

						@Override
						public boolean canStepInto() {
							return PrimCodeThread.this.canStepInto();
						}

						@Override
						public <T> T getAdapter(Class<T> adapter) {
							return findAdapter(adapter, this);
						}

						@Override
						public String getModelIdentifier() {
							return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
						}

						@Override
						public ILaunch getLaunch() {
							return PrimitiveCodeDebugTargetOld.this.getLaunch();
						}

						@Override
						public IDebugTarget getDebugTarget() {
							return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
						}

						@Override
						public boolean hasVariables() throws DebugException {
							return true;
						}

						@Override
						public boolean hasRegisterGroups() throws DebugException {
							return false;
						}

						@Override
						public IVariable[] getVariables() throws DebugException {
							return new IVariable[] { new PrimCodeSimpleStackVariable(addr, bytes, imimul8) };
						}

						@Override
						public IThread getThread() {
							return PrimCodeThread.this;
						}

						@Override
						public IRegisterGroup[] getRegisterGroups() throws DebugException {
							return new IRegisterGroup[0];
						}

						@Override
						public String getName() throws DebugException {
							if (sourcefile == null || getLineNumber() == -1) {
								return "simple stack frame";
							} else {
								return sourcefile.getName();
							}
						}

						@Override
						public int getLineNumber() throws DebugException {
							if (state != STATE_WAITING)
								return -1;
							try {
								AnythingContext ac = getAnythingContextFromStack(bytes, imimul8, addr);
								if (ac == null) {
									return -1;
								} else {
									return getToken(ac, true).getLine();
								}
							} catch (IOException e) {
								throw comunicationError(e);
							}
						}

						@Override
						public int getCharStart() throws DebugException {
							if (state != STATE_WAITING)
								return -1;
							try {
								AnythingContext ac = getAnythingContextFromStack(bytes, imimul8, addr);
								if (ac == null) {
									return -1;
								} else {
									return getToken(ac, true).getStartIndex();
								}
							} catch (IOException e) {
								throw comunicationError(e);
							}
						}

						@Override
						public int getCharEnd() throws DebugException {
							if (state != STATE_WAITING)
								return -1;
							try {
								AnythingContext ac = getAnythingContextFromStack(bytes, imimul8, addr);
								if (ac == null) {
									return -1;
								} else {
									return getToken(ac, false).getStopIndex() + 1;
								}
							} catch (IOException e) {
								throw comunicationError(e);
							}
						}

						private AnythingContext getAnythingContextFromStack(byte[] bytes, final int offset,
								final long addr) throws IOException {
							com.getMem(addr, bytes, offset, 8);
							long val = Convert.convertByteArrToLong(bytes, offset);
							if (val < startAddress || startAddress + binaryLength < val) {
								return null;
							}
							val -= startAddress;
							AnythingContext ac = getAnythingContextFromPosition(val, true);
							return ac;
						}
					};
				}
				return result;
			} catch (IOException e) {
				throw comunicationError(e);
			}
		}

		public class PrimCodeSimpleStackVariable implements IVariable {

			private final long addr;
			private final byte[] bytes;
			private final int index;;
			private long val;;

			private PrimCodeSimpleStackVariable(long addr, byte[] bytes, int index) {
				this.addr = addr;
				this.bytes = bytes;
				this.index = index;
				this.val = Convert.convertByteArrToLong(bytes, this.index);
			}

			@Override
			public boolean verifyValue(IValue value) throws DebugException {
				return verifyValue(value.getValueString());
			}

			@Override
			public boolean verifyValue(String expression) throws DebugException {
				try {
					extractValue(expression);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public boolean supportsValueModification() {
				return true;
			}

			@Override
			public void setValue(IValue value) throws DebugException {
				setValue(value.getValueString());
			}

			@Override
			public void setValue(String expression) throws DebugException {
				try {
					long value = extractValue(expression.toLowerCase()
							.replaceFirst("^\\s*instruction[\\-_\\s]*pointer\\s*:\\s*([^\\s].*])$ ", "$1"));
					Convert.convertLongToByteArr(bytes, index, value);
					com.setMem(addr, bytes, index, 8);
					val = value;
				} catch (IOException e) {
					comunicationError(e);
				}
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public String getModelIdentifier() {
				return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTargetOld.this.getLaunch();
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
			}

			@Override
			public boolean hasValueChanged() throws DebugException {
				try {
					com.getMem(addr, bytes, index, 8);
					long nval = Convert.convertByteArrToLong(bytes, index);
					long oval = val;
					val = nval;
					return oval != nval;
				} catch (IOException e) {
					throw comunicationError(e);
				}
			}

			@Override
			public IValue getValue() throws DebugException {
				return new IValue() {

					@Override
					public <T> T getAdapter(Class<T> adapter) {
						return findAdapter(adapter, this);
					}

					@Override
					public String getModelIdentifier() {
						return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
					}

					@Override
					public ILaunch getLaunch() {
						return PrimitiveCodeDebugTargetOld.this.getLaunch();
					}

					@Override
					public IDebugTarget getDebugTarget() {
						return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
					}

					@Override
					public boolean isAllocated() throws DebugException {
						return true;
					}

					@Override
					public boolean hasVariables() throws DebugException {
						return false;
					}

					@Override
					public IVariable[] getVariables() throws DebugException {
						return new IVariable[0];
					}

					@Override
					public String getValueString() throws DebugException {
						try {
							com.getMem(addr, bytes, index, 8);
							val = Convert.convertByteArrToLong(bytes, index);
							if (val < startAddress || val > startAddress + binaryLength) {
								return "HEX-" + Long.toString(val, 16);
							} else {
								return "instruction-pointer: UHEX-" + Long.toString(val, 16);
							}
						} catch (IOException e) {
							throw comunicationError(e);
						}
					}

					@Override
					public String getReferenceTypeName() throws DebugException {
						return PrimCodeSimpleStackVariable.this.getReferenceTypeName();
					}
				};
			}

			@Override
			public String getReferenceTypeName() throws DebugException {
				try {
					com.getMem(addr, bytes, index, 8);
					val = Convert.convertByteArrToLong(bytes, index);
					if (val < startAddress) {
						return "int64";
					} else if (val > startAddress + binaryLength) {
						return "int64";
					} else {
						return "instruction-pointer";
					}
				} catch (IOException e) {
					throw comunicationError(e);
				}
			}

			@Override
			public String getName() throws DebugException {
				return "simple stack frame variable";
			}
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
			return new IStackFrame() {

				@Override
				public void terminate() throws DebugException {
					PrimitiveCodeDebugTargetOld.this.terminate();
				}

				@Override
				public boolean isTerminated() {
					return PrimitiveCodeDebugTargetOld.this.isTerminated();
				}

				@Override
				public boolean canTerminate() {
					return PrimitiveCodeDebugTargetOld.this.canTerminate();
				}

				@Override
				public void suspend() throws DebugException {
					PrimitiveCodeDebugTargetOld.this.suspend();
				}

				@Override
				public void resume() throws DebugException {
					PrimitiveCodeDebugTargetOld.this.resume();
				}

				@Override
				public boolean isSuspended() {
					return PrimitiveCodeDebugTargetOld.this.isSuspended();
				}

				@Override
				public boolean canSuspend() {
					return PrimitiveCodeDebugTargetOld.this.canSuspend();
				}

				@Override
				public boolean canResume() {
					return PrimitiveCodeDebugTargetOld.this.canResume();
				}

				@Override
				public void stepReturn() throws DebugException {
					PrimCodeThread.this.stepReturn();
				}

				@Override
				public void stepOver() throws DebugException {
					PrimCodeThread.this.stepOver();
				}

				@Override
				public void stepInto() throws DebugException {
					PrimCodeThread.this.stepInto();
				}

				@Override
				public boolean isStepping() {
					return PrimCodeThread.this.isStepping();
				}

				@Override
				public boolean canStepReturn() {
					return PrimCodeThread.this.canStepReturn();
				}

				@Override
				public boolean canStepOver() {
					return PrimCodeThread.this.canStepOver();
				}

				@Override
				public boolean canStepInto() {
					return PrimCodeThread.this.canStepInto();
				}

				@Override
				public <T> T getAdapter(Class<T> adapter) {
					return PrimitiveCodeDebugTargetOld.findAdapter(adapter, this);
				}

				@Override
				public String getModelIdentifier() {
					return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
				}

				@Override
				public ILaunch getLaunch() {
					return PrimitiveCodeDebugTargetOld.this.getLaunch();
				}

				@Override
				public IDebugTarget getDebugTarget() {
					return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
				}

				@Override
				public boolean hasVariables() throws DebugException {
					return true;
				}

				@Override
				public boolean hasRegisterGroups() throws DebugException {
					return true;
				}

				@Override
				public IVariable[] getVariables() throws DebugException {
					try {
						PVMSnapshot sn = com.getSnapshot();
						return new IVariable[] { // @formatter:off 
							new PrimCodeRegister(IP, sn.ip),
							new PrimCodeRegister(SP, sn.ip),
							new PrimCodeRegister(STATUS, sn.ip),
							new PrimCodeRegister(INTP, sn.ip),
							new PrimCodeRegister(INTCNT, sn.ip),
							new PrimCodeXRegs()
					}; // @formatter:on
					} catch (IOException e) {
						comunicationError(e);
						throw new Error();
					}
				}

				@Override
				public IThread getThread() {
					return PrimCodeThread.this;
				}

				@Override
				public IRegisterGroup[] getRegisterGroups() throws DebugException {
					return new IRegisterGroup[] { new PrimCodeRegisterGroup(false), new PrimCodeRegisterGroup(true) };
				}

				@Override
				public String getName() throws DebugException {
					return sourcefile == null ? "highest pvm stack frame" : sourcefile.getName();
				}

				private AnythingContext getAnythingContext() throws DebugException {
					if (state != STATE_WAITING)
						return null;
					long pos = -startAddress;
					try {
						PVMSnapshot sn = com.getSnapshot();
						pos += sn.ip;
					} catch (IOException e) {
						comunicationError(e);
					}
					return getAnythingContextFromPosition(pos, false);
				}

				@Override
				public int getLineNumber() throws DebugException {
					AnythingContext ac = getAnythingContext();
					if (ac == null) {
						return -1;
					} else {
						return getToken(ac, true).getLine();
					}
				}

				@Override
				public int getCharStart() throws DebugException {
					AnythingContext ac = getAnythingContext();
					if (ac == null) {
						return -1;
					} else {
						return getToken(ac, true).getStartIndex();
					}
				}

				@Override
				public int getCharEnd() throws DebugException {
					AnythingContext ac = getAnythingContext();
					if (ac == null) {
						return -1;
					} else {
						return getToken(ac, false).getStopIndex() + 1;
					}
				}
			};
		}

		private static final int IP = 1, SP = 2, STATUS = 3, INTP = 4, INTCNT = 5;

		public class PrimCodeXRegs implements IVariable {

			@Override
			public String getModelIdentifier() {
				return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTargetOld.this.getLaunch();
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public void setValue(String expression) throws DebugException {
				throw new DebugException(new Status(IStatus.ERROR, getClass(), "unsupported operation"));
			}

			@Override
			public void setValue(IValue value) throws DebugException {
				throw new DebugException(new Status(IStatus.ERROR, getClass(), "unsupported operation"));
			}

			@Override
			public boolean supportsValueModification() {
				return false;
			}

			@Override
			public boolean verifyValue(String expression) throws DebugException {
				return false;
			}

			@Override
			public boolean verifyValue(IValue value) throws DebugException {
				return false;
			}

			@Override
			public IValue getValue() throws DebugException {
				return new IValue() {

					@Override
					public <T> T getAdapter(Class<T> adapter) {
						return findAdapter(adapter, this);
					}

					@Override
					public String getModelIdentifier() {
						return PrimCodeXRegs.this.getModelIdentifier();
					}

					@Override
					public ILaunch getLaunch() {
						return PrimCodeXRegs.this.getLaunch();
					}

					@Override
					public IDebugTarget getDebugTarget() {
						return PrimCodeXRegs.this.getDebugTarget();
					}

					@Override
					public boolean isAllocated() throws DebugException {
						return true;
					}

					@Override
					public boolean hasVariables() throws DebugException {
						return true;
					}

					@Override
					public IVariable[] getVariables() throws DebugException {
						try {
							IVariable[] result = new IVariable[251];
							PVMSnapshot sn = com.getSnapshot();
							for (int i = 0; i < 251; i++) {
								result[i] = new PrimCodeSingleXReg(i, sn.x[i]);
							}
							return result;
						} catch (IOException e) {
							throw comunicationError(e);
						}
					}

					@Override
					public String getValueString() throws DebugException {
						return "int64[251]";
					}

					@Override
					public String getReferenceTypeName() throws DebugException {
						return PrimCodeXRegs.this.getReferenceTypeName();
					}
				};
			}

			@Override
			public String getName() throws DebugException {
				return "PVM XNN Register";
			}

			@Override
			public String getReferenceTypeName() throws DebugException {
				return "int64[251]";
			}

			@Override
			public boolean hasValueChanged() throws DebugException {
				return false;
			}

		}

		public class PrimCodeSingleXReg implements IRegister {
			private final int index;
			private long val;

			public PrimCodeSingleXReg(int index, long val) {
				this.index = index;
				this.val = val;
			}

			@Override
			public String getModelIdentifier() {
				return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTargetOld.this.getLaunch();
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public void setValue(String expression) throws DebugException {
				long val = extractValue(expression);
				PVMSnapshot sn;
				try {
					sn = com.getSnapshot();
					sn.x[this.index] = val;
					com.setSnapshot(sn);
					this.val = val;
				} catch (IOException e) {
					comunicationError(e);
				}
			}

			@Override
			public void setValue(IValue value) throws DebugException {
				if (value instanceof PrimCodeSingleXReg) {
					try {
						int other = ((PrimCodeSingleXReg) value).index;
						PVMSnapshot sn = com.getSnapshot();
						sn.x[this.index] = sn.x[other];
						com.setSnapshot(sn);
						this.val = sn.x[other];
					} catch (IOException e) {
						comunicationError(e);
					}
				} else {
					setValue(value.getValueString());
				}
			}

			@Override
			public boolean supportsValueModification() {
				return true;
			}

			@Override
			public boolean verifyValue(String expression) throws DebugException {
				try {
					extractValue(expression);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public boolean verifyValue(IValue value) throws DebugException {
				if (value instanceof PrimCodeSingleXReg) {
					return true;
				} else {
					return verifyValue(value.getValueString());
				}
			}

			@Override
			public IValue getValue() throws DebugException {
				return new IValue() {

					@Override
					public <T> T getAdapter(Class<T> adapter) {
						return findAdapter(adapter, this);
					}

					@Override
					public String getModelIdentifier() {
						return PrimCodeSingleXReg.this.getModelIdentifier();
					}

					@Override
					public ILaunch getLaunch() {
						return PrimCodeSingleXReg.this.getLaunch();
					}

					@Override
					public IDebugTarget getDebugTarget() {
						return PrimCodeSingleXReg.this.getDebugTarget();
					}

					@Override
					public boolean isAllocated() throws DebugException {
						return true;
					}

					@Override
					public boolean hasVariables() throws DebugException {
						return false;
					}

					@Override
					public IVariable[] getVariables() throws DebugException {
						return new IVariable[0];
					}

					@Override
					public String getValueString() throws DebugException {
						try {
							PVMSnapshot sn = com.getSnapshot();
							return sn.x[index] + " : UHEX-" + Long.toUnsignedString(sn.x[index], 16) + " : "
									+ Double.longBitsToDouble(sn.x[index]);
						} catch (IOException e) {
							comunicationError(e);
							throw new Error();
						}
					}

					@Override
					public String getReferenceTypeName() throws DebugException {
						return PrimCodeSingleXReg.this.getReferenceTypeName();
					}
				};
			}

			@Override
			public String getName() throws DebugException {
				String str = Integer.toHexString(index).toUpperCase();
				if (str.length() == 1) {
					str = "0" + str;
				}
				assert str.length() == 2;
				return "X" + str;
			}

			@Override
			public String getReferenceTypeName() throws DebugException {
				return "int64";
			}

			@Override
			public boolean hasValueChanged() throws DebugException {
				try {
					PVMSnapshot sn = com.getSnapshot();
					long old = this.val;
					long newv = sn.x[index];
					this.val = newv;
					return old == newv;
				} catch (IOException e) {
					throw comunicationError(e);
				}
			}

			@Override
			public IRegisterGroup getRegisterGroup() throws DebugException {
				return new PrimCodeRegisterGroup(true);
			}
		}

		public class PrimCodeRegister implements IRegister {

			private final int type;
			private long oldVal;

			public PrimCodeRegister(int type, long oldval) {
				this.type = type;
				this.oldVal = oldval;
			}

			@Override
			public IValue getValue() throws DebugException {
				return new IValue() {

					@Override
					public <T> T getAdapter(Class<T> adapter) {
						return findAdapter(adapter, this);
					}

					@Override
					public String getModelIdentifier() {
						return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
					}

					@Override
					public ILaunch getLaunch() {
						return PrimitiveCodeDebugTargetOld.this.getLaunch();
					}

					@Override
					public IDebugTarget getDebugTarget() {
						return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
					}

					@Override
					public boolean isAllocated() throws DebugException {
						return true;
					}

					@Override
					public boolean hasVariables() throws DebugException {
						return false;
					}

					@Override
					public IVariable[] getVariables() throws DebugException {
						return new IVariable[0];
					}

					@Override
					public String getValueString() throws DebugException {
						try {
							PVMSnapshot sn = com.getSnapshot();
							switch (type) {
							case IP:
								if (sn.ip < 0) {
									return "UHEX-" + Long.toString(sn.ip, 16);
								}
								return "HEX-" + Long.toString(sn.ip, 16);
							case SP:
								if (sn.status < 0) {
									return "UHEX-" + Long.toString(sn.sp, 16);
								}
								return "HEX-" + Long.toString(sn.sp, 16);
							case STATUS:
								if (sn.status < 0) {
									return "UHEX-" + Long.toString(sn.status, 16);
								}
								return "HEX-" + Long.toString(sn.status, 16);
							case INTP:
								if (sn.intp < 0) {
									return "UHEX-" + Long.toString(sn.intp, 16);
								}
								return "HEX-" + Long.toString(sn.intp, 16);
							case INTCNT:
								return "DEC-" + Long.toString(sn.intcnt, 10);
							default:
								throw new InternalError("unknown type: " + type);
							}
						} catch (IOException e) {
							comunicationError(e);
							throw new Error();
						}
					}

					@Override
					public String getReferenceTypeName() throws DebugException {
						return PrimCodeRegister.this.getReferenceTypeName();
					}
				};
			}

			@Override
			public String getName() throws DebugException {
				switch (type) {
				case IP:
					return "IP";
				case SP:
					return "SP";
				case STATUS:
					return "STATUS";
				case INTP:
					return "INTP";
				case INTCNT:
					return "INTCNT";
				default:
					throw new InternalError("unknown type: " + type);
				}
			}

			@Override
			public String getReferenceTypeName() throws DebugException {
				switch (type) {
				case IP:
				case SP:
					return "instruction-pointer";
				case INTP:
					return "instruction-pointer[INTCNT]-pointer";
				case STATUS:
				case INTCNT:
					return "int64";
				default:
					throw new InternalError("unknown type: " + type);
				}
			}

			@Override
			public boolean hasValueChanged() throws DebugException {
				long oldValue = oldVal;
				long newValue = getValueIntern();
				return newValue == oldValue;
			}

			private long getValueIntern() throws InternalError, DebugException, Error {
				long val = getValueInternWithoutSetOldVal();
				oldVal = val;
				return val;
			}

			private long getValueInternWithoutSetOldVal() throws InternalError, DebugException, Error {
				try {
					PVMSnapshot sn = com.getSnapshot();
					switch (type) {
					case IP:
						return sn.ip;
					case SP:
						return sn.sp;
					case STATUS:
						return sn.status;
					case INTP:
						return sn.intp;
					case INTCNT:
						return sn.intcnt;
					default:
						throw new InternalError("unknown type: " + type);
					}
				} catch (IOException e) {
					comunicationError(e);
					throw new Error();
				}
			}

			@Override
			public String getModelIdentifier() {
				return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTargetOld.this.getLaunch();
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public void setValue(String expression) throws DebugException {
				long val = extractValue(expression);
				setValue(val);
			}

			private void setValue(long val) throws DebugException {
				try {
					PVMSnapshot sn = com.getSnapshot();
					switch (type) {
					case IP:
						sn.ip = val;
						break;
					case SP:
						sn.sp = val;
						break;
					case STATUS:
						sn.status = val;
						break;
					case INTP:
						sn.intp = val;
						break;
					case INTCNT:
						sn.intcnt = val;
						break;
					default:
						throw new InternalError("unknown type: " + type);
					}
					com.setSnapshot(sn);
				} catch (IOException e) {
					comunicationError(e);
				}
			}

			@Override
			public void setValue(IValue value) throws DebugException {
				setValue(value.getValueString());
			}

			@Override
			public boolean supportsValueModification() {
				return true;
			}

			@Override
			public boolean verifyValue(String expression) throws DebugException {
				try {
					extractValue(expression);
					return true;
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public boolean verifyValue(IValue value) throws DebugException {
				return verifyValue(value.getValueString());
			}

			@Override
			public IRegisterGroup getRegisterGroup() throws DebugException {
				return new PrimCodeRegisterGroup(false);
			}

		}

		public class PrimCodeRegisterGroup implements IRegisterGroup {

			private final boolean xregs;

			public PrimCodeRegisterGroup(boolean xregs) {
				this.xregs = xregs;
			}

			@Override
			public <T> T getAdapter(Class<T> adapter) {
				return findAdapter(adapter, this);
			}

			@Override
			public String getModelIdentifier() {
				return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
			}

			@Override
			public ILaunch getLaunch() {
				return PrimitiveCodeDebugTargetOld.this.getLaunch();
			}

			@Override
			public IDebugTarget getDebugTarget() {
				return PrimitiveCodeDebugTargetOld.this.getDebugTarget();
			}

			@Override
			public boolean hasRegisters() throws DebugException {
				return true;
			}

			@Override
			public IRegister[] getRegisters() throws DebugException {
				try {
					PVMSnapshot sn = com.getSnapshot();
					if (xregs) {
						IRegister[] result = new IRegister[251];
						for (int i = 0; i < 251; i++) {
							result[i] = new PrimCodeSingleXReg(i, sn.x[i]);
						}
						return result;
					} else {
						return new IRegister[] { // @formatter:off
						new PrimCodeRegister(IP, sn.ip),
						new PrimCodeRegister(SP, sn.ip),
						new PrimCodeRegister(STATUS, sn.ip),
						new PrimCodeRegister(INTP, sn.ip),
						new PrimCodeRegister(INTCNT, sn.ip),
				}; // @formatter:on
					}
				} catch (IOException e) {
					comunicationError(e);
					throw new Error();
				}
			}

			@Override
			public String getName() throws DebugException {
				return xregs ? "PVM XNN Regs" : "PVM SRegs";
			}
		}

		@Override
		public IBreakpoint[] getBreakpoints() {
			return hittedBreaks;
		}

		@Override
		public String getModelIdentifier() {
			return PrimitiveCodeDebugTargetOld.this.getModelIdentifier();
		}

		@Override
		public PrimitiveCodeDebugTargetOld getDebugTarget() {
			return PrimitiveCodeDebugTargetOld.this;
		}

		@Override
		public ILaunch getLaunch() {
			return PrimitiveCodeDebugTargetOld.this.getLaunch();
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			return findAdapter(adapter, this, PrimitiveCodeDebugTargetOld.this, PrimitiveCodeDebugTargetOld.this.com,
					PrimitiveCodeDebugTargetOld.this.pvm);
		}

		@Override
		public boolean canResume() {
			return PrimitiveCodeDebugTargetOld.this.canResume();
		}

		@Override
		public boolean canSuspend() {
			return PrimitiveCodeDebugTargetOld.this.canSuspend();
		}

		@Override
		public boolean isSuspended() {
			return PrimitiveCodeDebugTargetOld.this.isSuspended();
		}

		@Override
		public void resume() throws DebugException {
			PrimitiveCodeDebugTargetOld.this.resume();
		}

		@Override
		public void suspend() throws DebugException {
			PrimitiveCodeDebugTargetOld.this.suspend();
		}

		@Override
		public boolean canTerminate() {
			return PrimitiveCodeDebugTargetOld.this.canTerminate();
		}

		@Override
		public boolean isTerminated() {
			return PrimitiveCodeDebugTargetOld.this.isTerminated();
		}

		@Override
		public void terminate() throws DebugException {
			PrimitiveCodeDebugTargetOld.this.terminate();
		}

		@Override
		public String getName() throws DebugException {
			return "Primitive Virtual Mashine Thread";
		}
	}

	private AnythingContext getAnythingContextFromPosition(long pos, boolean retLast) {
		ParseContext pc = ValidatorDocumentSetupParticipant.getContext(sourcefile);
		AnythingContext last = null;
		for (ParseTree pt : pc.children) {
			if (pt instanceof AnythingContext) {
				AnythingContext ac = (AnythingContext) pt;
				if (ac.command == null && ac.CONSTANT_POOL == null
						|| ac.command != null && ac.command.LABEL_DECLARATION != null) {
					continue;
				}
				if (ac.pos_ >= pos) {
					if (retLast) {
						return last;
					} else {
						return ac;
					}
				}
				last = ac;
			}
		}
		return null;
	}

	public static Token getToken(AnythingContext ac, boolean direction) {
		if (ac.command != null) {
			if (direction) {
				return ac.command.start;
			} else {
				return ac.command.stop;
			}
		} else if (ac.CONSTANT_POOL != null) {
			return ac.CONSTANT_POOL;
		} else {
			throw new InternalError("illegal state/line");
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
	private Error comunicationError(Throwable cause) throws DebugException {
		IStatus s = new Status(IStatus.ERROR, getClass(), "error on cumunicating with the pvm: " + cause.getMessage(),
				cause);
		throw new DebugException(s);
	}

	public void executeCommand(Runnable cmd) {
		new Thread(() -> {
			cmd.run();
			fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
		}, "PVM: Step Over").start();
	}

	public static void fireEvent(DebugEvent... event) {
		DebugPlugin manager = DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(event);
		}
	}

}
