// This file is part of the Patr File System and Code Projects
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
// Copyright (C) 2023 Patrick Hechler
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;


@SuppressWarnings("javadoc")
public class GenRunCommandFuncs implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		int lastHeader    = -1;
		int lastSubHeader = -1;
		int callStart     = -1;
		int callEnd       = -1;
		int returnStart   = -1;
		int returnEnd     = -1;
		int lastNum       = -1;
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			int n = cmd.num();
			if (lastHeader != ((n >>> 8) & 0xFF)) {
				lastHeader = n >>> 8;
				String h = SrcGen.PrimAsmReadmeCommand.header(lastHeader);
				out.write("\n/* ");
				out.write(hex(lastHeader >>> 4));
				out.write(hex(0xF & lastHeader));
				out.write(".. : ");
				out.write(h);
				out.write(" */\n");
			}
			if (lastSubHeader != ((n >>> 4) & 0xFFF)) {
				lastSubHeader = n >>> 4;
				String  sh      = SrcGen.PrimAsmReadmeCommand.subHeader(lastSubHeader);
				boolean wroteSH = false;
				if (callStart == -1 && "call".equals(sh)) {
					writeSubHeader(out, lastSubHeader, sh);
					wroteSH   = true;
					callStart = n;
					out.write("#define CALL_COMMANDS_START 0x");
					writeHexWord(out, callStart);
					out.write('\n');
				} else if (callStart != -1 && callEnd == -1) {
					callEnd = lastNum;
					out.write("#define CALL_COMMANDS_COUNT ");
					out.write(Integer.toString(callEnd - callStart + 1));
					out.write('\n');
				}
				if (returnStart == -1 && "return".equals(sh)) {
					writeSubHeader(out, lastSubHeader, sh);
					wroteSH     = true;
					returnStart = n;
					out.write("#define RETURN_COMMANDS_START 0x");
					writeHexWord(out, returnStart);
					out.write('\n');
				} else if (returnStart != -1 && returnEnd == -1) {
					returnEnd = lastNum;
					out.write("#define RETURN_COMMANDS_COUNT ");
					out.write(Integer.toString(returnEnd - returnStart + 1));
					out.write('\n');
				}
				if (!wroteSH) {
					writeSubHeader(out, lastSubHeader, sh);
				}
			}
			out.write("\t/* ");
			writeHexWord(out, n);
			out.write(" */ static void c_");
			out.write(cmd.name().toLowerCase());
			out.write("();\n");
			lastNum = n;
		}
		if (returnEnd == -1 || callEnd == -1) { throw new AssertionError("callEnd=" + callEnd + "  returnEnd=" + returnEnd); }
	}
	
	private static void writeSubHeader(Writer out, int lastSubHeader, String sh) throws IOException {
		out.write("/* ");
		out.write(hex(lastSubHeader >>> 8));
		out.write(hex(0xF & (lastSubHeader >>> 4)));
		out.write(hex(0xF & lastSubHeader));
		out.write(". : ");
		out.write(sh);
		out.write(" */\n");
	}
	
	private static void writeHexWord(Writer out, int n) throws IOException {
		out.write(hex(n >>> 12));
		out.write(hex(0xF & (n >>> 8)));
		out.write(hex(0xF & (n >>> 4)));
		out.write(hex(0xF & n));
	}
	
	public static char[] toHex(int word) {
		if ((word & 0xFFFF) != word) { throw new IllegalArgumentException(Integer.toHexString(word)); }
		char[] res = new char[4];
		res[0] = hex((word >>> 12) & 0xF);
		res[1] = hex((word >>> 8) & 0xF);
		res[2] = hex((word >>> 4) & 0xF);
		res[3] = hex((word >>> 0) & 0xF);
		return res;
	}
	
	private static char hex(int c) {
		if (c <= 9) {
			return (char) ('0' + c);
		}
		return (char) ('A' - 10 + c);
	}
	
}
