package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hechler.patrick.codesprachen.primitive.compile.c.exceptions.ReversedReplaceException;
import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;

public class FunctionHead implements NameUse {
	
	public final String name;
	public final List <CType> paramTypes;
	public final CType returnType;
	
	
	
	public FunctionHead(String name, List <CType> paramTypes, CType returnType) {
		this.name = name;
		this.paramTypes = Collections.unmodifiableList(new ArrayList <>(paramTypes));
		this.returnType = returnType;
	}
	
	
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FunctionHead other = (FunctionHead) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if ( !name.equals(other.name)) return false;
		if (paramTypes == null) {
			if (other.paramTypes != null) return false;
		} else if ( !paramTypes.equals(other.paramTypes)) return false;
		if (returnType == null) {
			if (other.returnType != null) return false;
		} else if ( !returnType.equals(other.returnType)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder(returnType.toString()).append(' ').append(name).append('(');
		if (paramTypes.size() > 0) {
			res.append(paramTypes.get(0));
			for (int i = 1; i < paramTypes.size(); i ++ ) {
				res.append(", ").append(paramTypes.get(i));
			}
		}
		return res.append(')').toString();
	}
	
	@Override
	public boolean canReplace(NameUse other) throws ReversedReplaceException {
		if (other instanceof FunctionImpl) {
			if (other.canReplace(this)) {
				throw new ReversedReplaceException();
			}
			return false;
		}
		return NameUse.super.canReplace(other);
	}
	
}
