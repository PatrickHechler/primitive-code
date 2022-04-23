package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PrimitiveCodeTopStackFrame extends PrimitiveCodeStackStackFrame {

	private final PrimitiveCodeRegisterGroup regGroup;

	protected PrimitiveCodeTopStackFrame(PrimitiveCodeDebugTarget debug, long stackPos, int len) {
		super(debug, stackPos, len);
		this.regGroup = new PrimitiveCodeRegisterGroup(debug);
	}

	@Override
	public IVariable[] getVariables() {
		IVariable[] vars = super.getVariables();
		IRegister[] regs = this.regGroup.getRegisters();
		int oldlen = vars.length;
		vars = Arrays.copyOf(vars, oldlen + regs.length);
		System.arraycopy(regs, 0, vars, oldlen, regs.length);
		return vars;
	}

	@Override
	public String getName() {
		IFile file = getFile();
		if (file != null) {
			return file.getName();
		}
		return "PVM Top Stack Frame";
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() {
		return new IRegisterGroup[]{this.regGroup};
	}

	@Override
	public boolean hasRegisterGroups() {
		return true;
	}

	@Override
	public AnythingContext getAnythingContext() {
		try {
			PVMSnapshot sn = this.regGroup.debug.thread.com.getSnapshot();
			long addr = sn.ip;
			IFile file = debug.getFile(addr);
			return getAnythingContext(addr, false, file);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public IFile getFile() {
		try {
			PVMSnapshot sn = this.regGroup.debug.thread.com.getSnapshot();
			return debug.getFile(sn.ip);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

}
