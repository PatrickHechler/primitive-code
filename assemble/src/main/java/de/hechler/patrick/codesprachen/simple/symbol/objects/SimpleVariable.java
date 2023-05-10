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
			return relative;
		}
		
		public long offset() {
			if (this.addr == -1L) { throw new AssertionError("not yet initilized"); }
			return this.addr;
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
			checkNoInit();
			wantsPointer = true;
		}
		
		public boolean watsPointer() {
			return wantsPointer;
		}
		
		private void checkNoInit() throws AssertionError {
			if (this.reg != -1) { throw new AssertionError("already initilized"); }
		}
		
		private void checkInit() throws AssertionError {
			if (this.reg == -1) { throw new AssertionError("already initilized"); }
		}
		
		public boolean hasOffset() {
			checkInit();
			return offset != -1L;
		}
		
		public long offset() {
			if (offset == -1L) { throw new AssertionError(); }
			return offset;
		}
		
		public int reg() {
			if (reg == -1) { throw new AssertionError(); }
			return reg;
		}
		
		@Override
		public String toExportString() throws IllegalStateException {
			throw new AssertionError();
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
		b.append(type);
		b.append(' ');
		b.append(name);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = prime * result + (export ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleVariable other = (SimpleVariable) obj;
		if (export != other.export) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (type == null) {
			if (other.type != null) return false;
		} else if (!type.equals(other.type)) return false;
		return true;
	}
	
}
