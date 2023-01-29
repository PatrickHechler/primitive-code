package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;

public class GenCorePredefinedClass implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmConstant cnst : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			out.write("\t/**\n");
			out.write("\t * <b>" + cnst.name() + "</b>: " + cnst.header() + "<br>\n");
			out.write("\t * value: <code>" + val(cnst) + "</code>\n");
			out.write("\t * ");
			SrcGen.writeJavadocLines(out, "<p>", cnst.docu());
			out.write("\t */\n");
			out.write("\tpublic static final long " + cnst.name() + " = " + val(cnst) + "L;\n");
		}
	}
	
	private static String val(PrimAsmConstant cnst) throws InternalError {
		return switch (cnst.valType()) {
		case DECIMAL -> Long.toString(cnst.value());
		case HEX, UHEX -> hex(16, cnst.value());
		case NHEX -> "-" + hex(16, cnst.value());
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
