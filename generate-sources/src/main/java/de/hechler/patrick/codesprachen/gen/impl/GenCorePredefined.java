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
			if (generateClass) {
				out.write("\t/**\n");
				out.write("\t * ");
				start = "\t * ";
			} else {
				out.write("|:  ");
				start = "|   ";
			}
			out.write("<b>" + cnst.name() + "</b>: " + cnst.header() + "<br>\n");
			out.write(start + "value: <code>" + val(cnst) + "</code>\n");
			if (generateClass) {
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
