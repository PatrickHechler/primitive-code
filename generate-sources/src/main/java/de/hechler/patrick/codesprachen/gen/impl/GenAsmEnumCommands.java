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

public class GenAsmEnumCommands implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		int sum = 0;
		sum += writeCmds(out, ParamType.NO_CONST_PARAM, ParamType.PARAM, ParamType.CONST_PARAM, ", 3, 1),\n");
		sum += writeCmds(out, ParamType.NO_CONST_PARAM, ParamType.NO_CONST_PARAM, null, ", 2, 2),\n");
		sum += writeCmds(out, ParamType.NO_CONST_PARAM, ParamType.PARAM, null, ", 2, 1),\n");
		sum += writeCmds(out, ParamType.PARAM, ParamType.PARAM, null, ", 2, 0),\n");
		sum += writeCmds(out, ParamType.PARAM, ParamType.CONST_PARAM, null, ", 2, 0, 1),\n");
		sum += writeCmds(out, ParamType.NO_CONST_PARAM, ParamType.BYTE_PARAM, null, ", 2, 1, 0, 1),\n");
		sum += writeCmds(out, ParamType.NO_CONST_PARAM, null, null, ", 1, 1),\n");
		sum += writeCmds(out, ParamType.PARAM, null, null, ", 1, 0),\n");
		sum += writeCmds(out, ParamType.LABEL, null, null, ", 1, -1),\n");
		sum += writeCmds(out, null, null, null, ", 0, 0),\n");
		if (sum != SrcGen.PrimAsmReadmeCommand.ALL_CMDS.size()) {
			throw new IllegalStateException("sum=" + sum + " size=" + SrcGen.PrimAsmReadmeCommand.ALL_CMDS.size());
		}
	}
	
	private static int writeCmds(Writer out, ParamType p1, ParamType p2, ParamType p3, String end) throws IOException {
		int cnt = 0;
		out.write("\t\n\t// PARAMS:");
		if (p1 != null) {
			out.write(' ');
			out.write(p1.toString());
			if (p2 != null) {
				out.write(' ');
				out.write(p2.toString());
				if (p3 != null) {
					out.write(' ');
					out.write(p3.toString());
				}
			}
		}
		out.write("\n\t\n");
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			if (cmd.p1() != p1 || cmd.p2() != p2 || cmd.p3() != p3) {
				continue;
			}
			out.write("\tCMD_" + cmd.name() + '(' + cmd.name() + end);
			cnt++;
		}
		return cnt;
	}
	
}
