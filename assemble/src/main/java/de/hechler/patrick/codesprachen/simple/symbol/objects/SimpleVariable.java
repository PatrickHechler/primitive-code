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

import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleType;

public abstract class SimpleVariable implements SimpleExportable {
	
	public static class SimpleOffsetVariable extends SimpleVariable {
		
		public final boolean intern;
		
		private Object relative;
		private long   addr;
		
		public SimpleOffsetVariable(SimpleType type, String name) {
			super(type, name, false);
			this.addr   = -1L;
			this.intern = false;
		}
		
		public SimpleOffsetVariable(SimpleType type, String name, boolean export) {
			super(type, name, export);
			this.addr   = -1L;
			this.intern = false;
		}
		
		public SimpleOffsetVariable(SimpleType type, String name, boolean export, boolean intern) {
			super(type, name, export);
			if (export && intern) { throw new AssertionError("intern and export can't be both set"); }
			this.addr   = -1L;
			this.intern = intern;
		}
		
		public SimpleOffsetVariable(long addr, Object relative, SimpleType type, String name, boolean export) {
			super(type, name, export);
			this.addr     = addr;
			this.relative = relative;
			this.intern   = false;
		}
		
		public void init(long addr, Object relative) {
			if (this.addr != -1) { throw new AssertionError("already initilized"); }
			this.addr     = addr;
			this.relative = relative;
		}
		
		public Object relative() {
			if (this.addr == -1L) { throw new AssertionError("not yet initilized"); }
			return this.relative;
		}
		
		public long offset() {
			if (this.addr == -1L) { throw new AssertionError("not yet initilized"); }
			return this.addr;
		}
		
		@Override
		public SimpleExportable changeRelative(Object relative) {
			if (this.addr == -1L) { throw new AssertionError("not yet initilized"); }
			return new SimpleOffsetVariable(this.addr, relative, this.type, this.name, this.export);
		}
		
		@Override
		public String toExportString() {
			if (this.addr == -1) { throw new AssertionError("not yet initilized"); }
			if (!this.export) { throw new IllegalStateException("this is not marked as export!"); }
			StringBuilder b = new StringBuilder();
			b.append(VAR);
			b.append(Long.toHexString(this.addr).toUpperCase());
			b.append(VAR);
			b.append(this.name);
			b.append(NAME_TYPE_SEP);
			this.type.appendToExportStr(b);
			return b.toString();
		}
		
	}
	
	public static class SimpleFunctionVariable extends SimpleVariable {
		
		private boolean wantsPointer = false;
		private long    offset       = -1L;
		private int     reg          = -1;
		
		public SimpleFunctionVariable(SimpleType type, String name) {
			super(type, name, false);
		}
		
		public void init(long addr, int reg) {
			checkNoInit();
			this.offset = addr;
			this.reg    = reg;
		}
		
		public void setWantsPointer() {
			// after init used by the compiler
			if (this.reg != -1) {
				this.wantsPointer = true;
			}
		}
		
		public boolean watsPointer() {
			return this.wantsPointer;
		}
		
		private void checkNoInit() throws AssertionError {
			if (this.reg != -1) { throw new AssertionError("already initilized"); }
		}
		
		private void checkInit() throws AssertionError {
			if (this.reg == -1) { throw new AssertionError("already initilized"); }
		}
		
		public boolean hasOffset() {
			checkInit();
			return this.offset != -1L;
		}
		
		public long offset() {
			if (this.offset == -1L) { throw new AssertionError(); }
			return this.offset;
		}
		
		public int reg() {
			if (this.reg == -1) { throw new AssertionError(); }
			return this.reg;
		}
		
		@Override
		public String toExportString() throws IllegalStateException {
			throw new AssertionError();
		}
		
		@Override
		public SimpleExportable changeRelative(Object relative) {
			return this;
		}
		
	}
	
	public final SimpleType type;
	public final String     name;
	public final boolean    export;
	
	public SimpleVariable(SimpleType type, String name, boolean export) {
		this.type   = type;
		this.name   = name;
		this.export = export;
	}
	
	@Override
	public boolean isExport() { return this.export; }
	
	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.type);
		b.append(' ');
		b.append(this.name);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = prime * result + (this.export ? 1231 : 1237);
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleVariable other = (SimpleVariable) obj;
		if (this.export != other.export) return false;
		if (this.name == null) {
			if (other.name != null) return false;
		} else if (!this.name.equals(other.name)) return false;
		if (this.type == null) {
			if (other.type != null) return false;
		} else if (!this.type.equals(other.type)) return false;
		return true;
	}
	
}
