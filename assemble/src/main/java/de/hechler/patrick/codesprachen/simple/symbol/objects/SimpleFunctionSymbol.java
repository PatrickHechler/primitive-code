package de.hechler.patrick.codesprachen.simple.symbol.objects;

import java.util.List;

import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleFuncType;

public class SimpleFunctionSymbol implements SimpleExportable {
	
	public long                 address;
	public final boolean        export;
	public final String         name;
	public final SimpleFuncType type;
	
	public SimpleFunctionSymbol(boolean export, String name, List<SimpleVariable> args, List<SimpleVariable> results) {
		this(-1L, export, name, new SimpleFuncType(args, results));
	}
	
	public SimpleFunctionSymbol(long address, String name, SimpleFuncType type) {
		this(address, true, name, type);
	}
	
	public SimpleFunctionSymbol(long address, boolean export, String name, SimpleFuncType type) {
		this.address = address;
		this.export = export;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public boolean isExport() {
		return export;
	}
	
	@Override
	public String name() {
		return this.name;
	}
	
	@Override
	public String toExportString() {
		if (!export) {
			throw new IllegalStateException("this is not marked as export!");
		}
		if (address == -1L) {
			throw new IllegalStateException("address is not initilized!");
		}
		StringBuilder b = new StringBuilder();
		b.append(FUNC);
		b.append(Long.toHexString(this.address).toUpperCase());
		b.append(FUNC);
		b.append(this.name);
		type.appendToExportStr(b);
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		return type.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleFunctionSymbol other = (SimpleFunctionSymbol) obj;
		if (!type.equals(other.type)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("func ");
		if (export) {
			b.append("exp ");
		}
		b.append(name);
		b.append(type);
		return b.toString();
	}
	
}
