package de.hechler.patrick.codesprachen.simple.symbol.objects;

import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;

public record SimpleConstant(String name, long value, boolean export) implements SimpleExportable {
	
	@Override
	public boolean isExport() {
		return this.export;
	}
	
	@Override
	public String toExportString() {
		if (!export) {
			throw new IllegalStateException("this is not marked as export!");
		}
		StringBuilder b = new StringBuilder();
		b.append(CONST);
		b.append(this.name);
		b.append(CONST);
		b.append(Long.toHexString(this.value));
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		return ((int) value) | (int) (value >> 32);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleConstant other = (SimpleConstant) obj;
		if ( !name.equals(other.name)) return false;
		if (value != other.value) return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("const ");
		if (this.export) {
			b.append("exp ");
		}
		b.append(this.name);
		b.append(" = ");
		b.append(this.value);
		b.append(';');
		return b.toString();
	}
	
}
