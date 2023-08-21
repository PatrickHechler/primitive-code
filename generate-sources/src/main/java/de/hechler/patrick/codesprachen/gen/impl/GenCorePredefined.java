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

public class GenCorePredefined implements SrcGen {
	
	private final boolean generateClass;
	
	public GenCorePredefined(boolean generateClass) {
		this.generateClass = generateClass;
	}
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmConstant cnst : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			String start;
			if (this.generateClass) {
				out.write("\t/**\n");
				out.write("\t * ");
				start = "\t * ";
			} else {
				out.write("|:  ");
				start = "|   ";
			}
			out.write("<b>" + cnst.name() + "</b>: " + cnst.header() + "<br>\n");
			out.write(start + "value: <code>" + val(cnst) + "</code>\n");
			if (this.generateClass) {
				out.write(start);
				SrcGen.writeJavadocLines(out, "<p>", cnst.docu());
				out.write("\t */\n");
				out.write("\tpublic static final long " + cnst.name() + " = " + val(cnst) + "L;\n");
			} else {
				out.write("|   ");
				SrcGen.writeJavadocLines(out, "|   ", "<p>", cnst.docu());
				out.write("\n" + cnst.name() + "=UHEX-" + Long.toHexString(cnst.value()) + '\n');
			}
		}
	}
	
	private static String val(PrimAsmConstant cnst) throws InternalError {
		return switch (cnst.valType()) {
		case DECIMAL -> Long.toString(cnst.value());
		case HEX, UHEX -> hex(16, cnst.value());
		case NHEX -> "-" + hex(16, -cnst.value()); // min val stays min val, but thats correct for unsigned
		case UHEX_DWORD -> hex(8, cnst.value());
		default -> throw new InternalError("unknown val type: " + cnst.valType().name());
		};
	}
	
	private static String hex(int len, long value) {
		String val = Long.toUnsignedString(value, 16);
		if (val.length() < len) {
			return "0x" + "0".repeat(len - val.length()) + val;
		} else if (val.length() == len) {
			return "0x" + val;
		} else {
			throw new AssertionError();
		}
	}
	
}
