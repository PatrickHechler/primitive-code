//This file is part of the Patr File System and Code Projects
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
package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;


public class GenErrEnum implements SrcGen {
	
	private final String nameStart;
	private final boolean c;
	
	public GenErrEnum(String nameStart, boolean c) {
		this.nameStart = nameStart;
		this.c = c;
	}
	
	public GenErrEnum(String nameStart) {
		this(nameStart, true);
	}
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmConstant cnst : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			if (!cnst.name().startsWith("ERR_")) {
				continue;
			}
			if (!this.c) {
				out.write("\t/** ");
				out.write(cnst.header());
				out.write(" */\n");
			}
			out.write('\t');
			out.write(this.nameStart);
			out.write(cnst.name().substring("ERR_".length()));
			out.write("                                 ".substring(cnst.name().length()));
			out.write(" = ");
			out.write(Long.toString(cnst.value()));
			if (this.c) {
				out.write(", /* ");
				out.write(cnst.header());
				out.write(" */\n");
			} else {
				out.write(";\n");
			}
		}
	}
	
}
