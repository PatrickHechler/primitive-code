package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PrimitivCodeRegister extends PrimitiveCodeVariable implements IRegister {

	public final PrimitiveCodeRegisterGroup regGroup;
	public final int index;

	public PrimitivCodeRegister(PrimitiveCodeDebugTarget debug, PrimitiveCodeRegisterGroup regGroup, int index) {
		super(debug);
		this.regGroup = regGroup;
		this.index = index;
	}

	@Override
	public IRegisterGroup getRegisterGroup() throws DebugException {
		return regGroup;
	}

	@Override
	public void setValue(long value) {
		try {
			PVMSnapshot sn = regGroup.debug.thread.com.getSnapshot();
			sn.setRegister(index, value);
			regGroup.debug.thread.com.setSnapshot(sn);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public long getLongValue() {
		try {
			PVMSnapshot sn = regGroup.debug.thread.com.getSnapshot();
			return sn.getRegister(index);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public String getName() {
		switch (index) {
		case PVMSnapshot.IP_INDEX:
			return "IP";
		case PVMSnapshot.SP_INDEX:
			return "SP";
		case PVMSnapshot.STATUS_INDEX:
			return "STATUS";
		case PVMSnapshot.INTP_INDEX:
			return "INTP";
		case PVMSnapshot.INTCNT_INDEX:
			return "INTCNT";
		default:
			String str = Integer.toString(index - PVMSnapshot.XNN_SUB, 16).toUpperCase();
			if (str.length() == 1) {
				str = "0" + str;
			}
			assert str.length() == 2;
			return "X" + str;
		}
	}

	@Override
	public PrimCodeVauleType getType() {
		switch (index) {
		case PVMSnapshot.IP_INDEX:
		case PVMSnapshot.SP_INDEX:
		case PVMSnapshot.INTP_INDEX:
			return PrimCodeVauleType.pointer;
		default:
			return PrimCodeVauleType.int64;
		}
	}

}
