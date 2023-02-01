package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.FUNC;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.exportVars;

import java.util.List;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable;

public class SimpleFuncType implements SimpleType {
	
	public static final int REG_METHOD_STRUCT = PrimAsmConstants.X_ADD;
	
	private static final SimpleVariable[] EMPTY_RESULTS = new SimpleVariable[0];
	
	/**
	 * this array should not be modified
	 */
	public final SimpleVariable[] arguments;
	/**
	 * this array should not be modified
	 */
	public final SimpleVariable[] results;
	
	public SimpleFuncType(List <SimpleVariable> args, List <SimpleVariable> results) {
		this.arguments = args.toArray(new SimpleVariable[args.size()]);
		this.results = results == null ? EMPTY_RESULTS : results.toArray(new SimpleVariable[results.size()]);
		init(this.arguments);
		init(this.results);
	}
	
	private static final void init(SimpleVariable[] svs) {
		long addr = 0L;
		for (int i = 0; i < svs.length; i ++ ) {
			SimpleVariable sv = svs[i];
			int bc = sv.type.byteCount();
			addr = SimpleStructType.align(addr, bc);
			sv.addr = addr;
			sv.reg = REG_METHOD_STRUCT;
			addr += bc;
		}
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	@Override
	public boolean isPointerOrArray() {
		return false;
	}
	
	@Override
	public boolean isPointer() {
		return false;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}
	
	@Override
	public boolean isStruct() {
		return true;
	}
	
	@Override
	public boolean isFunc() {
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (SimpleVariable sv : arguments) {
			result = prime * result + sv.type.hashCode();
		}
		for (SimpleVariable sv : results) {
			result = prime * result + sv.type.hashCode();
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if ( ! (obj instanceof SimpleFuncType)) return false;
		SimpleFuncType other = (SimpleFuncType) obj;
		if (arguments.length != other.arguments.length) return false;
		if (results.length != other.results.length) return false;
		for (int i = 0; i < arguments.length; i ++ ) {
			if ( !arguments[i].type.equals(other.arguments[i].type)) return false;
		}
		for (int i = 0; i < results.length; i ++ ) {
			if ( !results[i].type.equals(other.results[i].type)) return false;
		}
		return true;
	}
	
	@Override
	public int byteCount() {
		int bytesArgs = 0;
		int bytesResults = 0;
		for (int i = 0; i < arguments.length && i < results.length; i ++ ) {
			if (i < arguments.length) {
				bytesArgs += arguments[i].type.byteCount();
			}
			if (i < results.length) {
				bytesResults += results[i].type.byteCount();
			}
		}
		return Math.max(bytesArgs, bytesResults);
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		build.append(FUNC);
		exportVars(build, arguments);
		build.append(FUNC);
		exportVars(build, results);
		build.append(FUNC);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('(');
		if (arguments.length > 0) {
			b.append(arguments[0]);
			for (int i = 1; i < arguments.length; i ++ ) {
				b.append(", ").append(arguments[i]);
			}
		}
		b.append(") --> <");
		if (results.length > 0) {
			b.append(results[0]);
			for (int i = 1; i < results.length; i ++ ) {
				b.append(", ").append(results[i]);
			}
		}
		return b.append('>').toString();
	}
	
}
