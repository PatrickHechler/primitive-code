package de.patrick.hechler.codesprachen.primitive.assemble.exceptions;

public class AssembleRuntimeException extends RuntimeException {
	
	/** UID */
	private static final long serialVersionUID = -8548990438376299254L;
	
	public final int line;
	public final int posInLine;
	
	public AssembleRuntimeException(int line, int posInLine, String msg) {
		super(msg);
		this.line = line;
		this.posInLine = posInLine;
	}
	
}
