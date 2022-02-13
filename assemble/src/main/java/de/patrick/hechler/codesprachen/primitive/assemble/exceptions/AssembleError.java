package de.patrick.hechler.codesprachen.primitive.assemble.exceptions;


public class AssembleError extends RuntimeException {
	
	/** UID */
	private static final long serialVersionUID = -8548990438376299254L;
	
	public AssembleError(String msg) {
		super(msg);
	}
}
