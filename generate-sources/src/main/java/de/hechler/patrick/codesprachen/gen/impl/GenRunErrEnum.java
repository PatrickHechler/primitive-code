package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;


public class GenRunErrEnum implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmConstant cnst : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			if (!cnst.name().startsWith("ERR_")) {
				continue;
			}
			out.write("\tPE_");
			out.write(cnst.name().substring("ERR_".length()));
			out.write("                         ".substring(cnst.name().length()));
			out.write(" = ");
			out.write(Long.toString(cnst.value()));
			out.write(", /* ");
			out.write(cnst.header());
			out.write(" */\n");
		}
	}
	
}
