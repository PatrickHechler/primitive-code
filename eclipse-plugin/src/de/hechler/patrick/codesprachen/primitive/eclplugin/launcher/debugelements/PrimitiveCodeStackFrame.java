package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;

public abstract class PrimitiveCodeStackFrame extends PrimitiveCodeNTDE implements IStackFrame {

	protected PrimitiveCodeStackFrame(PrimitiveCodeDebugTarget debug) {
		super(debug);
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return debug;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this, debug);
	}

	@Override
	public boolean canStepInto() {
		return debug.thread.canResume();
	}

	@Override
	public boolean canStepOver() {
		return debug.thread.canResume();
	}

	@Override
	public abstract boolean canStepReturn();

	@Override
	public boolean isStepping() {
		return !debug.isSuspended();
	}

	@Override
	public void stepInto() {
		executeCommand(this.debug.thread::blockingStepInto, PrimitiveCodeCommandTypes.Step_Into);
	}

	@Override
	public void stepOver() {
		executeCommand(this::blockingStepOver, PrimitiveCodeCommandTypes.Step_Over);
	}

	public abstract void blockingStepOver();

	@Override
	public void stepReturn() {
		executeCommand(this::blockingStepReturn, PrimitiveCodeCommandTypes.Step_Return);
	}

	public abstract void blockingStepReturn();

	@Override
	public boolean canResume() {
		return debug.canResume();
	}

	@Override
	public boolean canSuspend() {
		return debug.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return debug.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		debug.resume();
	}

	@Override
	public void suspend() throws DebugException {
		debug.suspend();
	}

	@Override
	public boolean canTerminate() {
		return debug.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return debug.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		debug.terminate();
	}

	@Override
	public IThread getThread() {
		return debug.thread;
	}

	@Override
	public abstract IVariable[] getVariables();

	@Override
	public boolean hasVariables() {
		return true;
	}

	@Override
	public int getLineNumber() {
		AnythingContext ac = getAnythingContext();
		if (ac == null) {
			return -1;
		}
		return getToken(ac, true).getLine();
	}

	@Override
	public int getCharStart() {
		AnythingContext ac = getAnythingContext();
		if (ac == null) {
			return -1;
		}
		return getToken(ac, true).getStartIndex();
	}

	@Override
	public int getCharEnd() {
		AnythingContext ac = getAnythingContext();
		if (ac == null) {
			return -1;
		}
		return getToken(ac, false).getStopIndex() + 1;
	}

	public abstract AnythingContext getAnythingContext();

	public abstract IFile getFile();

	@Override
	public abstract String getName();

	@Override
	public abstract IRegisterGroup[] getRegisterGroups();

	@Override
	public abstract boolean hasRegisterGroups();

	public void update(List<DebugEvent> fire) {
		int index = fire.size();
		for (IVariable p : getVariables()) {
			if (p instanceof PrimitiveCodeVariable) {
				((PrimitiveCodeVariable) p).update();
			}
			try {
				if (p.hasValueChanged()) {
					fire.add(new DebugEvent(p, DebugEvent.CHANGE, DebugEvent.CONTENT));
				}
			} catch (DebugException e) {
				fire.add(new DebugEvent(p, DebugEvent.CHANGE, DebugEvent.CONTENT));
			}
		}
		if (!fire.isEmpty()) {
			fire.add(index, new DebugEvent(this, DebugEvent.CHANGE, DebugEvent.CONTENT));
		}
	}

}
