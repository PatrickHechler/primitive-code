package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;

public class CVariable implements NameUse {
	
	public final String name;
	public final CType type;
	public final long initValue;
	public final boolean hasInitValue;
	
	public CVariable(String name, CType type) {
		this(name, type, 0L, false);
		
	}
	
	public CVariable(String name, CType type, double initValue) {
		this(name, type, Double.doubleToRawLongBits(initValue), true);
	}
	
	public CVariable(String name, CType type, long initValue) {
		this(name, type, initValue, true);
	}
	
	public CVariable(String name, CType type, long initValue, boolean hasInitValue) {
		this.name = name;
		this.type = type;
		this.initValue = initValue;
		this.hasInitValue = hasInitValue;
	}
	
}
