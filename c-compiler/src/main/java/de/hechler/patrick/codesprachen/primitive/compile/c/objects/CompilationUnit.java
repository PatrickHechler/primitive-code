package de.hechler.patrick.codesprachen.primitive.compile.c.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hechler.patrick.codesprachen.primitive.compile.c.exceptions.CCompileError;
import de.hechler.patrick.codesprachen.primitive.compile.c.exceptions.ReversedReplaceException;
import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.NameUse;
import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.Sealable;

public class CompilationUnit implements Sealable {
	
	private Map <String, NameUse> usedNames = new HashMap <>();
	private Map <String, CType> enumTypes = new HashMap <>();
	private Map <String, CType> unionTypes = new HashMap <>();
	private Map <String, CType> structTypes = new HashMap <>();
	
	public CompilationUnit() {
	}
	
	
	
	@Override
	public void seal() {
		usedNames = Collections.unmodifiableMap(usedNames);
		enumTypes = Collections.unmodifiableMap(enumTypes);
		unionTypes = Collections.unmodifiableMap(unionTypes);
		structTypes = Collections.unmodifiableMap(structTypes);
	}
	
	public void addAll(NameUse[] nus) {
		for (NameUse nu : nus) {
			add(nu);
		}
	}
	
	public void add(NameUse nu) {
		if (nu instanceof Sealable) {
			((Sealable) nu).seal();
		}
		if (nu instanceof CType) {
			CType type = (CType) nu;
			if (type.isEnum()) {
				type.getEnumConstants().forEach((name, v) -> {
					checkedPut(usedNames, type, name);
				});
				checkedPut(enumTypes, type, type.name);
			} else if (type.isStruct()) {
				checkedPut(structTypes, type, type.name);
			} else if (type.isUnion()) {
				checkedPut(unionTypes, type, type.name);
			} else if (type.isTypedef()) {
				checkedPut(usedNames, type, type.name);
			}
		} else if (nu instanceof FunctionHead) {
			checkedPut(usedNames, nu, ((FunctionHead) nu).name);
		} else if (nu instanceof FunctionHead) {
			checkedPut(usedNames, nu, ((FunctionHead) nu).name);
		} else {
			throw new InternalError("unknown NameUse: " + nu.getClass().getName() + " obj='" + nu + "'");
		}
	}
	
	private <T extends NameUse> void checkedPut(Map <String, T> map, T newNameUser, String name) throws CCompileError {
		T old = map.put(name, newNameUser);
		try {
			if (old != null && !newNameUser.canReplace(old)) {
				throw new CCompileError("[ERROR]: multiple uses of the name: " + name + "\n  first: " + old + "\n  second: " + newNameUser);
			}
		} catch (ReversedReplaceException e) {
			try {
				if ( !old.canReplace(newNameUser)) {
					throw new InternalError("conflict: new says, that the old could replace the new, but the old does not say so!", e);
				}
			} catch (ReversedReplaceException e1) {
				e.addSuppressed(e1);
				throw new InternalError("loop of reverse replaces detected", e);
			}
			old = map.put(name, old);
			if (old != newNameUser) {
				throw new InternalError("this should never happan (does someone tries to confuse me, now I am)", e);
			}
		}
	}
	
	public void eval(CStaticAssert sa) throws CCompileError {
		// TODO Auto-generated method stub
		
	}
	
}
