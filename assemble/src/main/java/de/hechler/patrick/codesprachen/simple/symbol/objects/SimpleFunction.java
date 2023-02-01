package de.hechler.patrick.codesprachen.simple.symbol.objects;

import java.util.List;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.Command;
import de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable;
import de.hechler.patrick.codesprachen.simple.symbol.objects.types.SimpleFuncType;

public class SimpleFunction implements SimpleExportable {
	
	public boolean                  addrVars;
	public int                      regVars = -1;
	public long                     address = -1L;
	public List <Command>           cmds    = null;
	public final boolean            export;
	public final boolean            main;
	public final String             name;
	public final SimpleFuncType     type;
	
	public SimpleFunction(boolean export, boolean main, String name, List <SimpleVariable> args,
		List <SimpleVariable> results) {
		this( -1L, export, main, name, new SimpleFuncType(args, results));
	}
	
	public SimpleFunction(long address, String name, SimpleFuncType type) {
		this(address, true, false, name, type);
	}
	
	private SimpleFunction(long address, boolean export, boolean main, String name, SimpleFuncType type) {
		this.address = address;
		this.export = export;
		this.main = main;
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
		if ( !export) {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleFunction other = (SimpleFunction) obj;
		if ( !type.equals(other.type))
			return false;
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
