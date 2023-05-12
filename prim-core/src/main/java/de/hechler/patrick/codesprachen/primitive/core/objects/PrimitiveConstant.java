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
