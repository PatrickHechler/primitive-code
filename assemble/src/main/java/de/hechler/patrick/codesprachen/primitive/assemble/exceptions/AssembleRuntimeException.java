package de.hechler.patrick.codesprachen.primitive.assemble.exceptions;

public class AssembleRuntimeException extends RuntimeException {
	
	/** UID */
	private static final long serialVersionUID = -8548990438376299254L;

	
	public final int line;
	public final int posInLine;
	public final int length;
	public final int charPos;
	
	public AssembleRuntimeException(int line, int posInLine, int length, int charPos, String msg, Throwable cause) {
		super(msg, cause);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
		this.charPos = charPos;
	}
	
	public AssembleRuntimeException(int line, int posInLine, int length, int charPos, String msg) {
		super(msg);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
		this.charPos = charPos;
	}
	
}
