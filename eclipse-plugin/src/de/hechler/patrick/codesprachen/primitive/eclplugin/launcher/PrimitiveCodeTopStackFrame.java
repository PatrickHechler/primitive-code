package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.IOError;
import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

@Deprecated
public class PrimitiveCodeTopStackFrame extends PrimitiveCodeStackFrame {

	private final PrimCodeRegisterGroup registerGroup = new PrimCodeRegisterGroup();

	public PrimitiveCodeTopStackFrame(PrimitiveCodeDebugTarget debugTarget) {
		super(debugTarget);
	}

	@Override
	public boolean canStepReturn() {
		try {
			long stack = debugTarget.getStackMemory();
			PVMSnapshot sn = debugTarget.getCom().getSnapshot();
			return stack > sn.sp;
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public boolean canStepOver() {
		return debugTarget.canResume();
	}

	@Override
	public void blockingStepOver() {
		debugTarget.myThread.blockingStepOver();
	}

	@Override
	public void blockingStepReturn() {
		debugTarget.myThread.blockingStepReturn();
	}

	@Override
	public IVariable[] getVariables() {
		IVariable[] result = new IVariable[256];
		fillWithRegs(result);
		return result;
	}

	@Override
	public String getName() {
		return "PVM Top Stack Frame";
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[] { registerGroup };
	}

	@Override
	public boolean hasRegisterGroups() {
		return true;
	}

	private void fillWithRegs(IVariable[] result) {
		for (int i = 0; i < 256; i++) {
			result[i] = new PrimCodeRegister(i);
		}
	}

	@Override
	public AnythingContext getAnythingContext() {
		try {
			PVMSnapshot sn = debugTarget.getCom().getSnapshot();
			return getAnythingContextFromPosition(sn.ip, false);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	public class PrimCodeRegisterGroup implements IRegisterGroup {

		@Override
		public String getModelIdentifier() {
			return debugTarget.getModelIdentifier();
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
			return PrimitiveCodeDebugTarget.findAdapter(adapter, this);
		}

		@Override
		public String getName() throws DebugException {
			return "PVM Register Stack Frame";
		}

		@Override
		public IRegister[] getRegisters() throws DebugException {
			IRegister[] result = new IRegister[256];
			fillWithRegs(result);
			return result;
		}

		@Override
		public boolean hasRegisters() throws DebugException {
			return true;
		}

	}

	public class PrimCodeRegister extends PrimitiveCodeVariable implements IRegister {

		private final int reg;

		public PrimCodeRegister(int reg) {
			super(PrimitiveCodeTopStackFrame.this.debugTarget);
			this.reg = reg;
		}

		@Override
		public IRegisterGroup getRegisterGroup() throws DebugException {
			return registerGroup;
		}

		@Override
		public void setValue(long value) {
			try {
				PVMSnapshot sn = debugTarget.getCom().getSnapshot();
				sn.setRegister(reg, value);
			} catch (IOException e) {
				throw new IOError(e);
			}
		}

		@Override
		public long getLongValue() {
			try {
				PVMSnapshot sn = debugTarget.getCom().getSnapshot();
				return sn.getRegister(reg);
			} catch (IOException e) {
				throw new IOError(e);
			}
		}

		@Override
		public String getName() {
			switch (reg) {
			case (int) PVMSnapshot.IP_INDEX:
				return "IP";
			case (int) PVMSnapshot.SP_INDEX:
				return "SP";
			case (int) PVMSnapshot.STATUS_INDEX:
				return "STATUS";
			case (int) PVMSnapshot.INTCNT_INDEX:
				return "INTCNT";
			case (int) PVMSnapshot.INTP_INDEX:
				return "INTP";
			default:
				String str = Integer.toHexString(reg).toUpperCase();
				if (str.length() == 1) {
					str = "0" + str;
				}
				assert str.length() == 2;
				return "X" + str;
			}
		}

		@Override
		public PrimCodeVauleType getType() {
			switch (reg) {
			case (int) PVMSnapshot.IP_INDEX:
			case (int) PVMSnapshot.SP_INDEX:
			case (int) PVMSnapshot.INTP_INDEX:
				return PrimCodeVauleType.pointer;
			default:
				return PrimCodeVauleType.int64;
			}
		}

	}

}
