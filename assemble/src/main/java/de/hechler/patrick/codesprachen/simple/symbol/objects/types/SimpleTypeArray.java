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
package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.ARRAY;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.UNKNOWN_SIZE_ARRAY;

public class SimpleTypeArray extends SimpleTypePointer {
	
	public final long elementCount;
	
	public SimpleTypeArray(SimpleType target, long elementCount) {
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
	public long byteCount() {
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
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		return this.target.equals(((SimpleTypeArray) obj).target);
	}
	
}
