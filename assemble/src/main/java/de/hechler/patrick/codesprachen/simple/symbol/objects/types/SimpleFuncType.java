//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.FUNC;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.FUNC2;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.exportVars;

import java.util.List;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.SimpleOffsetVariable;

@SuppressWarnings("javadoc")
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
			checkDoubleName(this.results, i + 1, this.results[i].name);
		}
	}
	
	private static void checkDoubleName(SimpleOffsetVariable[] vals, int startIndex, String name) {
		for (int i = startIndex; i < vals.length; i++) {
			if (vals[i].name.equals(name)) { throw new IllegalArgumentException("same name is used multiple times: " + name); }
		}
	}
	
	private final void init(SimpleOffsetVariable[] svs) {
		long addr = 0L;
		for (int i = 0; i < svs.length; i++) {
			SimpleOffsetVariable sv = svs[i];
			long                 bc = sv.type.byteCount();
			addr = SimpleStructType.align(addr, bc);
			sv.init(addr, this);
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
		for (SimpleOffsetVariable sv : this.arguments) {
			if (sv.name.equals(name)) { return sv; }
		}
		for (SimpleOffsetVariable sv : this.arguments) {
			if (sv.name.equals(name)) { return sv; }
		}
		throw new IllegalArgumentException("there is no member with the name: " + name);
	}
	
	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		for (SimpleOffsetVariable sv : this.arguments) {
			result = prime * result + sv.type.hashCode();
		}
		for (SimpleOffsetVariable sv : this.results) {
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
		if (this.arguments.length != other.arguments.length) return false;
		if (this.results.length != other.results.length) return false;
		for (int i = 0; i < this.arguments.length; i++) {
			if (!this.arguments[i].type.equals(other.arguments[i].type)) return false;
		}
		for (int i = 0; i < this.results.length; i++) {
			if (!this.results[i].type.equals(other.results[i].type)) return false;
		}
		return true;
	}
	
	@Override
	public long byteCount() {
		long bytesArgs    = 0;
		long bytesResults = 0;
		for (int i = 0; i < this.arguments.length || i < this.results.length; i++) {
			if (i < this.arguments.length) {
				bytesArgs += this.arguments[i].type.byteCount();
			}
			if (i < this.results.length) {
				bytesResults += this.results[i].type.byteCount();
			}
		}
		return Math.max(bytesArgs, bytesResults);
	}
	
	public long resultByteCount() {
		long bytesResults = 0;
		for (int i = 0; i < this.results.length; i++) {
			bytesResults += this.results[i].type.byteCount();
		}
		return bytesResults;
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		build.append(FUNC);
		exportVars(build, this.arguments);
		build.append(FUNC2);
		exportVars(build, this.results);
		build.append(FUNC);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append('(');
		if (this.arguments.length > 0) {
			b.append(this.arguments[0]);
			for (int i = 1; i < this.arguments.length; i++) {
				b.append(", ").append(this.arguments[i]);
			}
		}
		b.append(") --> <");
		if (this.results.length > 0) {
			b.append(this.results[0]);
			for (int i = 1; i < this.results.length; i++) {
				b.append(", ").append(this.results[i]);
			}
		}
		return b.append('>').toString();
	}
	
}
