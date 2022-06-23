package de.hechler.patrick.codesprachen.primitive.runtime.interfaces;

import java.util.Iterator;

public interface BreakIter extends Iterator <Long> {
	
	/**
	 * returns the next breakpoint
	 * 
	 * @return the next breakpoint
	 * @see #next()
	 */
	long nextBreak();
	
	@Override
	void remove();
	
}
