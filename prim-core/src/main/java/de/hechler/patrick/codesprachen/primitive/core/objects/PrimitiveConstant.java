package de.hechler.patrick.codesprachen.primitive.core.objects;

import java.nio.file.Path;

public record PrimitiveConstant(String name, String comment, long value, Path path, int line) {
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PrimitiveConstant other = (PrimitiveConstant) obj;
		if (!name.equals(other.name)) return false;
		return  value == other.value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (comment != null) {
			builder.append(comment);
			builder.append('\n');
		}
		builder.append(name);
		builder.append('=');
		builder.append(value);
		return builder.toString();
	}
	
}
