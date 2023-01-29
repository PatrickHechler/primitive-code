package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;

public class GenCorePrimAsmCmds implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			out.write("\t/**\n");
			out.write("\t * <b>" + cmd.name() + "</b> <code>(" + word(cmd.num(), ' ') + ")</code><br>\n");
			writeParams(out, cmd);
			out.write("\t * ");
			SrcGen.writeJavadocLines(out, "<p>", cmd.general());
			out.write("\t * <p>\n");
			out.write("\t * <b>definition:</b>");
			SrcGen.writeJavadocLines(out, "<br>", cmd.definition());
			out.write("\t */\n");
			out.write("\tpublic static final int " + cmd.name());
			out.write("        ".substring(cmd.name().length()) + "= 0x" + word(cmd.num()) + ";\n");
		}
	}
	
	private static void writeParams(Writer out, PrimAsmReadmeCommand cmd) throws IOException {
		if (cmd.p1() != null) {
			out.write("\t * Parameter: <code>&lt;" + cmd.p1() + "&gt;");
			if (cmd.p2() != null) {
				out.write(" , &lt;" + cmd.p2() + "&gt;");
				if (cmd.p3() != null) {
					out.write(" , &lt;" + cmd.p3() + "&gt;");
				}
			}
			out.write("</code>\n");
		} else {
			out.write("\t * <i>Parameter: none</i>\n");
		}
	}
	
	private static String word(int word, char between) {
		return byteHex(word >>> 8) + between + byteHex(word);
	}
	
	private static String word(int word) {
		return byteHex(word >>> 8) + byteHex(word);
	}
	
	private static String byteHex(int val) {
		String str = Integer.toHexString(0xFF & val);
		if (str.length() == 1) { return "0" + str; }
		return str;
	}
	
}
