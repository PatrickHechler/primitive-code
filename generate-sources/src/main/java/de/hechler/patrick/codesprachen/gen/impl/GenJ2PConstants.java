package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;


@SuppressWarnings("javadoc")
public class GenJ2PConstants implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		PrimAsmConstants.export(J2P.CONSTANTS, out);
	}
	
}
