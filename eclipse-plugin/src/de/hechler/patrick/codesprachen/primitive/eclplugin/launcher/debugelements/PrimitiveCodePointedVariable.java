package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IVariable;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;

public class PrimitiveCodePointedVariable extends PrimitiveCodeVariable implements IVariable {

	private long address;

	public PrimitiveCodePointedVariable(PrimitiveCodeDebugTarget debug, long address) {
		super(debug);
		this.address = address;
	}

	public void setAddress(long address) {
		this.address = address;
	}
	
	public long getAddress() {
		return address;
	}

	@Override
	public void setValue(long value) {
		try {
			byte[] bytes = new byte[8];
			Convert.convertLongToByteArr(bytes, 0, value);
			debug.thread.com.setMem(this.address, bytes, 0, 8);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public long getLongValue() throws IOError {
		try {
			byte[] bytes = new byte[8];
			debug.thread.com.getMem(this.address, bytes, 0, 8);
			return Convert.convertByteArrToLong(bytes, 0);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public String getName() {
		long addr;
		try {
			addr = getLongValue();
		} catch (IOError e) {
			return "invalid primitive pointed variable";
		}
		IFile file = debug.getFile(addr);
		if (file != null) {
			addr -= debug.getAddress(file);
			ParseContext pc = ValidatorDocumentSetupParticipant.getContext(file);
			if (pc != null) {
				AnythingContext ac = getAnythingContextFromPosition(addr, true, pc);
				if (ac != null) {
					return file.getName() + ": Line: " + getToken(ac, true).getLine();
				}
			}
			return file.getName() + " + HEX-" + Long.toString(addr, 16);
		}
		return "primitive value";
	}

	@Override
	public PrimCodeVauleType getType() {
		long addr = getLongValue();
		IFile file = debug.getFile(addr);
		if (file != null) {
			return PrimCodeVauleType.pointer;
		} else {
			return PrimCodeVauleType.int64;
		}
	}

}
