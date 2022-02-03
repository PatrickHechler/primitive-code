package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;

public class CVariable implements NameUse {
	
	public final String name;
	public final CType type;
	public final CExpression init;
	
	public CVariable(String name, CType type) {
		this(name, type, null);
		
	}
	
	public CVariable(String name, CType type, CExpression init) {
		this.name = name;
		this.type = type;
		this.init = init;
	}
	
}
