package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.FUNC;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.exportVars;

import java.util.List;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.SimpleOffsetVariable;

public class SimpleFuncType implements SimpleType {
	
	public static final int REG_METHOD_STRUCT = PrimAsmConstants.X_ADD;
	
	private static final SimpleOffsetVariable[] EMPTY_RESULTS = new SimpleOffsetVariable[0];
	
	/**
	 * this array should not be modified
	 */
	public final SimpleOffsetVariable[] arguments;
	/**
	 * this array should not be modified
	 */
	public final SimpleOffsetVariable[] results;
	
	public SimpleFuncType(List<SimpleOffsetVariable> args, List<SimpleOffsetVariable> results) {
		this.arguments = args.toArray(new SimpleOffsetVariable[args.size()]);
		this.results   = results == null ? EMPTY_RESULTS : results.toArray(new SimpleOffsetVariable[results.size()]);
		init(this.arguments);
		init(this.results);
		for (int i = 0; i < this.arguments.length; i++) {
			checkDoubleName(this.arguments, i + 1, this.arguments[i].name);
			checkDoubleName(this.results, 0, this.arguments[i].name);
		}
		for (int i = 0; i < this.results.length; i++) {
			checkDoubleName(this.results, i + 1, this.arguments[i].name);
		}
	}
	
	private static void checkDoubleName(SimpleOffsetVariable[] vals, int startIndex, String name) {
		for (int i = startIndex; i < vals.length; i++) {
			if (vals[i].name.equals(name)) { throw new IllegalArgumentException("same name is used multiple times: " + name); }
		}
	}
	
	private static final void init(SimpleOffsetVariable[] svs) {
		long addr = 0L;
		for (int i = 0; i < svs.length; i++) {
			SimpleOffsetVariable sv = svs[i];
			long                 bc = sv.type.byteCount();
			addr = SimpleStructType.align(addr, bc);
			sv.init(addr);
			addr += bc;
		}
	}
	
	@Override
	public boolean isPrimitive() { return false; }
	
	@Override
	public boolean isPointerOrArray() { return false; }
	
	@Override
	public boolean isPointer() { return false; }
	
	@Override
	public boolean isArray() { return false; }
	
	@Override
	public boolean isStruct() { return true; }
	
	@Override
	public boolean isFunc() { return true; }
	
	public SimpleOffsetVariable member(String name) {
		for (SimpleOffsetVariable sv : arguments) {
			if (sv.name.equals(name)) { return sv; }
		}
		for (SimpleOffsetVariable sv : arguments) {
			if (sv.name.equals(name)) { return sv; }
		}
		throw new IllegalArgumentException("there is no member with the name: " + name);
	}
	
	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		for (SimpleOffsetVariable sv : arguments) {
			result = prime * result + sv.type.hashCode();
		}
		for (SimpleOffsetVariable sv : results) {
			result = prime * result + sv.type.hashCode();
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SimpleFuncType)) return false;
		SimpleFuncType other = (SimpleFuncType) obj;
		if (arguments.length != other.arguments.length) return false;
		if (results.length != other.results.length) return false;
		for (int i = 0; i < arguments.length; i++) {
			if (!arguments[i].type.equals(other.arguments[i].type)) return false;
		}
		for (int i = 0; i < results.length; i++) {
			if (!results[i].type.equals(other.results[i].type)) return false;
		}
		return true;
	}
	
	@Override
	public long byteCount() {
		long bytesArgs    = 0;
		long bytesResults = 0;
		for (int i = 0; i < arguments.length && i < results.length; i++) {
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
			for (int i = 1; i < arguments.length; i++) {
				b.append(", ").append(arguments[i]);
			}
		}
		b.append(") --> <");
		if (results.length > 0) {
			b.append(results[0]);
			for (int i = 1; i < results.length; i++) {
				b.append(", ").append(results[i]);
			}
		}
		return b.append('>').toString();
	}
	
}
