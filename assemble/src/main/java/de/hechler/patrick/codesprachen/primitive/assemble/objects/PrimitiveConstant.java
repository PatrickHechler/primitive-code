package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.nio.file.Path;

public class PrimitiveConstant {
	
	public final String name;
	public final String comment;
	public final long value;
	public final Path path;
	public final int line;
	
	public PrimitiveConstant(String name, String comment, long value, Path path, int line) {
		this.name = name;
		this.comment = comment;
		this.value = value;
		this.path = path;
		this.line = line;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( (name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (value ^ (value >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PrimitiveConstant other = (PrimitiveConstant) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if ( !name.equals(other.name)) return false;
		if (value != other.value) return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(comment);
		builder.append('\n');
		builder.append(name);
		builder.append(' ');
		builder.append(value);
		return builder.toString();
	}
	
}
