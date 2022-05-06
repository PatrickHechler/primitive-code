package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

public class PrimitiveCodeRegisterGroup extends PrimitiveCodeNTDE implements IRegisterGroup {

	public PrimitiveCodeRegisterGroup(PrimitiveCodeDebugTarget debug) {
		super(debug);
	}

	@Override
	public String getName() {
		return "PVM Register";
	}

	@Override
	public IRegister[] getRegisters() {
		IRegister[] regs = new IRegister[256];
		for (int index = 0; index < regs.length; index++) {
			regs[index] = new PrimitivCodeRegister(this.debug, this, index);
		}
		return regs;
	}

	@Override
	public boolean hasRegisters() {
		return true;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this);
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return this.debug;
	}

}
