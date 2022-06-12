package de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;

@FunctionalInterface
public interface Interrupt {
	
	void execute(int intNum) throws PrimitiveErrror;
	
}
