package de.hechler.patrick.codesprachen.primitive.compile.c.exceptions;


public class CCompileError extends RuntimeException {
	
	/** UID */
	private static final long serialVersionUID = 2775910447601191310L;
	
	public CCompileError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public CCompileError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CCompileError(String message) {
		super(message);
	}
	
	public CCompileError(Throwable cause) {
		super(cause);
	}
	
}
