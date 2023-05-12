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


public class GenRunCommandArray implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		int num = -1;
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			int newNum = cmd.num();
			if (newNum <= num) { throw new AssertionError("num=" + num + " cmd=" + cmd); }
			writeIll(out, num, newNum);
			num = newNum;
			out.write("\tc_");
			out.write(cmd.name().toLowerCase());
			out.write(", /* ");
			out.write(GenRunCommandFuncs.toHex(cmd.num()));
			out.write(" */\n");
		}
		writeIll(out, num, 0x10000);
	}
	
	private static void writeIll(Writer out, int num, int newNum) throws IOException {
		for (; num < newNum - 0x1000; num += 0x1000) {
			out.write("\tILL_1000\n");
		}
		for (; num < newNum - 0x100; num += 0x100) {
			out.write("\tILL_100\n");
		}
		for (; num < newNum - 0x10; num += 0x10) {
			out.write("\tILL_10\n");
		}
		for (; num < newNum - 0x1; num += 0x1) {
			out.write("\tILL_1\n");
		}
	}
	
}
