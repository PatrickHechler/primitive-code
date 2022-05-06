package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeVariable.PrimCodeVauleType;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PrimitiveCodeStackStackFrame extends PrimitiveCodeStackFrame {

	protected long stackPos;
	protected int len;
	protected List<PrimitiveCodePointedVariable> vars;

	protected PrimitiveCodeStackStackFrame(PrimitiveCodeDebugTarget debug, long stackPos, int len) {
		super(debug);
		this.stackPos = stackPos;
		this.vars = new ArrayList<>();
	}

	@Override
	public void update(List<DebugEvent> fire) {
		updateNeeded(true);
		super.update(fire);
	}

	private void updateNeeded(boolean alsoChildren) {
		int index = 0;
		int len = 0;
		long stack = debug.thread.getStackSize() - stackPos;
		PVMSnapshot sn;
		try {
			sn = debug.thread.com.getSnapshot();
			stack += sn.sp;
		} catch (IOException e) {
			throw new IOError(e);
		}
		for (int off = len - 8; off >= 0; off -= 8, len += 8) {
			PrimitiveCodePointedVariable pv;
			long newAdd = stack + off;
			if (index >= this.vars.size()) {
				pv = new PrimitiveCodePointedVariable(debug, newAdd);
				this.vars.add(pv);
			} else {
				pv = this.vars.get(index);
				long addr = pv.getAddress();
				pv.setAddress(newAdd);
				if (alsoChildren) {
					pv.update();
					if (addr != newAdd) {
						pv.setChanged();
					}
				}
			}
			index++;
			len = 0;
		}

	}

	public void setStackPos(long stackPos, int len) {
		this.stackPos = stackPos;
		this.len = len;
	}

	@Override
	public boolean canStepReturn() {
		return canResume() && stackPos > 0L;
	}

	@Override
	public void blockingStepOver() {
		while (this.debug.thread.getStackSize() > this.stackPos) {
			this.debug.thread.blockingStepReturn();
		}
	}

	@Override
	public void blockingStepReturn() {
		while (this.debug.thread.getStackSize() >= this.stackPos) {
			this.debug.thread.blockingStepReturn();
		}
	}

	@Override
	public IVariable[] getVariables() {
		updateNeeded(false);
		return vars.toArray(new IVariable[vars.size()]);
	}

	@Override
	public AnythingContext getAnythingContext() {
		PrimitiveCodePointedVariable v = this.vars.get(0);
		long addr = v.getLongValue();
		IFile file = debug.getFile(addr);
		return getAnythingContext(addr, false, file);
	}

	public AnythingContext getAnythingContext(long addr, boolean retLast, IFile file) {
		if (file == null) {
			return null;
		}
		addr -= debug.getAddress(file);
		ParseContext pc = ValidatorDocumentSetupParticipant.getContext(file);
		if (pc == null) {
			return null;
		}
		return getAnythingContextFromPosition(addr, retLast, pc);
	}

	@Override
	public IFile getFile() {
		PrimitiveCodePointedVariable v = this.vars.get(0);
		long addr = v.getLongValue();
		return debug.getFile(addr);
	}

	@Override
	public String getName() {
		PrimitiveCodePointedVariable v = this.vars.get(0);
		if (v.getType() == PrimCodeVauleType.pointer) {
			long value = v.getLongValue();
			IFile file = debug.getFile(value);
			return file.getName();
		} else {
			return "Primitive Stack Frame";
		}
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[0];
	}

	@Override
	public boolean hasRegisterGroups() {
		return false;
	}

}
