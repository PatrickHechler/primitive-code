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
