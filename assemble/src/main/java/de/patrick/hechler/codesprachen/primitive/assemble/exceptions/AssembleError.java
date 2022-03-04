package de.patrick.hechler.codesprachen.primitive.assemble.exceptions;


public class AssembleError extends Error {
	
	/** UID */
	private static final long serialVersionUID = -8548990438376299254L;
	
	public final int line;
	public final int posInLine;
	public final int length;
	
	public AssembleError(int line, int posInLine, int length, String msg, Throwable cause) {
		super(msg, cause);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
	}
	
	public AssembleError(int line, int posInLine, int length, String msg) {
		super(msg);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
	}
	
}
