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
package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import de.hechler.patrick.codesprachen.primitive.assemble.enums.CompilerCommand;

public class CompilerCommandCommand extends Command {

	public final CompilerCommand directive;
	public final long value;

	public CompilerCommandCommand(CompilerCommand directive) {
		this(directive, -1L);
	}

	public CompilerCommandCommand(CompilerCommand directive, long value) {
		super(null, null, null);
		this.directive = directive;
		this.value = value;
	}

	@Override
	public long length() {
		return 0L;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompilerDirectiveCommand [");
		builder.append(directive);
		if (value != -1L) {
			builder.append(", value=");
			builder.append(value);
			builder.append(" : 0x");
			builder.append(Long.toHexString(value));
		}
		builder.append(']');
		return builder.toString();
	}

}
