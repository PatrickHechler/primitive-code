// This file is part of the Primitive Code Project
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
// Copyright (C) 2023 Patrick Hechler
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.exportVars;

import java.util.List;

import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.SimpleOffsetVariable;

public class SimpleStructType implements SimpleType, SimpleExportable {
	
	public final String                 name;
	public final boolean                export;
	/**
	 * this array should not be modified
	 */
	public final SimpleOffsetVariable[] members;
	
	public SimpleStructType(String name, boolean export, List<SimpleOffsetVariable> members) {
		this(name, export, members.toArray(new SimpleOffsetVariable[members.size()]));
	}
	
	public SimpleStructType(String name, boolean export, SimpleOffsetVariable[] members) {
		this.name    = name;
		this.members = members;
		this.export  = export;
		long off = 0;
		for (SimpleOffsetVariable sv : members) {
			long bc = sv.type.byteCount();
			off = align(off, bc);
			sv.init(off, this);
			off += bc;
		}
	}
	
	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public boolean isPrimitive() { return false; }
	
	@Override
	public boolean isPointerOrArray() { return false; }
	
	@Override
	public boolean isStruct() { return true; }
	
	@Override
	public boolean isPointer() { return false; }
	
	@Override
	public boolean isArray() { return false; }
	
	@Override
	public boolean isFunc() { return false; }
	
	public SimpleOffsetVariable member(String name) {
		for (SimpleOffsetVariable sv : members) {
			if (sv.name.equals(name)) { return sv; }
		}
		throw new IllegalArgumentException("there is no member with the name: " + name);
	}
	
	@Override
	public SimpleExportable changeRelative(Object relative) {
		return this;
	}
	
	@Override
	public long byteCount() {
		if (members.length == 0) return 0;
		SimpleOffsetVariable mem = members[members.length - 1];
		long                 len = mem.offset() + mem.type.byteCount();
		return align(len, len);
	}
	
	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		for (SimpleVariable sv : members) {
			result = prime * result + sv.type.hashCode();
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SimpleType)) return false;
		if (!(obj instanceof SimpleStructType)) return false;
		SimpleStructType other = (SimpleStructType) obj;
		if (members.length != other.members.length) return false;
		for (int i = 0; i < members.length; i++) {
			if (!members[i].type.equals(other.members[i].type)) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("struct ").append(name).append(" { ");
		for (SimpleVariable sv : members) {
			builder.append(sv.type).append(' ').append(sv.name).append("; ");
		}
		builder.append("}");
		return builder.toString();
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		build.append(this.name);
	}
	
	@Override
	public boolean isExport() { return export; }
	
	@Override
	public String toExportString() {
		if (!export) { throw new IllegalStateException("this is not marked as export!"); }
		StringBuilder b = new StringBuilder();
		b.append(STRUCT);
		b.append(this.name);
		b.append(STRUCT);
		exportVars(b, this.members);
		b.append(STRUCT);
		return b.toString();
	}
	
	public static long align(long addr, long bc) {
		int ibc;
		if (bc > 4) ibc = 8;
		else {
			int val = (int) bc;
			switch (val) {
			case 0 -> throw new AssertionError("bc is zero");
			case 1, 2, 4 -> ibc = val;
			case 3 -> ibc = 4;
			default -> ibc = 8;
			}
		}
		int and = ibc - 1;
		if ((addr & and) == 0) return addr;
		return addr + ibc - (addr & and);
	}
	
	public static long align(long addr, int bc) {
		switch (bc) {
		case 0 -> throw new AssertionError("bc is zero");
		case 1, 2, 4 -> {/**/}
		case 3 -> bc = 4;
		default -> bc = 8;
		}
		int and = bc - 1;
		if ((addr & and) == 0) return addr;
		return addr + bc - (addr & and);
	}
	
}
