package de.hechler.patrick.codesprachen.primitive.runtime.interfaces.functional;

import de.hechler.patrick.codesprachen.primitive.runtime.exceptions.PrimitiveErrror;

@FunctionalInterface
public interface PVMCommand {
	
	void execute() throws PrimitiveErrror;
	
}
