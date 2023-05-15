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
	public SimpleExportable changeRelative(Object relative) {
		return this;
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
		if (!name.equals(other.name)) return false;
		return value == other.value;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("const ");
		if (this.export) {
			b.append("exp ");
		}
		return b.append(this.name).append(" = ").append(this.value).append(';').toString();
	}
	
}
