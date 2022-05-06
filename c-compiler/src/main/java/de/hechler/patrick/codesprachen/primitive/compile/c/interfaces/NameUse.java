package de.hechler.patrick.codesprachen.primitive.compile.c.interfaces;

import de.hechler.patrick.codesprachen.primitive.compile.c.exceptions.ReversedReplaceException;

public interface NameUse {
	
	default boolean canReplace(NameUse other) throws ReversedReplaceException {
		return equals(other);
	}
	
}
