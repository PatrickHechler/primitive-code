package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.ARRAY;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.UNKNOWN_SIZE_ARRAY;

public class SimpleTypeArray extends SimpleTypePointer {
	
	public final int elementCount;
	
	public SimpleTypeArray(SimpleType target, int elementCount) {
		super(target);
		this.elementCount = elementCount;
	}
	
	@Override
	public boolean isPointer() {
		return false;
	}
	
	@Override
	public boolean isArray() {
		return true;
	}
	
	@Override
	public int byteCount() {
		if (elementCount == -1) {
			return 0;
		}
		return target.byteCount() * elementCount;
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		this.target.appendToExportStr(build);
		if (this.elementCount == -1) {
			build.append(UNKNOWN_SIZE_ARRAY);
		} else {
			build.append(ARRAY);
			build.append(Long.toHexString(this.elementCount));
			build.append(ARRAY);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(target).append('[');
		if (elementCount != -1) {
			b.append(elementCount);
		}
		return b.append(']').toString();
	}
	
}
