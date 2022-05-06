package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

public class PrimitiveCodeDebugTarget extends PrimitiveCodeDebugElement implements IDebugTarget {

	public final PrimitiveCodeProcess process;
	public final PrimitiveCodeThread thread;
	public final ILaunch launch;
	private final Map<IFile, Long> knownFiles;
	private final NavigableMap<Long, IFile> knownAddresses;
	private boolean disconnected;

	public PrimitiveCodeDebugTarget(Process process, PVMDebugingComunicator com, PVMDebugingComunicator com2, ILaunch launch, String cmdLine, long launchTime, String dir, String path) {
		this.process = new PrimitiveCodeProcess(this, process, cmdLine, launchTime, dir, path);
		this.thread = new PrimitiveCodeThread(this, com, com2);
		this.launch = launch;
		this.knownFiles = new HashMap<>();
		this.knownAddresses = new TreeMap<>();
		this.disconnected = false;
		new Thread(this.process, "PVM: pvm process").start();
	}

	public Long getAddress(IFile file) {
		return this.knownFiles.get(file);
	}

	public IFile getFile(long address) {
		Entry<Long, IFile> entry = this.knownAddresses.floorEntry(address);
		if (entry == null) {
			return null;
		}
		IFile file = entry.getValue();
		long filepos = entry.getKey();
		ParseContext pc = ValidatorDocumentSetupParticipant.getContext(file);
		if (pc == null) {
			if (filepos == address) {
				return file;
			} else {
				return null;
			}
		} else if (pc.pos + filepos > address) {
			return file;
		}
		return null;
	}

	public void addFile(String absolutefile, long address) {
		IFile file = findSourceFile(absolutefile);
		if (file == null) {
			return;
		}
		addFile(file, address);
	}

	public void addFile(IFile file, long address) {
		this.knownFiles.put(file, address);
		this.knownAddresses.put(address, file);
	}

	public void removeFile(String absolutefile) {
		IFile file = findSourceFile(absolutefile);
		if (file == null) {
			return;
		}
		removeFile(file);
	}

	public void removeFile(IFile file) {
		Long addr = this.knownFiles.remove(file);
		if (addr != null) {
			this.knownAddresses.remove(addr);
		}
	}

	@Override
	public boolean canTerminate() {
		return true;
	}

	@Override
	public boolean isTerminated() {
		return this.thread.isTerminated();
	}

	@Override
	public void terminate() {
		this.thread.terminate();
	}

	@Override
	public boolean canResume() {
		return this.thread.canResume();
	}

	@Override
	public boolean canSuspend() {
		return this.thread.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return this.thread.isSuspended();
	}

	@Override
	public void resume() {
		this.thread.resume();
	}

	@Override
	public void suspend() {
		this.thread.suspend();
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		addBreakpoint(breakpoint);
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		this.thread.removeBreakpoint(breakpoint);
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		this.thread.removeBreakpoint(breakpoint);
		addBreakpoint(breakpoint);
	}

	private void addBreakpoint(IBreakpoint breakpoint) {
		IResource res = breakpoint.getMarker().getResource();
		if (res.getType() != IResource.FILE) {
			return;
		}
		IFile f = (IFile) res;
		Long val = this.knownFiles.get(f);
		if (val == null) {
			return;
		}
		long addr = val;
		if (!(breakpoint instanceof ILineBreakpoint)) {
			return;
		}
		ILineBreakpoint lb = (ILineBreakpoint) breakpoint;
		int ln;
		try {
			ln = lb.getLineNumber();
		} catch (CoreException e) {
			return;
		}
		ParseContext pc = ValidatorDocumentSetupParticipant.getContext(f);
		AnythingContext ac = getAnythingContextFromLine(ln, pc);
		if (ac == null) {
			return;
		}
		this.thread.addBreakpoint(breakpoint, addr + ac.pos_);
	}

	@Override
	public boolean canDisconnect() {
		return !this.disconnected;
	}

	@Override
	public void disconnect() {
		this.thread.com.detach();
		this.disconnected = true;
		this.thread.setDisconnect();
	}

	@Override
	public boolean isDisconnected() {
		return this.disconnected;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return true;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) {
		return new PrimitiveCodeMemoryBlock(this, startAddress, length);
	}

	@Override
	public IProcess getProcess() {
		return this.process;
	}

	@Override
	public IThread[] getThreads() {
		return new IThread[]{this.thread};
	}

	@Override
	public boolean hasThreads() {
		return !isTerminated() && !this.disconnected;
	}

	@Override
	public String getName() {
		return "Primitive Virtual Mashine";
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint instanceof ILineBreakpoint) {
			IResource res = breakpoint.getMarker().getResource();
			if (res.getType() == IResource.FILE) {
				if (this.knownFiles.containsKey((IFile) res)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this, this.thread);
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return this;
	}

}
