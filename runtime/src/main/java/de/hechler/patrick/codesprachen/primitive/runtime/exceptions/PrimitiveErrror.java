package de.hechler.patrick.codesprachen.primitive.runtime.exceptions;


public class PrimitiveErrror extends Exception {
	
	/** UID */
	private static final long serialVersionUID = 6802182620302479058L;
	
	public final long intNum;
	
	public PrimitiveErrror(long intNum) {
		super();
		this.intNum = intNum;
	}
	
}
