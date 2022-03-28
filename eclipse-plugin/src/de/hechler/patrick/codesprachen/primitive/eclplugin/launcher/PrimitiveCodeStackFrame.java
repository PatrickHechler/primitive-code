package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;

@Deprecated
public abstract class PrimitiveCodeStackFrame implements IStackFrame {

	protected final PrimitiveCodeDebugTarget debugTarget;

	protected PrimitiveCodeStackFrame(PrimitiveCodeDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
	}

	@Override
	public String getModelIdentifier() {
		return PrimitiveCodeDebugTarget.PVM_MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return debugTarget;
	}

	@Override
	public ILaunch getLaunch() {
		return debugTarget.getLaunch();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return PrimitiveCodeDebugTarget.findAdapter(adapter, this, debugTarget);
	}

	@Override
	public boolean canStepInto() {
		return debugTarget.myThread.canStepInto();
	}

	@Override
	public abstract boolean canStepOver();

	@Override
	public abstract boolean canStepReturn();

	@Override
	public boolean isStepping() {
		return !debugTarget.isSuspended();
	}

	@Override
	public void stepInto() {
		debugTarget.myThread.stepInto();
	}

	@Override
	public void stepOver() {
		debugTarget.executeCommand(this::blockingStepOver, "step over");
	}

	public abstract void blockingStepOver();

	@Override
	public void stepReturn() {
		debugTarget.executeCommand(this::blockingStepReturn, "step return");
	}

	public abstract void blockingStepReturn();

	@Override
	public boolean canResume() {
		return debugTarget.canResume();
	}

	@Override
	public boolean canSuspend() {
		return debugTarget.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return debugTarget.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		debugTarget.resume();
	}

	@Override
	public void suspend() throws DebugException {
		debugTarget.suspend();
	}

	@Override
	public boolean canTerminate() {
		return debugTarget.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return debugTarget.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		debugTarget.terminate();
	}

	@Override
	public IThread getThread() {
		return debugTarget.myThread;
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

	protected AnythingContext getAnythingContextFromPosition(long pos, boolean retLast) {
		pos -= debugTarget.getStartAddress();
		ParseContext pc = ValidatorDocumentSetupParticipant.getContext(debugTarget.sourcefile);
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

	@Override
	public abstract String getName();

	@Override
	public abstract IRegisterGroup[] getRegisterGroups();

	@Override
	public abstract boolean hasRegisterGroups();

}
