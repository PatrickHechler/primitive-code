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
package de.hechler.patrick.codesprachen.simple.symbol.objects;

import java.util.List;

import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.SimpleVariable.SimpleOffsetVariable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleFuncType;

@SuppressWarnings("javadoc")
public class SimpleFunctionSymbol implements SimpleExportable {
	
	private long                address;
	private Object              relative;
	public final boolean        export;
	public final String         name;
	public final SimpleFuncType type;
	
	public SimpleFunctionSymbol(boolean export, String name, List<SimpleOffsetVariable> args, List<SimpleOffsetVariable> results) {
		this(-1L, null, export, name, new SimpleFuncType(args, results));
	}
	
	public SimpleFunctionSymbol(long address, Object relative, String name, SimpleFuncType type) {
		this(address, relative, true, name, type);
	}
	
	public SimpleFunctionSymbol(long address, Object relative, boolean export, String name, SimpleFuncType type) {
		this.address  = address;
		this.relative = relative;
		this.export   = export;
		this.name     = name;
		this.type     = type;
	}
	
	@Override
	public boolean isExport() { return this.export; }
	
	@Override
	public String name() {
		return this.name;
	}
	
	public void init(long address, Object relative) {
		if (this.address != -1L) { throw new AssertionError("address is already initilized!"); }
		this.address  = address;
		this.relative = relative;
	}
	
	public Object relative() {
		if (this.address == -1L) { throw new AssertionError("address is not initilized!"); }
		return this.relative;
	}
	
	public long address() {
		if (this.address == -1L) { throw new AssertionError("address is not initilized!"); }
		return this.address;
	}
	
	@Override
	public SimpleExportable changeRelative(Object relative) {
		if (this.address == -1) { throw new AssertionError("address is not initilized!"); }
		if (this.relative == null) { throw new AssertionError("relative is not initilized!"); }
		return new SimpleFunctionSymbol(this.address, relative, this.export, this.name, this.type);
	}
	
	@Override
	public String toExportString() {
		if (!this.export) throw new IllegalStateException("this is not marked as export!");
		if (this.address == -1L) throw new AssertionError("address is not initilized!");
		StringBuilder b = new StringBuilder();
		b.append(FUNC).append(Long.toHexString(this.address).toUpperCase());
		b.append(FUNC).append(this.name);
		this.type.appendToExportStr(b);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		return this.type.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleFunctionSymbol other = (SimpleFunctionSymbol) obj;
		if (!this.type.equals(other.type)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("func ");
		if (this.export) {
			b.append("exp ");
		}
		b.append(this.name);
		b.append(this.type);
		return b.toString();
	}
	
}
