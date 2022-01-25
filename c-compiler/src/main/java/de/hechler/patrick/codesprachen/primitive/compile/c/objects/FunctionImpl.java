package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import java.util.List;

import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;
import de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds.CBlock;

public class FunctionImpl extends FunctionHead {
	
	public final CBlock block;
	
	public FunctionImpl(String name, List <CType> paramTypes, CType returnType, CBlock block) {
		super(name, paramTypes, returnType);
		this.block = block;
	}
	
	@Override
	public boolean canReplace(NameUse other) {
		if (other instanceof FunctionImpl) return this == other;
		else if (other instanceof FunctionHead) return super.equals(other);
		else return false;
	}
	
}
