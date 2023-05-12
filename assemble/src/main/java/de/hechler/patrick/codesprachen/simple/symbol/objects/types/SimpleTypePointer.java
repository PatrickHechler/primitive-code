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

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.POINTER;

public class SimpleTypePointer implements SimpleType {
	
	public final SimpleType target;
	
	public SimpleTypePointer(SimpleType target) {
		this.target = target;
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	@Override
	public boolean isPointerOrArray() {
		return true;
	}
	
	@Override
	public boolean isPointer() {
		return true;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}
	
	@Override
	public boolean isStruct() {
		return false;
	}
	
	@Override
	public boolean isFunc() {
		return false;
	}
	
	@Override
	public long byteCount() {
		return 8;
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		this.target.appendToExportStr(build);
		build.append(POINTER);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(target).append('#');
		return b.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.getClass() == getClass();
	}
	
}
