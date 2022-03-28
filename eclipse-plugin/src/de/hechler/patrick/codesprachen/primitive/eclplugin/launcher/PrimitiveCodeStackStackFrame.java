package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.IOError;
import java.io.IOException;

import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

@Deprecated
public class PrimitiveCodeStackStackFrame extends PrimitiveCodeStackFrame {

	private final long stackPos;

	public PrimitiveCodeStackStackFrame(PrimitiveCodeDebugTarget debugTarget, long stackPos) {
		super(debugTarget);
		this.stackPos = stackPos;
	}

	@Override
	public boolean canStepReturn() {
		long stackMemory = debugTarget.getStackMemory();
		if (stackMemory < stackPos) {
			return debugTarget.canResume();
		}
		return false;
	}

	public boolean canStepOver() {
		long stackMemory = debugTarget.getStackMemory();
		if (stackMemory <= stackPos) {
			return debugTarget.canResume();
		}
		return false;
	}

	@Override
	public void blockingStepOver() {
		try {
			PVMDebugingComunicator com = debugTarget.getCom();
			PVMSnapshot sn = com.getSnapshot();
			while (sn.sp < stackPos) {
				debugTarget.topStackFrame.blockingStepReturn();
				sn = com.getSnapshot();
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public void blockingStepReturn() {
		try {
			PVMDebugingComunicator com = debugTarget.getCom();
			PVMSnapshot sn = com.getSnapshot();
			while (sn.sp <= stackPos) {
				debugTarget.topStackFrame.blockingStepReturn();
				sn = com.getSnapshot();
			}
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public IVariable[] getVariables() {
		return new IVariable[] { new PrimitiveCodeVariable(debugTarget) {

			@Override
			public void setValue(long value) {
				try {
					byte[] bytes = new byte[8];
					Convert.convertLongToByteArr(bytes, 0, value);
					debugTarget.getCom().setMem(stackPos, bytes, 0, 8);
					this.oldVal = value;
				} catch (IOException e) {
					throw new IOError(e);
				}
			}

			@Override
			public long getLongValue() {
				try {
					byte[] bytes = new byte[8];
					debugTarget.getCom().getMem(stackPos, bytes, 0, 8);
					long value = Convert.convertByteArrToLong(bytes);
					this.oldVal = value;
					return value;
				} catch (IOException e) {
					throw new IOError(e);
				}
			}

			@Override
			public String getName() {
				return "primitive stack frame value";
			}

			@Override
			public PrimCodeVauleType getType() {
				long value = getLongValue();
				long start = debugTarget.getStartAddress();
				long len = debugTarget.getBinaryLength();
				if (value < start || start + len < value) {
					return PrimCodeVauleType.int64;
				} else {
					return PrimCodeVauleType.pointer;
				}
			}

		} };
	}

	@Override
	public AnythingContext getAnythingContext() {
		return getAnythingContextFromPosition(stackPos, true);
	}

	@Override
	public String getName() {
		int ln = getLineNumber();
		if (ln != -1) {
			return debugTarget.sourcefile.getName() + ": " + ln;
		} else {
			return "primitive stack frame";
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
