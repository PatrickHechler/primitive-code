package de.hechler.patrick.codesprachen.primitive.runtime.exceptions;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmPreDefines;

public class PrimitiveErrror extends Exception {
	
	/** UID */
	private static final long serialVersionUID = 6802182620302479058L;
	
	public final long intNum;
	
	public PrimitiveErrror(long intNum) {
		super();
		this.intNum = intNum;
	}
	
	@Override
	public String getLocalizedMessage() {
		switch ((int) intNum) {
		case (int) PrimAsmPreDefines.INT_ERRORS_ILLEGAL_INTERRUPT:
			return "ILLEGAL_INTERRUPT";
		case (int) PrimAsmPreDefines.INT_ERRORS_UNKNOWN_COMMAND:
			return "UNKNOWN_COMMAND";
		case (int) PrimAsmPreDefines.INT_ERRORS_ILLEGAL_MEMORY:
			return "ILLEGAL_MEMORY";
		case (int) PrimAsmPreDefines.INT_ERRORS_ARITHMETIC_ERROR:
			return "ARITMETIC_ERROR";
		default:
			throw new InternalError("invalid error intNum: " + intNum);
		}
	}
	
}
