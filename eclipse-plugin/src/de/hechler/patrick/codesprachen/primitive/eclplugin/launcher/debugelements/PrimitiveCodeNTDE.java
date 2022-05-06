package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;

public abstract class PrimitiveCodeNTDE extends PrimitiveCodeDebugElement implements IDebugElement {

	public final PrimitiveCodeDebugTarget debug;

	public PrimitiveCodeNTDE(PrimitiveCodeDebugTarget debug) {
		this.debug = debug;
	}

	@Override
	public ILaunch getLaunch() {
		return this.debug.launch;
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return this.debug;
	}

	@Override
	public void executeCommand(Runnable cmd, PrimitiveCodeCommandTypes type) {
		executeCommand(this, this.debug, getName(), cmd, type);
	}

}
